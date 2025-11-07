package com.cnpmnc.DreamCode.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AssetRetireLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "retired_by_id", nullable = false)
    private User retiredBy;

    @Column(nullable = false)
    private LocalDateTime retiredTime;

    private String reason;

    private Double salvageValue;
}
