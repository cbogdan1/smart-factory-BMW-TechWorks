# ECB Architecture Guide (Entity · Control · Boundary)

Every service in this platform follows the **Entity–Control–Boundary (ECB)** pattern.
It keeps a clear separation between *what talks to the outside world*, *what runs the
business logic*, and *what owns the data* — which maps very naturally onto the
Quarkus + Panache + Kafka + Flyway stack we use.

## The three layers

| Layer        | Package                           | Responsibility                                                                                              | Typical Quarkus building blocks                                                          |
|--------------|-----------------------------------|-------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| **Boundary** | `com.smartfactory.<svc>.boundary` | The *edge* / adapters. Translate external calls into Control invocations and back. Thin, no business rules. | JAX-RS `@Path` resources, Kafka `@Incoming`/`@Outgoing`/`Emitter`, request/response DTOs |
| **Control**  | `com.smartfactory.<svc>.control`  | The *use cases*. Validation, orchestration, transactions, publishing domain events.                         | `@ApplicationScoped` beans, `@Transactional` methods                                     |
| **Entity**   | `com.smartfactory.<svc>.entity`   | The *domain model* + persistence. Owns the service's private schema.                                        | Panache entities/repositories, enums, value objects                                      |

Each service already ships with these three packages scaffolded (see the
`package-info.java` in each). The readiness `PingResource` lives in `boundary`
as a first example.

## The one rule: dependencies point inward

```
Boundary ──▶ Control ──▶ Entity
   (HTTP/Kafka)  (use cases)   (data)
```

- **Boundary** may use **Control** and **Entity**.
- **Control** may use **Entity** — but must **not** import anything from **Boundary**
  (no `HttpServletRequest`, no JAX-RS types, no Kafka records leaking in).
- **Entity** depends on nothing else in the service.

If you ever feel the need to import a `boundary` type inside `control`, that logic
probably belongs in a DTO or should be passed as a plain argument.

## How ECB ties into Flyway

The **Entity** layer owns the schema, and the schema is managed by **Flyway**
(`src/main/resources/db/migration`). Because every service runs with:

```properties
quarkus.hibernate-orm.database.generation=validate
```

Hibernate will **validate** your entities against the Flyway-migrated schema at
startup and **fail fast** if they don't match. So the workflow is always:

1. Add/modify a Panache entity in `entity/`.
2. Add a matching migration `V2__…sql`, `V3__…sql`, … (never edit an applied `V1`).
3. Start the app — Flyway migrates, then Hibernate validates. Green = you're in sync.

## Worked example — an `order` slice

A minimal end-to-end slice in `order-service` showing all three layers.

### 1. Entity — `entity/PurchaseOrder.java`

```java
package com.smartfactory.order.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends PanacheEntity {
    public String customer;
    public String status;   // e.g. NEW, CONFIRMED, CANCELLED
}
```

### 2. Migration — `src/main/resources/db/migration/V2__purchase_order.sql`

```sql
CREATE TABLE purchase_order (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer VARCHAR(120) NOT NULL,
    status   VARCHAR(20)  NOT NULL DEFAULT 'NEW'
);
```

> The table/column names and types must match what Hibernate expects for the entity,
> otherwise `validate` fails at startup. That mismatch is your safety net.

### 3. Control — `control/OrderService.java`

```java
package com.smartfactory.order.control;

import com.smartfactory.order.entity.PurchaseOrder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    @Transactional
    public PurchaseOrder placeOrder(String customer) {
        PurchaseOrder order = new PurchaseOrder();
        order.customer = customer;
        order.status = "NEW";
        order.persist();
        // (later) publish an OrderPlaced event via a Kafka emitter in the boundary
        return order;
    }
}
```

### 4. Boundary — `boundary/OrderResource.java`

```java
package com.smartfactory.order.boundary;

import com.smartfactory.order.control.OrderOrderServiceImpl;
import com.smartfactory.order.entity.PurchaseOrder;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orders;

    @POST
    public PurchaseOrder place(@QueryParam("customer") String customer) {
        return orders.placeOrder(customer);   // delegate — no business logic here
    }
}
```

### Where Kafka fits

Kafka producers/consumers are **Boundary** components (they adapt the service to the
event bus). A consumer receives a record, maps it to a plain argument, and calls a
**Control** method; a producer is injected into a Control method as an `Emitter` and
used to publish domain events. Keep the messaging annotations in `boundary`.

## Checklist for teams

- [ ] New REST endpoint or Kafka adapter → `boundary`
- [ ] New use case / business rule / transaction → `control`
- [ ] New table or field → Panache entity in `entity` **and** a new `V#__…sql` migration
- [ ] No `boundary` imports inside `control`
- [ ] App starts cleanly (Flyway migrates → Hibernate `validate` passes)

