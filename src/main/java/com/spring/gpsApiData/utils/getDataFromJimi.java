package com.spring.gpsApiData.utils;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.CreateGeoFencePostRequest;
import com.spring.gpsApiData.model.CreateGeoFenceResponse;
import com.spring.gpsApiData.model.DeviceTrackListAndStoppagesListResponse;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.JimiException;
import com.spring.gpsApiData.model.RelaySendCommandResponse;
import com.spring.gpsApiData.model.RouteHistoryModel;
import com.spring.gpsApiData.model.RouteHistoryWithTotalDistanceModel;
import com.spring.gpsApiData.model.StoppagesListModel;
import com.spring.gpsApiData.model.duration;

@Component
public class getDataFromJimi {

	@Autowired
	JimiApiresponseUtils resUtils;

	@Value("${ACCESS_TOKEN_URL}")
	private String accessTokenUrl;

	@Value("${JIMI_URL}")
	private String jimiUrl;

	@Value("${APP_KEY}")
	private String appKey;

	@Value("${USER_ID}")
	private String userId;

	@Value("${USER_PWD_MD5}")
	private String userPwdMd5;

	@Value("${SIGN}")
	private String sign;

	@Value("${SIGN_METHOD}")
	private String signMethod;

	@Value("${TARGET}")
	private String target;

	private Map<String, String> commonParams = null;

	@PostConstruct
	private void intialise() {
		commonParams = Map.ofEntries(entry("app_key", appKey), entry("sign", sign), entry("sign_method", signMethod),
				entry("v", "0.9"), entry("format", "json"), entry("expires_in", "7200"), entry("target", target),
				entry("user_id", userId), entry("user_pwd_md5", userPwdMd5));
	}

	private String accessToken;
	private long accessTokenExpiryInMillis;

	public String getAccessTokenFromJimi() throws Exception {

		if (accessToken == null || System.currentTimeMillis() > accessTokenExpiryInMillis) {
			SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			// Current Date Time in GMT
			System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

			URL url_location = new URL(jimiUrl);
			Map<String, String> allRequiredParams = new HashMap<>();
			allRequiredParams.put("method", "jimi.oauth.token.get");
			allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
			allRequiredParams.putAll(commonParams);
			String res = getJsonResponse(allRequiredParams, url_location);
			System.out.println("accesToken response :" + res);
			JSONObject jsonObj = new JSONObject(res.toString());
			JSONObject resultObj = jsonObj.getJSONObject("result");
			accessToken = resultObj.getString("accessToken");
			long validityDurationInSec = resultObj.getLong("expiresIn") - 300; // -300sec for expiry margin//
			long currentTimeInMillis = System.currentTimeMillis();
			accessTokenExpiryInMillis = (validityDurationInSec * 1000) + currentTimeInMillis;
		}
		return accessToken;
	}

//	public String getAccessTokenFromJimi() throws Exception {
//		String access_token = "";
//		while (access_token == "") {
//			URL obj = new URL(accessTokenUrl);
//			HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
//			httpURLConnection.setRequestMethod("GET");
//			int responseCode = httpURLConnection.getResponseCode();
//			// System.out.println("Response Code : " + responseCode);
//			if (responseCode == 200) {
//				BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//				String inputLine;
//				StringBuffer response = new StringBuffer();
//				while ((inputLine = in.readLine()) != null) {
//					response.append(inputLine);
//				}
//				in.close();
//				JSONArray res = new JSONArray(response.toString());
//				// System.out.println(res);
//
//				JSONObject myResponse = new JSONObject(res.get(0).toString());
//				access_token = myResponse.getString("access_token");
//				// System.out.println("access_token : " + access_token);
//			} else {
//				Thread.sleep(10 * 1000);
//				System.out.println("couldn't find any access token");
//			}
//		}
//		return access_token;
//	}

