package com.spring.gpsApiData.utils;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.CreateGeoFencePostRequest;
import com.spring.gpsApiData.model.CreateGeoFenceResponse;
import com.spring.gpsApiData.model.DeviceTrackListAndStoppagesListResponse;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.JimiException;
import com.spring.gpsApiData.model.RelaySendCommandResponse;
import com.spring.gpsApiData.model.RouteHistoryResponse;
import com.spring.gpsApiData.model.duration;
import com.spring.gpsApiData.model.StoppagesListModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
	        System.out.println("postData is : " +postData);
	        
	        BufferedReader in = null;
	        String res = null;
	        
	        if (conn.getResponseCode() == 200)
	        {
	        	in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        	
	        } 
	        else {
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
        String responseResult = json_res.getString("result");
        
        if(Integer.parseInt(responseCode) == 0 && responseResult != null) {
	        JSONArray jsonArray = json_res.getJSONArray("result");
	        return jsonArray;
        }
        else if(Integer.parseInt(responseCode) == 0 && responseResult == null)
        {
        	return null;
        }   	
        else {
            throw new Exception("Api failed with error: "+responseCode);
        }
	}
	
	public String convert_GMT_To_IST(String gpstime) throws ParseException
	{
		DateFormat istDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		istDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = gmtDateFormat.parse(gpstime);
		return istDateFormat.format(date);
		
	}
	
	public String convert_IST_To_GMT(String timeEnteredByuser) throws ParseException
	{
		System.out.println("timeEnteredbyuser : " + timeEnteredByuser);
		DateFormat istDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		istDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = istDateFormat.parse(timeEnteredByuser);
		return gmtDateFormat.format(date);
		
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
	        gpsDataModel.setGpsTime(convert_GMT_To_IST(json.getString("gpsTime")));
	        System.out.println(gpsDataModel);	
	        return gpsDataModel;
    }
    
    public  DeviceTrackListAndStoppagesListResponse getGpsApiDataUsingImeiStartTimeEndTime(String imei,String startTime, String endTime) throws Exception {

    	//first get the jimmy access token
    	String access_token = getAccessTokenFromJimi();
        //then using jimmy api services and providing credentials in params
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

        URL url_location = new URL(jimiUrl);
        Map<String, String> allRequiredParams = new HashMap<>();
        allRequiredParams.put("method", "jimi.device.track.list");
        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
        allRequiredParams.put("access_token", access_token);
        allRequiredParams.put("imei", imei);
        allRequiredParams.put("begin_time", convert_IST_To_GMT(startTime));
        allRequiredParams.put("end_time", convert_IST_To_GMT(endTime));
        allRequiredParams.putAll(commonParams);
        String res = getJsonResponse(allRequiredParams,url_location);
        JSONArray jsonArray = convertJsonResponseToJsonArray(res);
        if (jsonArray == null || jsonArray.length() == 0) {
        	throw new JimiException("EmptyResponse");
        }   
        DeviceTrackListAndStoppagesListResponse response = new DeviceTrackListAndStoppagesListResponse();
        List<DeviceTrackListModel> deviceTrackDataList = new ArrayList<DeviceTrackListModel>();  
  
	        if (jsonArray != null) { 
	           for (int i=0;i<jsonArray.length();i++){
	        	   JSONObject trackModel = (JSONObject) jsonArray.get(i);
	        	   DeviceTrackListModel obj = new DeviceTrackListModel();
	        	   obj.setDirection(trackModel.getString("direction"));
	        	   obj.setGpsSpeed(trackModel.getString("gpsSpeed"));
	        	   obj.setGpsTime(convert_GMT_To_IST(trackModel.getString("gpsTime")));
	        	   obj.setLat(trackModel.getDouble("lat"));
	        	   obj.setLng(trackModel.getDouble("lng"));
	        	   obj.setPosType(trackModel.getString("posType"));
	        	   obj.setSatellite(trackModel.getString("satellite"));
	        	   deviceTrackDataList.add(obj);
	           }
	        } 
	        
	        response.setDeviceTrackList(deviceTrackDataList);
	        response.setStoppagesList(getStoppagesList(deviceTrackDataList));
	        return response;
	        
        }
    
    public RelaySendCommandResponse getGpsApiDeviceRelayData(IgnitionOffPostRequest ignitionOffPostRequest) throws Exception {

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
        
        RelaySendCommandResponse relaySendCommandResponseModel = new RelaySendCommandResponse();
        
        relaySendCommandResponseModel.setCode(json_res.getInt("code"));
        relaySendCommandResponseModel.setMessage(json_res.getString("message"));
        relaySendCommandResponseModel.setResult(json_res.getString("result"));
		return relaySendCommandResponseModel;
    }
    
    public CreateGeoFenceResponse CreatingGeoFence(CreateGeoFencePostRequest createGeoFencePostRequest) throws Exception {

    	//first get the jimmy access token
    	String access_token = getAccessTokenFromJimi();
        //then using jimmy api services and providing credentials in params
        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

        URL url_location = new URL(jimiUrl);
        Map<String, String> allRequiredParams = new HashMap<>();
        allRequiredParams.put("method", "jimi.open.device.fence.create");
        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
        allRequiredParams.put("access_token", access_token);
        allRequiredParams.put("imei",createGeoFencePostRequest.getImei());
     // Command message json character string //      
        allRequiredParams.put("fence_name",createGeoFencePostRequest.getFenceName() );
        allRequiredParams.put("alarm_type",createGeoFencePostRequest.getAlarmType());
        allRequiredParams.put("report_mode",createGeoFencePostRequest.getReportMode());
        allRequiredParams.put("alarm_switch",createGeoFencePostRequest.getAlarmSwitch());
        allRequiredParams.put("lng",createGeoFencePostRequest.getLng());
        allRequiredParams.put("radius",createGeoFencePostRequest.getRadius());
        allRequiredParams.put("zoom_level",createGeoFencePostRequest.getZoomLevel());
        allRequiredParams.put("lat",createGeoFencePostRequest.getLat());
        allRequiredParams.put("map_type","GOOGLE");
        allRequiredParams.putAll(commonParams);
        String res = getJsonResponse(allRequiredParams,url_location);
        JSONObject json_res = new JSONObject(res.toString());    
        
        CreateGeoFenceResponse CreateGeoFenceResponse = new CreateGeoFenceResponse();
        
        CreateGeoFenceResponse.setCode(json_res.getInt("code"));
        CreateGeoFenceResponse.setMessage(json_res.getString("message"));
        CreateGeoFenceResponse.setResult(json_res.getString("result"));
		return CreateGeoFenceResponse;
    }
    
