package com.spring.gpsApiData.utils;

import com.spring.gpsApiData.entities.historyData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public interface getDataFromJimi {
    public static GpsDataModel getGpsApiDataUsingImei(String imeis) throws Exception {

        String access_token = "";
        //first get the jimmy access token
        while (access_token == "") {
            String url = "http://3.109.80.120:1000/token";
            URL obj = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == 200) {
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

                JSONObject myResponse = new JSONObject(res.get(0).toString());
                access_token = myResponse.getString("access_token");
                System.out.println("access_token : " + access_token);
            } else {
                Thread.sleep(10 * 1000);
                System.out.println("couldn't find any access token");
            }

        }

        //then using jimmy api services and providing credentials in params
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
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
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
        
        String responseCode = json_res.getString("code");
        
        if(Integer.parseInt(responseCode) == 0) {
	        JSONArray jsonArray = json_res.getJSONArray("result");
	        
	        if (jsonArray == null || jsonArray.length() == 0) {
	        	throw new Exception("Api failed with empty response");
	        }
	        
	        JSONObject json = (JSONObject) jsonArray.get(0);
	
	        String imei = json.getString("imei");
	        String lat = json.getString("lat");
	        String lng = json.getString("lng");
	        String speed = json.getString("speed");
	        String deviceName = json.getString("deviceName");
	        String powerValue = json.getString("powerValue");
	        String direction = json.getString("direction");
	        String gpsTime = json.getString("gpsTime");
	        GpsDataModel gpsDataModel = new GpsDataModel();
	        gpsDataModel.setImei(imei);
	        gpsDataModel.setLat(lat);
	        gpsDataModel.setLng(lng);
	        gpsDataModel.setSpeed(speed);
	        gpsDataModel.setDeviceName(deviceName);
	        gpsDataModel.setPowerValue(powerValue);
	        gpsDataModel.setDirection(direction);
	        gpsDataModel.setGpsTime(gpsTime);
	        System.out.println(gpsDataModel);
	
	        return gpsDataModel;
        }
        else {
            throw new Exception("Api failed with error: "+responseCode);
        }

    }
}
