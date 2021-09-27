package com.spring.gpsApiData.model;
import com.spring.gpsApiData.constants.Constants.Status;

import lombok.Data;


@Data
public class RegisteredImeiDataModel {
	private String imei;
	private Status status;
}
