package com.spring.gpsApiData.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.gpsApiData.dao.gpsDataDao;
import com.spring.gpsApiData.entities.gpsData;
import org.json.*;
import com.spring.gpsApiData.model.GpsDataModel;
@Service
public class gpsDataServiceImpl implements gpsDataService {
	
	@Autowired
	private gpsDataDao dao;
	
	@Override
	public GpsDataModel getgpsData(String imei) throws Exception {

		return getGpsApiDataUsingImei(imei);
	}

	@Override
	public String savegpsData(gpsData data) {
		gpsData newData = new gpsData();
		newData.setImei(data.getImei());
		newData.setLatitude(data.getLatitude());
		newData.setLongitude(data.getLongitude());
		newData.setSpeed(data.getSpeed());
		newData.setDeviceName(data.getDeviceName());
		newData.setPowerValue(data.getPowerValue());
		dao.save(newData);
		return "done";
	}

	@Override
	public List<gpsData> getAllGpsData() {
		
		return dao.findAll();
	}

	public static GpsDataModel getGpsApiDataUsingImei(String imeis) throws Exception {
		String access_token = "";

		while (access_token == ""){
			String url = "http://3.108.162.7:1000/token";
			URL obj = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("GET");
			int responseCode = httpURLConnection.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			if (responseCode == 200){
				BufferedReader in = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				JSONArray res = new JSONArray(response.toString());
				System.out.println(res);

				JSONObject myResponse = new JSONObject( res.get(0).toString());
				access_token = myResponse.getString("access_token");
				System.out.println("access_token : "+ access_token);}
			else {
				Thread.sleep(10*1000);
				System.out.println("couldn't find any access token");
			}

		}

		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		//Current Date Time in GMT
		System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));


		URL url_location = new URL("http://open.10000track.com/route/rest");
		Map<String, String> params = new HashMap<>();
		params.put("method", "jimi.device.location.get");
		params.put("timestamp", gmtDateFormat.format(new Date()));
		params.put("app_key", "8FB345B8693CCD00E24B3F5EEE161B65");
		params.put("sign", "23bfa9c9590a239d9fa25628e3149f96");
		params.put("sign_method", "md5");
		params.put("v", "0.9");
		params.put("format", "json");
//                params.put("user_id", "liveasy@97");
//                params.put("user_pwd_md5", "cc120882480fd847d5a092a2d9817e75");
		params.put("expires_in", "7200");
		params.put("access_token", access_token);
		params.put("target", "liveasy@97");
		params.put("imeis", imeis);


		StringBuilder postData = new StringBuilder();

		for (Map.Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0) postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		byte[]postDataBytes = postData.toString().getBytes("UTF-8");
		HttpURLConnection conn = (HttpURLConnection) url_location.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0; )
			sb.append((char) c);
		String res = sb.toString();
		System.out.println(res);
		JSONObject json_res = new JSONObject(res.toString());
		System.out.println("code- " + json_res.getString("code"));
		System.out.println("message- " + json_res.getString("message"));
		JSONArray jsonArray = json_res.getJSONArray("result");
		System.out.println(jsonArray);
		JSONObject json = (JSONObject) jsonArray.get(0);

		String imei = json.getString("imei");
		String lat = json.getString("lat");
		String lng = json.getString("lng");
		String speed = json.getString("speed");
		String deviceName = json.getString("deviceName");
		String powerValue = json.getString("powerValue");
		GpsDataModel gpsDataModel = new GpsDataModel();
		gpsDataModel.setImei(imei);
		gpsDataModel.setLat(lat);
		gpsDataModel.setLng(lng);
		gpsDataModel.setSpeed(speed);
		gpsDataModel.setDeviceName(deviceName);
		gpsDataModel.setPowerValue(powerValue);
		System.out.println(gpsDataModel);

		return gpsDataModel;


//                    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//                    HttpPost request = new HttpPost("http://18.144.90.230:8080/locationbyimei");
//                    StringEntity output_params = new StringEntity("{\"imei\":\""+imei+"\",\"latitude\":\""+lat+"\",\"longitude\":\""+lng+"\",\"speed\":\""+speed+"\",\"deviceName\":\""+deviceName+"\",\"powerValue\":\""+powerValue+"\"}");
//                    request.addHeader("content-type", "application/json");
//                    request.setEntity(output_params);
//                    HttpResponse output_response = httpClient.execute(request);
//                    System.out.println(output_response.toString().substring(27, 30));

	}


}
