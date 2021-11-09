package com.spring.gpsApiData.model;

import java.util.List;

import lombok.Data;

@Data
public class DeviceTrackListAndStoppagesListResponse {
	
	private List<DeviceTrackListModel> deviceTrackList;
	private List<StoppagesListModel> stoppagesList;
	
}
