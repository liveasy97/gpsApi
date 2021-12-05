package com.spring.gpsApiData.dao;

import java.sql.SQLException;
import java.util.List;

import com.spring.gpsApiData.dao.model.StoppageData;
import com.spring.gpsApiData.entities.Tc_position;

public interface IGpsDataDao {
	List<Tc_position> getMinuteWiseSummarizedData(String imei, String startTime, String endTime)
			throws SQLException, ClassNotFoundException;

	List<StoppageData> getStoppages(String imei, String startTime, String endTime)
			throws SQLException, ClassNotFoundException;
}
