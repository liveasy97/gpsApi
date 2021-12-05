package com.spring.gpsApiData.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tc_events")
@Data
public class Tc_event {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "type")
	private String type;

	@Column(name = "eventtime")
	private String eventTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "deviceid")
	private Tc_device device;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "positionid")
	private Tc_position position;

}
