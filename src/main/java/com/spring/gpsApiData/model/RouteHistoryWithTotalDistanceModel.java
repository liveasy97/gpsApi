package com.spring.gpsApiData.model;

import java.util.List;

import lombok.Data;

@Data
public class RouteHistoryWithTotalDistanceModel {
	private double totalDistanceCovered;
	private List<RouteHistoryModel> routeHistoryList;

}
