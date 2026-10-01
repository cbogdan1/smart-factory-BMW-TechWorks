package com.smartfactory.order.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processed_events")
public class ProcessedEvent extends PanacheEntityBase {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedDate;

    public ProcessedEvent(final String eventId) {
        this.eventId = eventId;
        this.processedDate = LocalDateTime.now();
    }
}