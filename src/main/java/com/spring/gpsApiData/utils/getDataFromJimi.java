package com.spring.gpsApiData.utils;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.RelaySendCommandResponseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import static java.util.Map.entry;

@Component
public class getDataFromJimi {
	
	private static Map<String, String> commonParams = Map.ofEntries(
		    entry("app_key", "8FB345B8693CCD00E24B3F5EEE161B65"),
		    entry("sign", "23bfa9c9590a239d9fa25628e3149f96"),
		    entry("sign_method", "md5"),
		    entry("v", "0.9"),
		    entry("format", "json"),
		    entry("expires_in", "7200"),
		    entry("target", "liveasy@97")
//		    entry("user_id", "liveasy@97"),
//		    entry("user_pwd_md5", "cc120882480fd847d5a092a2d9817e75")
		);
	
	@Value("${ACCESS_TOKEN_URL}")
	private  String accessTokenUrl;
	
	@Value("${JIMI_URL}")
	private  String jimiUrl;
	
	public  String getAccessTokenFromJimi() throws Exception
	{
		String access_token = "";
        while (access_token == "") {
            URL obj = new URL(accessTokenUrl);
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
		return access_token;
	}
	
	//by giving all requiredparameters(common+private), we are getting json response//
	public  String getJsonResponse(Map<String,String> params,URL url_location) throws Exception
	{
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
	        System.out.println(postData);
	        
	        BufferedReader in = null;
	        String res = null;
	        
	        if (conn.getResponseCode() == 200)
	        {
	        	in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        	
	        } else {
	        	in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
	        }
	        
	        StringBuilder sb = new StringBuilder();
	        for (int c; (c = in.read()) >= 0; )
	        	sb.append((char) c);
	        res = sb.toString();
	        System.out.println(res);
	        
//	        if (conn.getResponseCode() == 400) {
//	        	JSONObject myResponse = new JSONObject(res);
//	        	String message = myResponse.getString("message");
//	        	throw new Exception(message);
//	        }
			return res;
	}
	
	// Converting Jsonresponse to JsonArray//
	public  JSONArray convertJsonResponseToJsonArray(String res) throws Exception
	{
		JSONObject json_res = new JSONObject(res.toString());    
        String responseCode = json_res.getString("code");
        
        if(Integer.parseInt(responseCode) == 0) {
	        JSONArray jsonArray = json_res.getJSONArray("result");
	        return jsonArray;
        }
        else {
            throw new Exception("Api failed with error: "+responseCode);
        }
	}
	
    public  historyData getGpsApiDataUsingImei(String imeis) throws Exception {
    	
    	//first get the jimmy access token
    	String access_token = getAccessTokenFromJimi();
        //then using jimmy api services and providing credentials in params
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

        URL url_location = new URL(jimiUrl);
        Map<String, String> allRequiredParams = new HashMap<>();
        allRequiredParams.put("method", "jimi.device.location.get");
        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
        allRequiredParams.put("access_token", access_token);
        allRequiredParams.put("imeis", imeis);  
        allRequiredParams.putAll(commonParams);       
        String res = getJsonResponse(allRequiredParams,url_location);
        JSONArray jsonArray = convertJsonResponseToJsonArray(res);
            
	        if (jsonArray == null || jsonArray.length() == 0) {
	        	throw new Exception("Api failed with empty response");
	        }      
	        JSONObject json = (JSONObject) jsonArray.get(0);
	
	        historyData gpsDataModel = new historyData();
	        gpsDataModel.setImei(json.getString("imei"));
	        gpsDataModel.setLat(json.getString("lat"));
	        gpsDataModel.setLng(json.getString("lng"));
	        gpsDataModel.setSpeed(json.getString("speed"));
	        gpsDataModel.setDeviceName(json.getString("deviceName"));
	        gpsDataModel.setPowerValue(json.getString("powerValue"));
	        gpsDataModel.setDirection(json.getString("direction"));
	        gpsDataModel.setGpsTime(json.getString("gpsTime"));
	        System.out.println(gpsDataModel);	
	        return gpsDataModel;
    }
    
    public  List<DeviceTrackListModel> getGpsApiDataUsingImeiStartTimeEndTime(String imei,String startTime, String endTime) throws Exception {

    	//first get the jimmy access token
    	String access_token = getAccessTokenFromJimi();
        //then using jimmy api services and providing credentials in params
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

        URL url_location = new URL(jimiUrl);
        Map<String, String> allRequiredParams = new HashMap<>();
        allRequiredParams.put("method", "jimi.device.track.mileage");
        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
        allRequiredParams.put("access_token", access_token);
        allRequiredParams.put("imeis", imei);
        allRequiredParams.put("begin_time", startTime);
        allRequiredParams.put("end_time", endTime);
        allRequiredParams.putAll(commonParams);
        String res = getJsonResponse(allRequiredParams,url_location);
        JSONArray jsonArray = convertJsonResponseToJsonArray(res);
	        if (jsonArray == null || jsonArray.length() == 0) {
	        	throw new Exception("Api failed with empty response");
	        }     
	        List<DeviceTrackListModel> deviceTrackModeList = new ArrayList<DeviceTrackListModel>();  
	        if (jsonArray != null) { 
	           for (int i=0;i<jsonArray.length();i++){
	        	   JSONObject trackModel = (JSONObject) jsonArray.get(i);
	        	   DeviceTrackListModel model = new DeviceTrackListModel();
	        	   model.setAvgSpeed(trackModel.getDouble("avgSpeed"));
	        	   model.setDistance(trackModel.getDouble("distance"));
	        	   model.setEndLat(trackModel.getString("endLat"));
	        	   model.setEndLng(trackModel.getString("endLng"));
	        	   model.setEndMileage(trackModel.getString("endMileage"));
	        	   model.setEndTime(trackModel.getString("endTime"));
	        	   model.setImei(trackModel.getString("imei"));
	        	   model.setRunTimeSecond(trackModel.getString("runTimeSecond"));
	        	   model.setStartLat(trackModel.getString("startLat"));
	        	   model.setStartLng(trackModel.getString("startLat"));
	        	   model.setStartMileage(trackModel.getString("startMileage"));
	        	   model.setStartTime(trackModel.getString("startTime"));
	        
	        	   deviceTrackModeList.add(model);
	           }
	        } 
	        return deviceTrackModeList;
	        
        }
    
    public RelaySendCommandResponseModel getGpsApiDeviceRelayData(IgnitionOffPostRequest ignitionOffPostRequest) throws Exception {

    	//first get the jimmy access token
    	String access_token = getAccessTokenFromJimi();
        //then using jimmy api services and providing credentials in params
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

        URL url_location = new URL(jimiUrl);
        Map<String, String> allRequiredParams = new HashMap<>();
        allRequiredParams.put("method", "jimi.open.instruction.send");
        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
        allRequiredParams.put("access_token", access_token);
        allRequiredParams.put("imei",ignitionOffPostRequest.getImei());
     // Command message json character string //      
        allRequiredParams.put("inst_param_json", "{\"inst_id\":\"113\",\"inst_template\":\"RELAY,1#\",\"params\":[],\"is_cover\":\"true\"}");
        allRequiredParams.putAll(commonParams);
        String res = getJsonResponse(allRequiredParams,url_location);
        JSONObject json_res = new JSONObject(res.toString());    
        
        RelaySendCommandResponseModel relaySendCommandResponseModel = new RelaySendCommandResponseModel();
        
        relaySendCommandResponseModel.setCode(json_res.getInt("code"));
        relaySendCommandResponseModel.setMessage(json_res.getString("message"));
        relaySendCommandResponseModel.setResult(json_res.getString("result"));
		return relaySendCommandResponseModel;
    }
        
}
