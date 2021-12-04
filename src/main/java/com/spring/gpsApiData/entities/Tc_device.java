package com.spring.gpsApiData.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tc_devices")
@Data
public class Tc_device {
	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "uniqueid")
	private String uniqueId;
}
