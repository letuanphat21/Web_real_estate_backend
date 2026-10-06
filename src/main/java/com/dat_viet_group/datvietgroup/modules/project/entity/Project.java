package com.dat_viet_group.datvietgroup.modules.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dat_viet_group.datvietgroup.modules.project.enums.BuildingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name", length = 200)
	private String name;

	@Column(name = "overview_image", length = 500)
	private String overviewImage;

	@Column(name = "location", length = 255)
	private String location;

	@Column(name = "investor", length = 200)
	private String investor;

	@Column(name = "consultancy", length = 200)
	private String consultancy;

	@Column(name = "development_model", length = 200)
	private String developmentModel;

	@Enumerated(EnumType.STRING)
	@Column(name = "building_type", length = 20)
	private BuildingType buildingType;

	@Column(name = "size", precision = 19, scale = 4)
	private BigDecimal size;

	@Column(name = "total_invesment", precision = 19, scale = 2)
	private BigDecimal totalInvestment;

	@Column(name = "ownership_type", length = 100)
	private String ownershipType;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
