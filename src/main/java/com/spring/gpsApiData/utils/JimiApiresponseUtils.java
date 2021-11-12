package com.spring.gpsApiData.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.spring.gpsApiData.model.duration;

@Component
public class JimiApiresponseUtils {

	public double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = (dist / 0.62137); // miles to kms
		if (Double.isNaN(dist)) {
			return 0;
		}
		return dist;
	}

	public double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public String convert_GMT_To_IST(String gpstime) throws ParseException {
		DateFormat istDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		istDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = gmtDateFormat.parse(gpstime);
		return istDateFormat.format(date);

	}

	public String convert_IST_To_GMT(String timeInIST) throws ParseException {
		// System.out.println("timeEnteredbyuser : " + timeEnteredByuser);

		DateFormat istDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		istDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date date = istDateFormat.parse(timeInIST);

		return gmtDateFormat.format(date);
	}

	public duration findDuration(String starttime, String endTime) throws ParseException {
		SimpleDateFormat istFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;

		d1 = istFormat.parse(starttime);
		d2 = istFormat.parse(endTime);

		// in milliseconds
		long diff = d2.getTime() - d1.getTime();
		long seconds = diff / 1000 % 60;
		long minutes = diff / (60 * 1000) % 60;
		long hours = diff / (60 * 60 * 1000) % 24;
		long days = diff / (24 * 60 * 60 * 1000);

		duration d = new duration();
		d.setDays(days);
		d.setHours(hours);
		d.setMinutes(minutes);
		d.setSeconds(seconds);
		return d;
	}
}