	// by giving all requiredparameters(common+private), we are getting json
	// response//
	public String getJsonResponse(Map<String, String> params, URL url_location) throws Exception {
		StringBuilder postData = new StringBuilder();

		for (Map.Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
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
		System.out.println("postData is : " + postData);

		BufferedReader in = null;
		String res = null;
		try {

			if (conn.getResponseCode() == 200) {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			} else {
				in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
			}
		} catch (Exception e) {
			throw new JimiException("Make Sure you have given all request parameters");
		}

		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char) c);
		res = sb.toString();
		return res;
	}

	// Converting Jsonresponse to JsonArray//
	public JSONArray convertJsonResponseToJsonArray(String res) throws Exception {
		JSONObject obj = new JSONObject(res);
		String responseCode = obj.getString("code");
		if (Integer.parseInt(responseCode) != 0) {
			String responseMessage = obj.getString("message");
			throw new JimiException(responseMessage);
		}
		JSONArray arr = null;
		if (obj.getString("result") != null) {
			arr = obj.getJSONArray("result");
		}

		if (Integer.parseInt(responseCode) == 0 && arr.length() == 0) {
			throw new JimiException("Response is Empty from jimi");
		}
		return arr;
	}

	public historyData getGpsApiDataUsingImei(String imeis) throws Exception {

		// first get the jimmy access token
		String access_token = getAccessTokenFromJimi();
		// then using jimmy api services and providing credentials in params
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// Current Date Time in GMT
		System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

		URL url_location = new URL(jimiUrl);
		Map<String, String> allRequiredParams = new HashMap<>();
		allRequiredParams.put("method", "jimi.device.location.get");
		allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
		allRequiredParams.put("access_token", access_token);
		allRequiredParams.put("imeis", imeis);
		allRequiredParams.putAll(commonParams);
		String res = getJsonResponse(allRequiredParams, url_location);
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
		gpsDataModel.setGpsTime(resUtils.convert_GMT_To_IST(json.getString("gpsTime")));
		System.out.println(gpsDataModel);
		return gpsDataModel;
	}

	public DeviceTrackListAndStoppagesListResponse getGpsApiDataUsingImeiStartTimeEndTime(String imei, String startTime,
			String endTime) throws Exception {

		// first get the jimmy access token
		String access_token = getAccessTokenFromJimi();
		// then using jimmy api services and providing credentials in params
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// Current Date Time in GMT
		System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

		URL url_location = new URL(jimiUrl);
		Map<String, String> allRequiredParams = new HashMap<>();
		allRequiredParams.put("method", "jimi.device.track.list");
		allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
		allRequiredParams.put("access_token", access_token);
		allRequiredParams.put("imei", imei);
		allRequiredParams.put("begin_time", resUtils.convert_IST_To_GMT(startTime));
		allRequiredParams.put("end_time", resUtils.convert_IST_To_GMT(endTime));
		allRequiredParams.putAll(commonParams);
		String res = getJsonResponse(allRequiredParams, url_location);
		JSONArray jsonArray = convertJsonResponseToJsonArray(res);
		if (jsonArray == null || jsonArray.length() == 0) {
			throw new JimiException("EmptyResponse");
		}
		DeviceTrackListAndStoppagesListResponse response = new DeviceTrackListAndStoppagesListResponse();
		List<DeviceTrackListModel> deviceTrackDataList = new ArrayList<DeviceTrackListModel>();

		if (jsonArray != null) {

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject trackModel = (JSONObject) jsonArray.get(i);

				DeviceTrackListModel obj = new DeviceTrackListModel();
				obj.setDirection(trackModel.getString("direction"));
				obj.setGpsSpeed(trackModel.getString("gpsSpeed"));
				obj.setGpsTime(resUtils.convert_GMT_To_IST(trackModel.getString("gpsTime")));
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

	public RouteHistoryWithTotalDistanceModel routeHistory(String imei, String startTime, String endTime)
			throws Exception {

		long timeStart = System.currentTimeMillis();

		// first get the jimmy access token
		String access_token = getAccessTokenFromJimi();
		// then using jimmy api services and providing credentials in params
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// Current Date Time in GMT

		URL url_location = new URL(jimiUrl);
		Map<String, String> allRequiredParams = new HashMap<>();
		allRequiredParams.put("method", "jimi.device.track.list");
		allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
		allRequiredParams.put("access_token", access_token);
		allRequiredParams.put("imei", imei);
		allRequiredParams.put("begin_time", resUtils.convert_IST_To_GMT(startTime));
		allRequiredParams.put("end_time", resUtils.convert_IST_To_GMT(endTime));
		allRequiredParams.putAll(commonParams);

		String res = getJsonResponse(allRequiredParams, url_location);

		long timeJimiCallEnd = System.currentTimeMillis();

		long timeElapsedForJimiApiCalls = timeJimiCallEnd - timeStart;
		System.out.println("timeElapsedForJimiApiCalls is : " + timeElapsedForJimiApiCalls);

		JSONArray jsonArray = convertJsonResponseToJsonArray(res);

		RouteHistoryWithTotalDistanceModel routeHistoryWithTotalDistance = new RouteHistoryWithTotalDistanceModel();

		List<RouteHistoryModel> list = new ArrayList<RouteHistoryModel>();
		double totalDistanceCovered = 0;

		int i = 0;
		while (i < jsonArray.length()) {

			JSONObject obj = (JSONObject) jsonArray.get(i);
			RouteHistoryModel routeHistoryObj = new RouteHistoryModel();
			double distanceCovered = 0;
			double lat1 = 0, lat2 = 0, lng1 = 0, lng2 = 0;

			if (Float.parseFloat(obj.getString("gpsSpeed")) > 2) {
				routeHistoryObj.setTruckStatus("running");
				routeHistoryObj.setStartTime(resUtils.convert_GMT_To_IST(obj.getString("gpsTime")));
				lat1 = ((JSONObject) jsonArray.get(i)).getDouble("lat");
				lng1 = ((JSONObject) jsonArray.get(i)).getDouble("lng");

				int j = i + 1;
				lat2 = ((JSONObject) jsonArray.get(j + 1)).getDouble("lat");
				lng2 = ((JSONObject) jsonArray.get(j + 1)).getDouble("lng");
				distanceCovered = distanceCovered + resUtils.distance(lat1, lng1, lat2, lng2);

				while (j < jsonArray.length()
						&& Float.parseFloat(((JSONObject) jsonArray.get(j)).getString("gpsSpeed")) > 2) {
					lat1 = ((JSONObject) jsonArray.get(j)).getDouble("lat");
					lng1 = ((JSONObject) jsonArray.get(j)).getDouble("lng");
					if (j + 1 < jsonArray.length()) {
						lat2 = ((JSONObject) jsonArray.get(j + 1)).getDouble("lat");
						lng2 = ((JSONObject) jsonArray.get(j + 1)).getDouble("lng");
					}
					distanceCovered = distanceCovered + resUtils.distance(lat1, lng1, lat2, lng2);

					j++;
				}

				i = j;

				if (i < jsonArray.length()) {
					routeHistoryObj.setEndTime(
							resUtils.convert_GMT_To_IST(((JSONObject) jsonArray.get(i)).getString("gpsTime")));
				} else {
					routeHistoryObj.setEndTime(
							resUtils.convert_GMT_To_IST(((JSONObject) jsonArray.get(i - 1)).getString("gpsTime")));
				}

				duration durationObj = resUtils.findDuration(routeHistoryObj.getStartTime(),
						routeHistoryObj.getEndTime());

				distanceCovered = Math.floor(distanceCovered * 100) / 100; // truncate to 2 decimal places
				routeHistoryObj.setDistanceCovered(distanceCovered);
				routeHistoryObj.setDuration(durationObj.toString());

				totalDistanceCovered = totalDistanceCovered + distanceCovered;
				list.add(routeHistoryObj);

			} else if (Float.parseFloat(obj.getString("gpsSpeed")) <= 2) {
				routeHistoryObj.setTruckStatus("stopped");
				routeHistoryObj.setStartTime(resUtils.convert_GMT_To_IST(obj.getString("gpsTime")));

				routeHistoryObj.setLat(obj.getDouble("lat"));
				routeHistoryObj.setLng(obj.getDouble("lng"));
				int j = i + 1;
				while (j < jsonArray.length()
						&& Float.parseFloat(((JSONObject) jsonArray.get(j)).getString("gpsSpeed")) <= 2) {
					j++;
				}

				i = j;
				if (i < jsonArray.length()) {
					routeHistoryObj.setEndTime(
							resUtils.convert_GMT_To_IST(((JSONObject) jsonArray.get(i)).getString("gpsTime")));
				} else {
					routeHistoryObj.setEndTime(
							resUtils.convert_GMT_To_IST(((JSONObject) jsonArray.get(i - 1)).getString("gpsTime")));
				}
				duration durationObj = resUtils.findDuration(routeHistoryObj.getStartTime(),
						routeHistoryObj.getEndTime());
				routeHistoryObj.setDuration(durationObj.toString());

				list.add(routeHistoryObj);
			}
		}

		long timeEnd = System.currentTimeMillis();
		long totalTimeElapsed = timeEnd - timeStart;
		System.out.println("TotalTimeElapsed is : " + totalTimeElapsed);
		Collections.reverse(list);
		routeHistoryWithTotalDistance.setRouteHistoryList(list);
//		System.out.println("totaldis is : " + totalDistanceCovered);
		routeHistoryWithTotalDistance.setTotalDistanceCovered(totalDistanceCovered);
		return routeHistoryWithTotalDistance;
	}

	// This is for locationbyimei strttime endtime params ...stoppageslist //
	public List<StoppagesListModel> getStoppagesList(List<DeviceTrackListModel> deviceTrackDataList) throws Exception {

		List<StoppagesListModel> stoppagesList = new ArrayList<StoppagesListModel>();
		int i = 0;
		while (i < deviceTrackDataList.size()) {
			if (Float.parseFloat(deviceTrackDataList.get(i).getGpsSpeed()) > 2) {
				i++;
			} else if (Float.parseFloat(deviceTrackDataList.get(i).getGpsSpeed()) <= 2) {
				StoppagesListModel stoppageObj = new StoppagesListModel();
				stoppageObj.setTruckStatus("stopped");
				stoppageObj.setStartTime(deviceTrackDataList.get(i).getGpsTime());
				stoppageObj.setLat(deviceTrackDataList.get(i).getLat());
				stoppageObj.setLng(deviceTrackDataList.get(i).getLng());
				int j = i + 1;
				while (j < deviceTrackDataList.size()
						&& Float.parseFloat(deviceTrackDataList.get(j).getGpsSpeed()) <= 2) {
					j++;
				}
				i = j;
				if (i < deviceTrackDataList.size()) {
					stoppageObj.setEndTime(deviceTrackDataList.get(i).getGpsTime());
				} else {
					stoppageObj.setEndTime(deviceTrackDataList.get(i - 1).getGpsTime());
				}
				duration durationObj = resUtils.findDuration(stoppageObj.getStartTime(), stoppageObj.getEndTime());
				stoppageObj.setDuration(durationObj.toString());
				stoppagesList.add(stoppageObj);
			}
		}
		return stoppagesList;
	}

	public RelaySendCommandResponse getGpsApiDeviceRelayData(IgnitionOffPostRequest ignitionOffPostRequest)
			throws Exception {

		// first get the jimmy access token
		String access_token = getAccessTokenFromJimi();
		// then using jimmy api services and providing credentials in params
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// Current Date Time in GMT
		System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

		URL url_location = new URL(jimiUrl);
		Map<String, String> allRequiredParams = new HashMap<>();
		allRequiredParams.put("method", "jimi.open.instruction.send");
		allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
		allRequiredParams.put("access_token", access_token);
		allRequiredParams.put("imei", ignitionOffPostRequest.getImei());
		// Command message json character string //
		allRequiredParams.put("inst_param_json",
				"{\"inst_id\":\"113\",\"inst_template\":\"RELAY,1#\",\"params\":[],\"is_cover\":\"true\"}");
		allRequiredParams.putAll(commonParams);
		String res = getJsonResponse(allRequiredParams, url_location);
		JSONObject json_res = new JSONObject(res.toString());

		RelaySendCommandResponse relaySendCommandResponseModel = new RelaySendCommandResponse();

		relaySendCommandResponseModel.setCode(json_res.getInt("code"));
		relaySendCommandResponseModel.setMessage(json_res.getString("message"));
		relaySendCommandResponseModel.setResult(json_res.getString("result"));
		return relaySendCommandResponseModel;
	}

	public CreateGeoFenceResponse CreatingGeoFence(CreateGeoFencePostRequest createGeoFencePostRequest)
			throws Exception {

		// first get the jimmy access token
		String access_token = getAccessTokenFromJimi();
		// then using jimmy api services and providing credentials in params
		SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		// Current Date Time in GMT
		System.out.println("Current Date and Time in GMT time zone: " + gmtDateFormat.format(new Date()));

		URL url_location = new URL(jimiUrl);
		Map<String, String> allRequiredParams = new HashMap<>();
		allRequiredParams.put("method", "jimi.open.device.fence.create");
		allRequiredParams.put("timestamp", gmtDateFormat.format(new Date()));
		allRequiredParams.put("access_token", access_token);
		allRequiredParams.put("imei", createGeoFencePostRequest.getImei());
		// Command message json character string //
		allRequiredParams.put("fence_name", createGeoFencePostRequest.getFenceName());
		allRequiredParams.put("alarm_type", createGeoFencePostRequest.getAlarmType());
		allRequiredParams.put("report_mode", createGeoFencePostRequest.getReportMode());
		allRequiredParams.put("alarm_switch", createGeoFencePostRequest.getAlarmSwitch());
		allRequiredParams.put("lng", createGeoFencePostRequest.getLng());
		allRequiredParams.put("radius", createGeoFencePostRequest.getRadius());
		allRequiredParams.put("zoom_level", createGeoFencePostRequest.getZoomLevel());
		allRequiredParams.put("lat", createGeoFencePostRequest.getLat());
		allRequiredParams.put("map_type", "GOOGLE");
		allRequiredParams.putAll(commonParams);
		String res = getJsonResponse(allRequiredParams, url_location);
		JSONObject json_res = new JSONObject(res.toString());

		CreateGeoFenceResponse CreateGeoFenceResponse = new CreateGeoFenceResponse();

		CreateGeoFenceResponse.setCode(json_res.getInt("code"));
		CreateGeoFenceResponse.setMessage(json_res.getString("message"));
		CreateGeoFenceResponse.setResult(json_res.getString("result"));
		return CreateGeoFenceResponse;
	}
}
