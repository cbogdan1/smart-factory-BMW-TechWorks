package com.smartfactory.quality.control.repositories;

import java.util.List;

import com.smartfactory.quality.entity.InspectionDefect;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InspectionDefectRepository implements PanacheRepository<InspectionDefect> {

    public boolean existsByInspectionIdAndDefectCode(final String inspectionId, final String defectCode) {
        return count("inspectionId = ?1 and defectCode = ?2", inspectionId, defectCode) > 0;
    }

    public List<InspectionDefect> findByInspectionId(final String inspectionId) {
        return list("inspectionId = ?1", Sort.by("createdDate"), inspectionId);
    }
}
