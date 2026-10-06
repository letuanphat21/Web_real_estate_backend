package com.dat_viet_group.datvietgroup.modules.project.entity;

import java.math.BigDecimal;

import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyDirection;
import com.dat_viet_group.datvietgroup.modules.project.enums.PropertyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "property")
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "property_code", length = 100)
    private String propertyCode;

    @Column(name = "area", precision = 19, scale = 4)
    private BigDecimal area;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 20)
    private PropertyDirection direction;

    @Column(name = "floor")
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PropertyStatus status;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "vector_embedding", columnDefinition = "text")
    private String vectorEmbedding;
}