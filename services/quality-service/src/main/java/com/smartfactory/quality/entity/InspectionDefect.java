package com.smartfactory.quality.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspection_defect")
public class InspectionDefect {
    @Id
    @Column(name = "inspection_defect_id", nullable = false, length = 50)
    private String inspectionDefectId;

    @Column(name = "inspection_id", nullable = false, length = 50)
    private String inspectionId;

    @Column(name = "defect_code", nullable = false, length = 50)
    private String defectCode;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

}
