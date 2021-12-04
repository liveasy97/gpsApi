package com.spring.gpsApiData.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spring.gpsApiData.dao.model.StoppageData;
import com.spring.gpsApiData.entities.Tc_position;

@Component
public class GpsDataDao implements IGpsDataDao {

	@Value("${spring.datasource.url}")
	private String databaseUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	public List<StoppageData> getStoppages(String imei, String startTime, String endTime)
			throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = getConnection();

		PreparedStatement stmt = conn
				.prepareStatement("select tcp.latitude,tcp.longitude,tce.type,tce.eventtime,tcd.uniqueid,tcp.speed "
						+ "from tc_events tce " + "join tc_devices tcd on tcd.id = tce.deviceid "
						+ "join tc_positions tcp on tcp.id=tce.positionid "
						+ "where tcd.uniqueid = ? and type in ('deviceStopped','deviceMoving')"
						+ "and eventtime between ? and ? ");

		stmt.setString(1, imei);
		stmt.setString(2, startTime);
		stmt.setString(3, endTime);

		ResultSet rs = stmt.executeQuery();

		List<StoppageData> stoppageList = new ArrayList<>();
		while (rs.next()) {
			StoppageData obj = new StoppageData();

			obj.setEventTime(rs.getString("eventtime"));
			obj.setLatitude(rs.getDouble("latitude"));
			obj.setLongitude(rs.getDouble("longitude"));
			obj.setType(rs.getString("type"));
			stoppageList.add(obj);
		}

		return stoppageList;

	}

	public List<Tc_position> getMinuteWiseSummarizedData(String imei, String startTime, String endTime)
			throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = getConnection();

		PreparedStatement stmt = conn.prepareStatement(
				"select minutedata.minutetime, minutedata.devicetime,minutedata.latitude,minutedata.longitude,"
						+ "minutedata.speed,max(minutedata.servertime),minutedata.uniqueid,minutedata.id,minutedata.deviceid "
						+ "from (" + "select date_format(tcp.servertime, '%Y-%m-%d %H:%i:00') as minutetime,"
						+ "tcp.devicetime,tcp.latitude,tcp.longitude,tcp.speed,tcp.servertime,tcd.uniqueid,tcd.id,tcp.deviceid "
						+ "from tc_devices tcd inner join tc_positions tcp on tcd.id = tcp.deviceid "
						+ "where tcd.uniqueid = ? and tcp.servertime between ? and ?  order by tcp.servertime desc  ) as minutedata "
						+ "group by minutetime");

		stmt.setString(1, imei);
		stmt.setString(2, startTime);
		stmt.setString(3, endTime);

		ResultSet rs = stmt.executeQuery();

		List<Tc_position> positions = new ArrayList<>();
		while (rs.next()) {
			Tc_position position = new Tc_position();
			position.setLat(rs.getDouble("latitude"));
			position.setLng(rs.getDouble("longitude"));
			position.setServerTime(rs.getString("minutetime"));
			position.setSpeed(rs.getString("speed"));

			positions.add(position);
		}

		return positions;
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(databaseUrl, username, password);
	}
}