//    public List<RouteHistoryResponse> routeHistory(String imei,String startTime, String endTime) throws Exception {
//
//    	//first get the jimmy access token
//    	String access_token = getAccessTokenFromJimi();
//        //then using jimmy api services and providing credentials in params
//        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        //Current Date Time in GMT
//        System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));
//
//        URL url_location = new URL(jimiUrl);
//        Map<String, String> allRequiredParams = new HashMap<>();
//        allRequiredParams.put("method", "jimi.device.track.list");
//        allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
//        allRequiredParams.put("access_token", access_token);
//        allRequiredParams.put("imei", imei);
//        allRequiredParams.put("begin_time", startTime);
//        allRequiredParams.put("end_time", endTime);
//        allRequiredParams.putAll(commonParams);
//        String res = getJsonResponse(allRequiredParams,url_location);
//        JSONArray jsonArray = convertJsonResponseToJsonArray(res);
//        if (jsonArray == null || jsonArray.length() == 0) {
//        	throw new JimiException("EmptyResponse");
//        }  
//       
//       List<RouteHistoryResponse> ls =new ArrayList<RouteHistoryResponse>();
//        
//        	int vehicle = 1;
//        	int i=0;
//	           while(i < jsonArray.length()){
//	        	   JSONObject trackModel = (JSONObject) jsonArray.get(i);
//	        	   RouteHistoryResponse routeHistoryResponse = new RouteHistoryResponse();
//	        	   if(vehicle ==1 && Float.parseFloat(trackModel.getString("gpsSpeed")) > 2)
//	        	   { 
//	        		   routeHistoryResponse.setTruckStatus("running");
//	        		   routeHistoryResponse.setStartTime(convert_GMT_To_IST(trackModel.getString("gpsTime")));
//	        		   
//	        		   System.out.println("travelled starttime is:" + convert_GMT_To_IST(trackModel.getString("gpsTime")));
//	        		   int j = i+1;
//	        		   while(j < jsonArray.length() && Float.parseFloat(((JSONObject) jsonArray.get(j)).getString("gpsSpeed")) > 2 && vehicle==1)
//	        		   {
//	        			  System.out.println("gps speed while running:" + ((JSONObject) jsonArray.get(j)).getString("gpsSpeed"));
//	        			  j++;
//	        		   }
//	        		   vehicle = 0;
//	        		   i = j;
//	        		   routeHistoryResponse.setEndTime(convert_GMT_To_IST(((JSONObject) jsonArray.get(j-1)).getString("gpsTime"))); 
//	        		   System.out.println("travelled endtime is:" +convert_GMT_To_IST(((JSONObject) jsonArray.get(j-1)).getString("gpsTime")));
//	        		   ls.add(routeHistoryResponse);
//	        		   
//	        	   }
//	        	   else if(vehicle ==0 && Float.parseFloat(trackModel.getString("gpsSpeed")) < 2)
//	        	   { 
//	  
//	        		   routeHistoryResponse.setTruckStatus("stopped");
//	        		   routeHistoryResponse.setStartTime(convert_GMT_To_IST(trackModel.getString("gpsTime")));
//	        		   System.out.println("stopped starttime is:" + convert_GMT_To_IST(trackModel.getString("gpsTime")));
//	        		   routeHistoryResponse.setLat(trackModel.getDouble("lat"));
//	        		   routeHistoryResponse.setLng(trackModel.getDouble("lng"));
//	        		   int j = i+1;
//	        		   while(j < jsonArray.length() && Float.parseFloat(((JSONObject) jsonArray.get(j)).getString("gpsSpeed")) < 2 && vehicle==0)
//	        		   {
//	        			   System.out.println("gps speed while stopped:" + ((JSONObject) jsonArray.get(j)).getString("gpsSpeed"));
//	        			  j++;
//	        		   }
//	        		   vehicle = 1;
//	        		   i = j;
//	        		   routeHistoryResponse.setEndTime(convert_GMT_To_IST(((JSONObject) jsonArray.get(j-1)).getString("gpsTime"))); 
//	        		   System.out.println("stopped endtime is:" +convert_GMT_To_IST(((JSONObject) jsonArray.get(j-1)).getString("gpsTime")));
//	        		   ls.add(routeHistoryResponse);
//	        		   
//	        	   }
//	           }
//	           System.out.println("list is : " + ls);
//		return ls;
//    }       
    
    // This is for locationbyimei strttime endtime params ...stoppageslist //
    public List<StoppagesListModel> getStoppagesList(List<DeviceTrackListModel> deviceTrackDataList) throws Exception {
   
    	List<StoppagesListModel> stoppagesList =new ArrayList<StoppagesListModel>();
    	int i = 0;
    	while( i < deviceTrackDataList.size())
    	{
    		if(Float.parseFloat(deviceTrackDataList.get(i).getGpsSpeed()) != 0)
    		{
    			i++;
    		}
    		else if(Float.parseFloat(deviceTrackDataList.get(i).getGpsSpeed()) == 0)
    		{
    			StoppagesListModel stoppageObj = new StoppagesListModel();
    			stoppageObj.setTruckStatus("stopped");
    			stoppageObj.setStartTime(deviceTrackDataList.get(i).getGpsTime());
    			int j = i+1;
    			while(j < deviceTrackDataList.size() && Float.parseFloat(deviceTrackDataList.get(j).getGpsSpeed()) == 0)
    			{
    				j++;
    			}
    			i = j;
    			if( i < deviceTrackDataList.size() )
    			{
    				stoppageObj.setEndTime(deviceTrackDataList.get(i).getGpsTime()); 
    				stoppageObj.setLat(deviceTrackDataList.get(i).getLat());
    				stoppageObj.setLng(deviceTrackDataList.get(i).getLng());
    			}
    			else
    			{
    				stoppageObj.setEndTime(deviceTrackDataList.get(i-1).getGpsTime()); 
    				stoppageObj.setLat(deviceTrackDataList.get(i-1).getLat());
    				stoppageObj.setLng(deviceTrackDataList.get(i-1).getLng());		
    			}
    			duration durationObj = findDuration(stoppageObj.getStartTime(),stoppageObj.getEndTime());
    			stoppageObj.setDuration(durationObj.toString());
    			stoppagesList.add(stoppageObj);
    		}
    	}
		return stoppagesList;        
    }       
        
    public duration findDuration(String starttime,String endTime) throws ParseException
    {
    	SimpleDateFormat istFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	Date d1 = null;
    	Date d2 = null;

    	d1 = istFormat.parse(starttime);
    	d2 = istFormat.parse(endTime);
    	
    	//in milliseconds
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
