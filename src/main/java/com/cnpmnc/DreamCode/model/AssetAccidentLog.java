package com.cnpmnc.DreamCode.model;

import com.cnpmnc.DreamCode.model.converter.StringListConverter;
import com.cnpmnc.DreamCode.model.enumType.AccidentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetAccidentLog extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> imageKeys;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccidentStatus status; 
    
    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "reporter_user_name", nullable = false)
    private String reporterUserName; 
    
    @Column(name = "reporter_department_name")
    private String reporterDepartmentName; 
    
}