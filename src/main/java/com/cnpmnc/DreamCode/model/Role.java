package com.cnpmnc.DreamCode.model;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Data
public class Role extends BaseEntity {

    String name;

    String description;

    @ManyToMany
    Set<Permission> permissions;
}