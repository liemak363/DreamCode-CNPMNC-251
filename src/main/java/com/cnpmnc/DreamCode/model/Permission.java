package com.cnpmnc.DreamCode.model;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Data
public class Permission extends BaseEntity {
    String name;

    String description;
}