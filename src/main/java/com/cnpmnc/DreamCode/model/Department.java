package com.cnpmnc.DreamCode.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Entity
@Data
public class Department extends BaseEntity {
    private String name;

    @OneToOne
    @JoinColumn(name = "manager_id", unique = true)
    private User manager;

    @OneToMany(mappedBy = "department")
    private java.util.List<Asset> assets;
}
