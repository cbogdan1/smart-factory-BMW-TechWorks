package com.smartfactory.procurement.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

import java.math.BigDecimal;

import com.smartfactory.common.dto.procurement.CreateSupplierRequest;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.repository.SupplierRepository;
import com.smartfactory.procurement.entity.Supplier;
import com.smartfactory.procurement.entity.SupplierPart;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SupplierResourceTest {

    @Inject
    SupplierRepository supplierRepository;

    @Inject
    SupplierPartRepository supplierPartRepository;

    @BeforeEach
    @Transactional
    void setup() {

        supplierPartRepository.deleteAll();
        supplierRepository.deleteAll();

        final Supplier sup1 = new Supplier();
        sup1.setSupplierId("SUP-001");
        sup1.setName("Supplier One");
        sup1.setLeadTimeDays(2);
        sup1.setRating(new BigDecimal("4.2"));
        sup1.setContactEmail("sup1@test.com");
        sup1.setActive(true);

        final Supplier sup2 = new Supplier();
        sup2.setSupplierId("SUP-002");
        sup2.setName("Supplier Two");
        sup2.setLeadTimeDays(5);
        sup2.setRating(new BigDecimal("4.0"));
        sup2.setContactEmail("sup2@test.com");
        sup2.setActive(true);

        final Supplier sup3 = new Supplier();
        sup3.setSupplierId("SUP-003");
        sup3.setName("Supplier Three");
        sup3.setLeadTimeDays(2);
        sup3.setRating(new BigDecimal("4.5"));
        sup3.setContactEmail("sup3@test.com");
        sup3.setActive(true);

        supplierRepository.persist(sup1);
        supplierRepository.persist(sup2);
        supplierRepository.persist(sup3);

        final SupplierPart sp1 = new SupplierPart();
        sp1.setSupplier(sup1);
        sp1.setPartId("PART-BAT-001");
        sp1.setUnitCost(new BigDecimal("210.00"));
        sp1.setLeadTimeDays(2);

        final SupplierPart sp2 = new SupplierPart();
        sp2.setSupplier(sup2);
        sp2.setPartId("PART-BAT-001");
        sp2.setUnitCost(new BigDecimal("220.00"));
        sp2.setLeadTimeDays(5);

        final SupplierPart sp3 = new SupplierPart();
        sp3.setSupplier(sup3);
        sp3.setPartId("PART-BAT-001");
        sp3.setUnitCost(new BigDecimal("210.00"));
        sp3.setLeadTimeDays(2);

        supplierPartRepository.persist(sp1);
        supplierPartRepository.persist(sp2);
        supplierPartRepository.persist(sp3);
    }

    @Test
    void testCreateSupplier_ValidPayload_Returns201() {
        final CreateSupplierRequest request =
                new CreateSupplierRequest("Global Industries", 30, new BigDecimal("4.8"), "sales@globalindustries.com",
                        true);

        given().contentType(ContentType.JSON).body(request).when().post("/api/v1/suppliers").then().statusCode(201)
                .body("supplierId", startsWith("SUP-")).body("name", is("Global Industries"))
                .body("contactEmail", is("sales@globalindustries.com"));
    }

    @Test
    void testCreateSupplier_InvalidEmail_Returns400() {
        final CreateSupplierRequest request =
                new CreateSupplierRequest("Bad Email Corp", 10, new BigDecimal("3.0"), "not-an-email", true);

        given().contentType(ContentType.JSON).body(request).when().post("/api/v1/suppliers").then().statusCode(400);
    }

    @Test
    void testGetAllSuppliers_Returns200() {
        given().when().get("/api/v1/suppliers").then().statusCode(200).body("$", any(java.util.List.class));
    }

    @Test
    void testGetSupplierById_NotFound_Returns404() {
        given().when().get("/api/v1/suppliers/SUP-INVALID").then().statusCode(404);
    }

    @Test
    void testSelectBestSupplier_TieBreakScenario_Returns200() {

        given().when().get("/api/v1/suppliers/select-best/PART-BAT-001").then().statusCode(200)
                .body("partId", is("PART-BAT-001")).body("selectedSupplierId", is("SUP-003"))
                .body("leadTimeDays", is(2)).body("rating", is(4.5f)).body("unitCost", is(210.0f));
    }

    @Test
    void testSelectBestSupplier_NoSupplierForPart_Returns422() {

        final String missingPartId = "PART-UNKNOWN-999";

        given().when().get("/api/v1/suppliers/select-best/" + missingPartId).then().statusCode(422)
                .body("message", is("No supplier available for part " + missingPartId));
    }
}