package com.smartfactory.assembly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import java.time.OffsetDateTime;

@Entity
@Table(name = "event_processed")
@Getter
@Setter
public class EventProcessed {
    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "processed_at", nullable = false, updatable = false)
    @ColumnDefault("now()")
    @Generated
    private OffsetDateTime processedAt;
}
