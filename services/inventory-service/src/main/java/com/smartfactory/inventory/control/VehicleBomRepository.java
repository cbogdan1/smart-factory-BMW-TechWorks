package com.smartfactory.inventory.control;

import java.util.List;
import com.smartfactory.inventory.entity.VehicleBom;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleBomRepository implements PanacheRepository<VehicleBom> {
    /**
     * Returns a list of all vehicleBom records which belong to a certain vehicle model
     * @param vehicleModel the model of the vehicle for which you want to retrieve all vehicle bom records
     * @return list of VehicleBom objects
     */
    public List<VehicleBom> findByVehicleModel(final String vehicleModel){
        return list("vehicleModel", vehicleModel);
    }
}
