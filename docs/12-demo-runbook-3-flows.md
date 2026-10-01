# Demo Runbook — Happy Path, Rework and Failed Flows

**How this demo is driven**: **Swagger UI** for every REST call, **Kafka UI** for every event.
No terminal needed after startup.

Every step tells you: which **URL to open**, which **operation to expand**, what **body to paste**,
what **response to expect**, and what to **show in Kafka UI**.

---

## Table of contents

1. [Start the stack](#1-start-the-stack)
2. [Open your tabs](#2-open-your-tabs)
3. [How to use Swagger UI](#3-how-to-use-swagger-ui)
4. [How to use Kafka UI](#4-how-to-use-kafka-ui)
5. [Preflight checks](#5-preflight-checks)
6. [Things you must know before demoing](#6-things-you-must-know-before-demoing)
7. [Flow A — Happy path](#7-flow-a--happy-path)
8. [Flow B — Rework](#8-flow-b--rework)
9. [Flow C — Failed / scrap](#9-flow-c--failed--scrap)
10. [Producing Kafka messages by hand](#10-producing-kafka-messages-by-hand)
11. [Troubleshooting](#11-troubleshooting)
12. [Appendix — what had to be fixed](#12-appendix--what-had-to-be-fixed)

---

## 1. Start the stack

One terminal command, then you are done with the terminal.

```bash
cd smart-factory
mvn -DskipTests package
docker compose up -d --build
```

Wait until all containers are healthy:

```bash
docker compose ps
```

For a completely clean slate between rehearsals:

```bash
docker compose down -v && docker compose up -d --build
```

---

## 2. Open your tabs

Open these **9 tabs** before you start and keep them in this order.

| # | Tab | URL |
|---|---|---|
| 1 | **Kafka UI** | <http://localhost:8080> |
| 2 | **Order** Swagger | <http://localhost:8081/q/swagger-ui> |
| 3 | **Planning** Swagger | <http://localhost:8082/q/swagger-ui> |
| 4 | **Inventory** Swagger | <http://localhost:8083/q/swagger-ui> |
| 5 | **Procurement** Swagger | <http://localhost:8084/q/swagger-ui> |
| 6 | **Assembly** Swagger | <http://localhost:8085/q/swagger-ui> |
| 7 | **AGV** Swagger | <http://localhost:8086/q/swagger-ui> |
| 8 | **Quality** Swagger | <http://localhost:8087/q/swagger-ui> |
| 9 | **DWH** Swagger | <http://localhost:8088/q/swagger-ui> |

---

## 3. How to use Swagger UI

Every REST step in this document is the same four clicks:

1. Go to the **tab** named in the step.
2. Find the operation (e.g. `POST /api/v1/orders`) and **click it to expand**.
3. Click the **`Try it out`** button (top right of the expanded box).
4. Paste the **Request body** from this document, then click the blue **`Execute`** button.

The result appears under **Server response** — status code on the left, JSON body on the right.

> **Path parameters** (like `{vehicleId}`) appear as **input boxes above the body**. Paste the value
> there, not into the JSON.

> **Enum fields** (`batteryType`, `priority`, `newStatus`) render as **dropdowns**. If you paste the
> whole JSON body the pasted value wins — that is fine.

---

## 4. How to use Kafka UI

### Reading a topic (you will do this constantly)

1. Tab 1 → left menu **`Topics`**.
2. Click the topic name (e.g. `order-created`).
3. Open the **`Messages`** tab.
4. Set **Seek Type** to `Newest` and press **`Submit`**.
5. Click a row to expand the full JSON.

The **Key** column is always the `vehicleId` — this is how you find *your* vehicle when several
students demo at the same time.

### The 17 business topics

```
order-created            order-status-updated       order-completed
production-planned       planning-kpi-updated
parts-reserved           parts-shortage-detected    inventory-low-stock
parts-delivered          procurement-metrics-updated
material-requested       material-delivered
vehicle-stage-advanced   vehicle-assembled
quality-approved         quality-rework-required    quality-failed
```

### Producing a message by hand

1. Tab 1 → **`Topics`** → click the topic → **`Produce Message`** button (top right).
2. Fill **`Key`** with the `vehicleId` (e.g. `VEH-0001`).
3. Paste the JSON into **`Value`**.
4. Leave headers empty → click **`Produce Message`**.

Ready-made payloads are in [section 10](#10-producing-kafka-messages-by-hand).

---

## 5. Preflight checks

Do these once, before the audience arrives.

### 5.1 Production lines exist

**Tab 3 (Planning)** → `GET /api/v1/plans` → `Try it out` → `Execute`
→ expect `200`. An empty `[]` is fine on a fresh stack.

### 5.2 Parts and stock are seeded

**Tab 4 (Inventory)** → `GET /api/v1/inventory/allInventoryStockRecords` → `Try it out` → `Execute`

Expect `200` with **13** parts in stock. The ones the demo consumes:

| Model | Parts |
|---|---|
| `BMW_I4` | `BAT-LONGRANGE`, `SEAT-STANDARD`, `PAINT-METALLIC`, `WHEEL-19INCH` |
| `BMW_IX` | `BAT-PERFORMANCE`, `SEAT-PREMIUM`, `PAINT-METALLIC`, `WHEEL-21INCH` |
| `BMW_M5` | `ENG-V8`, `BAT-STANDARD`, `SEAT-SPORT`, `PAINT-SOLID`, `WHEEL-20INCH` |

All should have a healthy `availableQuantity`.

> Stock is dimensioned for **~40 builds per model**. Reserved parts are never returned to
> `availableQuantity`, so each demo run consumes stock permanently — if a class has been rehearsing
> for a while, re-check this screen, and `docker compose down -v` to reset.

### 5.3 AGVs are available

**Tab 7 (AGV)** → `GET /api/v1/agvs` → `Try it out` → `Execute`

Expect `AGV-001`, `AGV-002`, `AGV-003` with status **`AVAILABLE`**.

### 5.4 The factory graph reaches every station

**Tab 7 (AGV)** → `POST /api/v1/routes/calculate` → `Try it out`

Paste this body and `Execute`:

```json
{
  "sourceNodeId": "WAREHOUSE-A",
  "targetNodeId": "LINE-1-INTERIOR-STATION"
}
```

Expect `200` with a `path` array and a `totalDistanceMeters`.

> Repeat for a few combinations — swap `LINE-1` for `LINE-2`/`LINE-3` and `INTERIOR` for
> `PAINT`/`POWERTRAIN`. All 9 must return a route. If any fails, the AGV migration `V7` did not
> apply — see [Troubleshooting](#11-troubleshooting).

### 5.5 Quality demo control responds

**Tab 8 (Quality)** → `GET /api/v1/inspections/demo/outcome` → `Try it out` → `Execute`

Expect:

```json
{ "outcome": "RANDOM" }
```

---

## 6. Things you must know before demoing

| Topic | Detail |
|---|---|
| **Vehicle models** | `BMW_I4`, `BMW_IX` and `BMW_M5` all work end to end — each is accepted by Order validation **and** has a BOM in Inventory. Pick a different one per flow to make the demo less repetitive. |
| **Model ↔ battery** | `BMW_I4` → `LONG_RANGE` \| `STANDARD_RANGE` · `BMW_IX` → `LONG_RANGE` \| `PERFORMANCE` · `BMW_M5` → `STANDARD_RANGE`. A wrong pair returns **422**. |
| **Priority** | `LOW`, `NORMAL`, `HIGH`. |
| **Stages** | `CREATED → BODY → PAINT → INTERIOR → POWERTRAIN → FINAL → ASSEMBLED`. From `BODY` that is exactly **5 advance calls**. |
| **AGV missions** | Only `PAINT`, `INTERIOR` and `POWERTRAIN` request material → **3 missions** per vehicle, whatever the model. |
| **Defect catalog** | `PAINT_SCRATCH` 10 pts (PAINT) · `DOOR_ALIGNMENT` 15 pts (INTERIOR) · `MISSING_SEAT` 20 pts (INTERIOR) · `BATTERY_FAILURE` 35 pts (POWERTRAIN). **Score = 100 − penalties.** |
| **Decision thresholds** | `score ≥ 80` → **PASS** · `50–79` → **REWORK** · `< 50` → **FAIL** |
| **Rework budget** | After **3** reworks the vehicle is force-failed regardless of score. |
| **What is manual** | Only **two** things: creating the order, and advancing the assembly stages. Everything else is event-driven. |

### The BOM per model

The BOM is what Inventory reserves when it consumes `production-planned`. Showing this table next to
the `parts-reserved` event makes the reservation step much easier to follow.

| Model | Battery types | BOM (part × qty) |
|---|---|---|
| **`BMW_I4`** | `LONG_RANGE`, `STANDARD_RANGE` | `BAT-LONGRANGE` ×1 · `SEAT-STANDARD` ×4 · `PAINT-METALLIC` ×1 · `WHEEL-19INCH` ×4 |
| **`BMW_IX`** | `LONG_RANGE`, `PERFORMANCE` | `BAT-PERFORMANCE` ×1 · `SEAT-PREMIUM` ×5 · `PAINT-METALLIC` ×1 · `WHEEL-21INCH` ×4 |
| **`BMW_M5`** | `STANDARD_RANGE` | `ENG-V8` ×1 · `BAT-STANDARD` ×1 · `SEAT-SPORT` ×4 · `PAINT-SOLID` ×1 · `WHEEL-20INCH` ×4 |

> `BMW_X5` also has a BOM but there is **no validator** for it in Order, so it cannot be ordered.
> Requesting it returns **422**.


### The pipeline in one picture

```
POST /orders                    (manual)
   └── order-created ──────────► Planning  ─ creates plan, assigns least-loaded line
          └── production-planned ─┬────────► Inventory ─ explodes BOM, reserves parts
                                  └────────► Assembly  ─ creates VehicleProduction (CREATED)
                 └── parts-reserved ────────► Assembly ─ CREATED → BODY, status IN_PRODUCTION

POST /vehicles/{id}/advance     (manual, ×5)
   └── vehicle-stage-advanced ──► Planning  ─ tracks plan stage
   └── material-requested ──────► AGV       ─ picks AGV, Dijkstra route, auto-completes mission
          └── material-delivered ──────────► Assembly / Inventory / DWH
   └── vehicle-assembled ───────┬──────────► Quality  ─ inspects, scores, decides
                                ├──────────► Planning ─ completes plan, frees line
                                └──────────► Order    ─ moves order to QUALITY_CHECK

Quality decision (automatic):
   score ≥ 80  → quality-approved        ──► Order    ─ COMPLETED  (+ order-completed)
   50 ≤ s < 80 → quality-rework-required ──► Assembly ─ rewind to targetStage
                                         ──► Order    ─ REWORK_REQUIRED
   score < 50  → quality-failed          ──► Order    ─ FAILED

DWH consumes every topic and builds the vehicle twin + timeline.
```

---

## 7. Flow A — Happy path

> **Story**: order → plan → parts reserved → 5 assembly stages with AGV deliveries → clean
> inspection → order **COMPLETED**.

### Step A0 — Pin the inspection outcome to a pass

> Do this **before** the vehicle reaches `ASSEMBLED`. The seeder is otherwise random.

**Tab 8 (Quality)** → `PUT /api/v1/inspections/demo/outcome/{outcome}` → `Try it out`

In the **`outcome`** path parameter box type:

```
CLEAN
```

→ `Execute`. Expect `200`:

```json
{ "outcome": "CLEAN" }
```

> `CLEAN` = no defects at all → score 100 → PASS.
> Use `PASS` instead if you want one or two harmless defects visible in the report (score 80–90).

---

### Step A1 — Create the order

**Tab 2 (Order)** → `POST /api/v1/orders` → `Try it out`

**Request body** — paste exactly:

```json
{
  "customerName": "John Smith",
  "customerEmail": "john.smith@example.com",
  "vehicleModel": "BMW_I4",
  "color": "ALPINE_WHITE",
  "batteryType": "LONG_RANGE",
  "priority": "HIGH"
}
```

→ `Execute`. Expect **`201`**:

```json
{
  "orderId": "ORD-...",
  "vehicleId": "VEH-...",
  "customerName": "John Smith",
  "vehicleModel": "BMW_I4",
  "color": "ALPINE_WHITE",
  "batteryType": "LONG_RANGE",
  "priority": "HIGH",
  "status": "CREATED"
}
```

> ✏️ **Write down `orderId` and `vehicleId`.** You need them in almost every following step.

**Show in Kafka UI**: Tab 1 → `Topics` → **`order-created`** → `Messages` → newest.
Point at the envelope: `eventId`, `eventType`, `correlationId` (= your `vehicleId`) and `payload`.

---

### Step A2 — Watch the chain react on its own

Nothing to click. Within a second or two three services have already reacted.

**Show in Kafka UI**, in this order:

| Topic | Produced by | Say this |
|---|---|---|
| **`production-planned`** | planning | "Planning consumed `order-created`, created a plan and assigned the least loaded line — see `productionLine`." |
| **`parts-reserved`** | inventory | "Inventory exploded the `BMW_I4` BOM and locked the parts — see `reservedParts`." |

Now verify in Swagger:

**Tab 3 (Planning)** → `GET /api/v1/plans` → `Execute`
→ find your `vehicleId`; `status` should be `PLANNED`, `currentStage` `CREATED`.

**Tab 4 (Inventory)** → `GET /api/v1/inventory/allInventoryStockRecords` → `Execute`
→ `availableQuantity` went **down**, `reservedQuantity` went **up**.

**Tab 6 (Assembly)** → `GET /api/v1/vehicles/{vehicleId}` → `Try it out`
→ paste your `vehicleId` into the path box → `Execute`. Expect:

```json
{
  "vehicleId": "VEH-...",
  "currentStage": "BODY",
  "status": "IN_PRODUCTION",
  "reworkCount": 0
}
```

> If `currentStage` is still `CREATED`, `parts-reserved` has not arrived yet — wait a moment and
> re-execute.

---

### Step A3 — Advance through the 5 stages

**Tab 6 (Assembly)** → `POST /api/v1/vehicles/{id}/advance` → `Try it out`

Paste your `vehicleId` into the **`id`** path box. **There is no request body.**

Click **`Execute` five times**, pausing after each one.

| Call | Transition | Events produced |
|---|---|---|
| 1 | `BODY` → `PAINT` | `vehicle-stage-advanced`, `material-requested` (PAINT_KIT) |
| 2 | `PAINT` → `INTERIOR` | `vehicle-stage-advanced`, `material-requested` (SEAT_SET) |
| 3 | `INTERIOR` → `POWERTRAIN` | `vehicle-stage-advanced`, `material-requested` (BATTERY_PACK) |
| 4 | `POWERTRAIN` → `FINAL` | `vehicle-stage-advanced` |
| 5 | `FINAL` → `ASSEMBLED` | `vehicle-stage-advanced`, **`vehicle-assembled`** |

Each response looks like:

```json
{
  "vehicleId": "VEH-...",
  "currentStage": "PAINT",
  "status": "IN_PRODUCTION"
}
```

**Show in Kafka UI after calls 1–3**:

* **`material-requested`** — point at `targetNode` (e.g. `LINE-2-PAINT-STATION`) and `material`.
* **`material-delivered`** — point at `missionId` and `agvId`, and stress that this appeared
  **without anybody clicking anything**: the AGV service consumed the request, ran Dijkstra,
  assigned a robot and completed the mission.

**Show in Swagger (optional, between calls)**:
**Tab 7 (AGV)** → `GET /api/v1/agvs` → `Execute` → watch an AGV flip to `BUSY` and back to
`AVAILABLE`.

---

### Step A4 — Quality decides automatically

The 5th advance published `vehicle-assembled`, which woke up **three** services at once.

**Show in Kafka UI**, in this order:

1. **`vehicle-assembled`** — "assembly is done, this is the trigger".
2. **`quality-approved`** — "Quality created an inspection, seeded defects, scored it and decided
   `PASS` — all automatic. Note the `score`."
3. **`order-status-updated`** — "the order walked `CREATED → PLANNED → IN_PRODUCTION →
   QUALITY_CHECK`, one event per hop."
4. **`order-completed`** — "and `quality-approved` closed the order."
5. **`planning-kpi-updated`** — "Planning released the line capacity."

Now verify in Swagger:

**Tab 8 (Quality)** → `GET /api/v1/inspections/history` → `Try it out`
→ put your `vehicleId` into the **`vehicleId`** query box → `Execute`.

Expect one inspection:

```json
[
  {
    "vehicleId": "VEH-...",
    "score": 100,
    "decision": "PASS",
    "status": "APPROVED",
    "targetStage": null,
    "defects": []
  }
]
```

**Tab 2 (Order)** → `GET /api/v1/orders/{id}` → `Try it out`
→ paste your `orderId` → `Execute`:

```json
{
  "orderId": "ORD-...",
  "status": "COMPLETED",
  "completionDate": "2026-08-19T..."
}
```

---

### Step A5 — Show the digital twin

**Tab 9 (DWH)** → `GET /api/v1/twins/{vehicleId}` → `Try it out` → paste `vehicleId` → `Execute`.

**Tab 9 (DWH)** → `GET /api/v1/timeline/{vehicleId}` → `Try it out` → paste `vehicleId` → `Execute`.

The timeline should contain, in order:

```
order-created
order-status-updated  (CREATED → PLANNED)
production-planned
parts-reserved
order-status-updated  (PLANNED → IN_PRODUCTION)
vehicle-stage-advanced × 5
material-requested / material-delivered × 3
vehicle-assembled
order-status-updated  (IN_PRODUCTION → QUALITY_CHECK)
quality-approved
order-completed
```

**Tab 9 (DWH)** → `GET /api/v1/dashboard/kpis` → `Execute` → show the aggregate numbers.

### ✅ Success criteria

* Order status `COMPLETED` with a `completionDate`
* Assembly stage `ASSEMBLED`, `reworkCount` = 0
* Exactly one inspection, decision `PASS`
* DWH timeline complete
* No messages in any `*-dlq` topic

---

## 8. Flow B — Rework

> **Story**: same pipeline, but the inspection finds **fixable** defects → the vehicle is rewound to
> an earlier stage → re-assembled → second inspection passes → order **COMPLETED**.

Use a **brand new order** so the timeline tells the story cleanly.

### Step B0 — Force a REWORK outcome

**Tab 8 (Quality)** → `PUT /api/v1/inspections/demo/outcome/{outcome}` → `Try it out`

`outcome` path box:

```
REWORK
```

→ `Execute`. Expect `{ "outcome": "REWORK" }`.

---

### Step B1 — Create the order

**Tab 2 (Order)** → `POST /api/v1/orders` → `Try it out`

```json
{
  "customerName": "Alex Popescu",
  "customerEmail": "alex.popescu@example.com",
  "vehicleModel": "BMW_IX",
  "color": "FROZEN_GREY",
  "batteryType": "PERFORMANCE",
  "priority": "NORMAL"
}
```

→ `Execute`. ✏️ **Write down the new `orderId` and `vehicleId`.**

> A different model on purpose — watch `parts-reserved` in Kafka UI and note that Inventory now
> reserves the **iX** recipe (`BAT-PERFORMANCE`, `SEAT-PREMIUM` ×5, `WHEEL-21INCH` ×4), not the i4 one.

---

### Step B2 — Run assembly to the end

**Tab 6 (Assembly)** → `POST /api/v1/vehicles/{id}/advance` → `Try it out` → paste `vehicleId` →
**`Execute` five times** (exactly as in Step A3).

---

### Step B3 — The rework decision

Quality seeded a defect combination worth 21–50 penalty points — typically
`PAINT_SCRATCH` (10) + `DOOR_ALIGNMENT` (15) → **score 75** → **REWORK** — and published
**`quality-rework-required`** with `targetStage` = the earliest affected stage.

**Show in Kafka UI**: `quality-rework-required` → point at `score`, `targetStage` and `reason`.

Two services reacted:

**Tab 8 (Quality)** → `GET /api/v1/inspections/history` → `vehicleId` query box → `Execute`:

```json
[
  {
    "score": 75,
    "decision": "REWORK",
    "status": "REWORK_REQUIRED",
    "targetStage": "PAINT",
    "defects": [
      { "defectCode": "PAINT_SCRATCH",  "penaltyPoints": 10 },
      { "defectCode": "DOOR_ALIGNMENT", "penaltyPoints": 15 }
    ]
  }
]
```

**Tab 6 (Assembly)** → `GET /api/v1/vehicles/{vehicleId}` → `Execute`:

```json
{
  "currentStage": "PAINT",
  "status": "IN_PRODUCTION",
  "reworkCount": 1
}
```

> 🎯 **This is the money shot.** The vehicle jumped **backwards** from `ASSEMBLED` to `PAINT` and
> `reworkCount` went to 1 — nobody clicked anything, Assembly reacted to the Kafka event.

**Tab 2 (Order)** → `GET /api/v1/orders/{id}` → `Execute` → `"status": "REWORK_REQUIRED"`.

---

### Step B4 — Flip the outcome so the retry succeeds

**Tab 8 (Quality)** → `PUT /api/v1/inspections/demo/outcome/{outcome}` → `outcome` = `CLEAN` →
`Execute`.

> If you skip this the vehicle is reworked again. After **3** reworks Quality force-fails it —
> which is a perfectly good alternative ending, see [Flow C](#9-flow-c--failed--scrap).

---

### Step B5 — Re-run the remaining stages

**Tab 6 (Assembly)** → `POST /api/v1/vehicles/{id}/advance` → `Execute` repeatedly until
`currentStage` is `ASSEMBLED`.

How many clicks depends on `targetStage`:

| `targetStage` | Clicks needed |
|---|---|
| `PAINT` | 4 |
| `INTERIOR` | 3 |
| `POWERTRAIN` | 2 |

**Show in Kafka UI**: `material-requested` / `material-delivered` fire **again** for the repeated
stages — the AGVs redo the deliveries.

---

### Step B6 — Second inspection passes, order completes

**Tab 8 (Quality)** → `GET /api/v1/inspections/history` → `vehicleId` query box → `Execute`

Now **two** inspections for the same vehicle:

```json
[
  { "score": 75,  "decision": "REWORK", "targetStage": "PAINT" },
  { "score": 100, "decision": "PASS",   "targetStage": null }
]
```

**Tab 2 (Order)** → `GET /api/v1/orders/{id}` → `Execute` → `"status": "COMPLETED"`.

**Tab 9 (DWH)** → `GET /api/v1/timeline/{vehicleId}` → `Execute` → show the stage events appearing
**twice** for the reworked stages, and the order status path:

```
… → QUALITY_CHECK → REWORK_REQUIRED → QUALITY_CHECK → COMPLETED
```

### ✅ Success criteria

* Two inspections: first `REWORK`, second `PASS`
* `reworkCount` = 1 on the assembly record
* Order path includes `REWORK_REQUIRED` and ends `COMPLETED`
* Timeline shows the repeated stage + AGV events

---

## 9. Flow C — Failed / scrap

> **Story**: the inspection finds **critical** defects → score below 50 → the vehicle is scrapped and
> the order is terminated as **FAILED**.

### Step C0 — Force a FAIL outcome

**Tab 8 (Quality)** → `PUT /api/v1/inspections/demo/outcome/{outcome}` → `outcome` = `FAIL` →
`Execute`. Expect `{ "outcome": "FAIL" }`.

---

### Step C1 — Create the order

**Tab 2 (Order)** → `POST /api/v1/orders` → `Try it out`

```json
{
  "customerName": "Maria Ionescu",
  "customerEmail": "maria.ionescu@example.com",
  "vehicleModel": "BMW_M5",
  "color": "BLACK_SAPPHIRE",
  "batteryType": "STANDARD_RANGE",
  "priority": "HIGH"
}
```

→ `Execute`. ✏️ **Write down `orderId` and `vehicleId`.**

> Third model, third recipe — `parts-reserved` now shows the **M5** BOM
> (`ENG-V8`, `BAT-STANDARD`, `SEAT-SPORT` ×4, `WHEEL-20INCH` ×4).
> `BMW_M5` only accepts `STANDARD_RANGE`; any other battery returns **422**.

---

### Step C2 — Run assembly to the end

**Tab 6 (Assembly)** → `POST /api/v1/vehicles/{id}/advance` → paste `vehicleId` →
**`Execute` five times**.

---

### Step C3 — Quality scraps the vehicle

Quality seeded a combination worth more than 50 penalty points — typically
`BATTERY_FAILURE` (35) + `MISSING_SEAT` (20) → **score 45** → **FAIL** — and published
**`quality-failed`**.

**Show in Kafka UI**: `quality-failed` → point at `score` and `defectCodes`.

**Tab 8 (Quality)** → `GET /api/v1/inspections/history` → `vehicleId` query box → `Execute`:

```json
[
  {
    "score": 45,
    "decision": "FAIL",
    "status": "FAILED",
    "targetStage": null,
    "defects": [
      { "defectCode": "BATTERY_FAILURE", "penaltyPoints": 35 },
      { "defectCode": "MISSING_SEAT",    "penaltyPoints": 20 }
    ]
  }
]
```

**Tab 2 (Order)** → `GET /api/v1/orders/{id}` → `Execute`:

```json
{ "orderId": "ORD-...", "status": "FAILED" }
```

**Tab 9 (DWH)** → `GET /api/v1/twins/{vehicleId}` → `Execute` → the twin records the failure.

---

### Step C4 — Prove that FAILED is terminal

**Tab 2 (Order)** → `PUT /api/v1/orders/{id}/status` → `Try it out`

Paste your `orderId` into the path box and this body:

```json
{
  "newStatus": "COMPLETED"
}
```

→ `Execute`. Expect **`409 Conflict`**.

> Say: "the order state machine refuses to leave a terminal state — you cannot un-scrap a car."

---

### Alternative ending — failure by exhausted rework budget

A nice variation on Flow B: leave the outcome pinned to `REWORK` and keep re-advancing the vehicle.
On the **4th** inspection Quality notices the rework budget is spent and downgrades the decision to
`FAIL` on its own — the order ends `FAILED` even though the score was in the rework band.

---

### Step C5 — Reset afterwards

**Tab 8 (Quality)** → `PUT /api/v1/inspections/demo/outcome/{outcome}` → `outcome` = `RANDOM` →
`Execute`.

### ✅ Success criteria

* Inspection decision `FAIL`, score < 50
* `quality-failed` visible on the topic
* Order status `FAILED`
* Further status changes rejected with `409`

---

## 10. Producing Kafka messages by hand

Everything above is driven from Swagger. Use this section only if a consumer misfires and you want to
re-inject an event, or if you want to demo a single service in isolation.

**In Kafka UI**: `Topics` → *topic name* → **`Produce Message`** → set **`Key`** = the `vehicleId`
→ paste the JSON into **`Value`** → **`Produce Message`**.

> ⚠️ **Two rules**
> 1. The **Key must be the `vehicleId`**, otherwise ordering per vehicle breaks.
> 2. Every consumer deduplicates on `eventId`. Re-sending the same `eventId` does **nothing** —
>    always change it (bump the number at the end).

> 📦 Note the wire format: the envelope is wrapped in an outer `"event"` object.

Replace `VEH-XXXX` / `ORD-XXXX` with your real ids everywhere.

---

### `production-planned` — make Inventory reserve and Assembly create the vehicle

Topic: **`production-planned`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-001",
    "eventType": "production-planned",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:00:00Z",
    "sourceSystem": "planning-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "planId": "PLAN-MANUAL-001",
      "orderId": "ORD-XXXX",
      "vehicleId": "VEH-XXXX",
      "vehicleModel": "BMW_I4",
      "productionLine": "LINE-2",
      "priority": "HIGH",
      "plannedStartDate": "2026-08-19"
    }
  }
}
```

---

### `material-requested` — make an AGV run a delivery mission

Topic: **`material-requested`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-002",
    "eventType": "material-requested",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:05:00Z",
    "sourceSystem": "assembly-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "requestId": "REQ-MANUAL-001",
      "vehicleId": "VEH-XXXX",
      "orderId": "ORD-XXXX",
      "material": "PAINT_KIT",
      "quantity": 1,
      "targetStage": "PAINT",
      "targetNode": "LINE-2-PAINT-STATION"
    }
  }
}
```

> Watch `material-delivered` appear a moment later — that is the AGV service answering.

---

### `vehicle-assembled` — make Quality inspect a vehicle

Topic: **`vehicle-assembled`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-003",
    "eventType": "vehicle-assembled",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:10:00Z",
    "sourceSystem": "assembly-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "vehicleId": "VEH-XXXX",
      "orderId": "ORD-XXXX",
      "vehicleModel": "BMW_I4",
      "productionLine": "LINE-2",
      "assembledAt": "2026-08-19T09:10:00Z"
    }
  }
}
```

---

### `quality-approved` — force the order to COMPLETED

Topic: **`quality-approved`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-004",
    "eventType": "quality-approved",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:15:00Z",
    "sourceSystem": "quality-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "inspectionId": "INSP-MANUAL-001",
      "vehicleId": "VEH-XXXX",
      "score": 95,
      "decidedAt": "2026-08-19T09:15:00Z"
    }
  }
}
```

---

### `quality-rework-required` — force a rework

Topic: **`quality-rework-required`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-005",
    "eventType": "quality-rework-required",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:20:00Z",
    "sourceSystem": "quality-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "inspectionId": "INSP-MANUAL-002",
      "vehicleId": "VEH-XXXX",
      "score": 72,
      "targetStage": "PAINT",
      "reason": "Surface paint scratch",
      "decidedAt": "2026-08-19T09:20:00Z"
    }
  }
}
```

> `targetStage` must be one of `BODY`, `PAINT`, `INTERIOR`, `POWERTRAIN`, `FINAL`.
> The vehicle must be in status `ASSEMBLED` for the rewind to be accepted.

---

### `quality-failed` — force the order to FAILED

Topic: **`quality-failed`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-006",
    "eventType": "quality-failed",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:25:00Z",
    "sourceSystem": "quality-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "inspectionId": "INSP-MANUAL-003",
      "vehicleId": "VEH-XXXX",
      "score": 40,
      "defectCodes": ["BATTERY_FAILURE", "MISSING_SEAT"],
      "decidedAt": "2026-08-19T09:25:00Z"
    }
  }
}
```

---

### `parts-delivered` — restock inventory (shortage loop)

Topic: **`parts-delivered`** · Key: `VEH-XXXX`

```json
{
  "event": {
    "eventId": "EVT-MANUAL-007",
    "eventType": "parts-delivered",
    "eventVersion": "1.0",
    "timestamp": "2026-08-19T09:30:00Z",
    "sourceSystem": "procurement-service",
    "correlationId": "VEH-XXXX",
    "payload": {
      "purchaseOrderId": "PO-MANUAL-001",
      "supplierId": "SUP-001",
      "partId": "BAT-LONGRANGE",
      "partName": "Long Range Battery Pack",
      "quantity": 5,
      "deliveryDate": "2026-08-19",
      "totalPrice": 9500.00
    }
  }
}
```

---

## 11. Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `POST /orders` returns **422** | Model/battery combination invalid, or the model has no validator (`BMW_X5`) | `BMW_I4` → `LONG_RANGE`/`STANDARD_RANGE` · `BMW_IX` → `LONG_RANGE`/`PERFORMANCE` · `BMW_M5` → `STANDARD_RANGE` |
| `POST /orders` returns **400** | A required field is missing or `customerEmail` is malformed | Re-paste the body from this document |
| No `production-planned` after creating the order | All three lines are at capacity (5 vehicles each) | Complete or fail some vehicles, or `docker compose restart planning-service` |
| No `parts-reserved`, and the inventory log says *"No BOM found for vehicle model …"* | The model has no BOM seeded | Use `BMW_I4`, `BMW_IX` or `BMW_M5`; check **Tab 4** → `GET /api/v1/inventory/allInventoryStockRecords` shows 13 parts |
| Assembly `GET /vehicles/{id}` returns **404** | `production-planned` has not been consumed yet | Wait a moment and retry |
| `advance` returns **409** | Vehicle is not `IN_PRODUCTION` — either still `CREATED` (parts not reserved) or already `ASSEMBLED` | `GET /api/v1/vehicles/{id}` to see the real state |
| No `material-delivered` | Target station missing from the AGV graph, or no AGV free | Re-run preflight **5.4**; check **Tab 7** → `GET /api/v1/agvs` |
| Inspection decision is not what you pinned | The outcome was set **after** `vehicle-assembled` was published | Pin it **before** the 5th advance; verify with `GET /api/v1/inspections/demo/outcome` |
| Order stuck in `QUALITY_CHECK` | The quality event was consumed but the transition was rejected | `docker compose logs order-service` |
| Messages piling up in a `*-dlq` topic | A consumer threw an exception | Open the DLQ topic in Kafka UI, then `docker compose logs -f <service>` |

### Useful log tails

```bash
docker compose logs -f quality-service  | grep -E "Publishing|decision"
docker compose logs -f order-service    | grep -E "Processing|COMPLETED|FAILED|QUALITY_CHECK"
docker compose logs -f assembly-service | grep -E "Processing|rework"
docker compose logs -f agv-service      | grep -E "mission|route"
```

### Full reset

```bash
docker compose down -v
docker compose up -d --build
```

Then set the outcome back to `RANDOM` (**Tab 8**, Step C5).

---

## 12. Appendix — what had to be fixed

These blockers were found while tracing the three flows, and the minimal fix applied to each.

| # | Problem | Fix | Where |
|---|---|---|---|
| 1 | The order stayed in `CREATED` forever (nobody consumed the planning/inventory events), so **every** quality transition was rejected by the order state machine. All three flows died at the last step. | New `vehicle-assembled` consumer fast-forwards the order `CREATED → PLANNED → IN_PRODUCTION → QUALITY_CHECK`, emitting one `order-status-updated` per hop. | `order-service` — `VehicleAssembledConsumer`, `OrderProgressService#markOrderForQualityCheck` |
| 2 | **No consumer for `quality-failed`** — the failed flow had no path into Order at all. | New consumer + `failOrderFromQualityFailure`. | `order-service` — `QualityFailedConsumer`, `OrderProgressService` |
| 3 | `REWORK_REQUIRED → FAILED` was not an allowed transition, so a vehicle failing *after* a rework could not be scrapped. | Added `FAILED` to the allowed set. | `order-service` — `OrderStatusTransitionHelper` |
| 4 | The inspection outcome was **random**, so a scripted demo was impossible. | `DemoOutcomeControl` + `PUT /api/v1/inspections/demo/outcome/{outcome}`. | `quality-service` — `DemoOutcomeControl`, `DemoOutcomeResource`, `DefectSeeder` |
| 5 | The AGV graph only had **LINE-2** BODY/PAINT/POWERTRAIN stations, but Planning assigns LINE-1/2/3 and Assembly also requests material at INTERIOR → `NoRouteException`, no `material-delivered`. | Flyway migration seeding all line/stage stations plus edges. | `agv-service` — `V7__seed_all_line_stations.sql` |
| 6 | During rework Assembly rewinds the stage, but Planning threw on the "out of order" stage event and pushed the rest of the rework into the DLQ. | Re-synchronise and log instead of throwing. | `planning-service` — `ProductionPlanService#advanceVehicleStage` |
| 7 | Order validation accepts `BMW_I4`, `BMW_IX` and `BMW_M5`, but only `BMW_I4` had a BOM. An `BMW_IX`/`BMW_M5` order was accepted and planned, then silently stalled in Inventory ("No BOM found") — no reservation, no `parts-reserved`, assembly never started. | Flyway migration seeding 5 new parts + stock and the BOM recipes for both models. | `inventory-service` — `V18__seed_bom_for_ix_and_m5.sql` |

> All seed data (production lines, parts + stock, vehicle BOM, factory graph, AGVs, defect catalog)
> is provisioned by Flyway on startup — there is **no manual setup** to do.

