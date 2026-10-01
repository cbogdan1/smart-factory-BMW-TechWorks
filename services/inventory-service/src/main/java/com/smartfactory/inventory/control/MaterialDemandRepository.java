package com.smartfactory.inventory.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.smartfactory.inventory.entity.MaterialDemand;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MaterialDemandRepository implements PanacheRepository<MaterialDemand> {
    /**
     * Find all upcoming demands: plannedDate >= today's date
     * @return list of MaterialDemand objects with plannedDate >= today's date
     */
    public List<MaterialDemand> findUpcomingDemands() {
        return list(
                "plannedDate >= ?1 ORDER BY plannedDate ASC",
                LocalDate.now()
        );
    }

    /**
     * Find all material demands by vehicleId
     * @param vehicleId the id of the vehicle we want to retrieve the material demand records for
     * @return list of MaterialDemand objects with their vehicle id = vehicleId
     */
    public List<MaterialDemand> findByVehicleId(final String vehicleId) {
        return list(
                "vehicleId = ?1 ORDER BY plannedDate ASC",
                vehicleId
        );
    }

    /**
     * Find all material demands by planId
     * @param planId the id of the plan we want to retrieve the material demand records for
     * @return list of MaterialDemand objects with their plan id = planId
     */
    public List<MaterialDemand> findByPlanId(final String planId){
        return list("planId", planId);
    }

    /**
     * Finds a material demand by its demand ID in the inventory system.
     *
     * @param demandId The demand ID of the material demand to find.
     * @return An Optional containing the found material demand, or empty if not found.
     */
    public Optional<MaterialDemand> findByDemandId(final String demandId) {
        return find("demandId", demandId).firstResultOptional();
    }

    /**
     * Finds a material demand by its event ID in the inventory system.
     *
     * @param eventId The event ID of the material demand to find.
     * @return An Optional containing the found material demand, or empty if not found.
     */
    public List<MaterialDemand> findByEventId(final String eventId) {
        return list("eventId", eventId);
    }

    /**
     * Check if a Kafka event is already accounted for
     * @param eventId the id of the event we're checking for duplicates
     * @return true if there is a duplicate, false otherwise
     */
    public boolean existsByEventId(final String eventId){
        return count("eventId", eventId) > 0;
    }

}
