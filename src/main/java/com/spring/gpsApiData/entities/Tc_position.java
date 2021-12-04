package com.spring.gpsApiData.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tc_positions")
@Data
public class Tc_position {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "devicetime")
	private String deviceTime;

	@Column(name = "latitude")
	private double lat;

	@Column(name = "longitude")
	private double lng;

	@Column(name = "speed")
	private String speed;

	@Column(name = "servertime")
	private String serverTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "deviceid")
	private Tc_device device;

}
