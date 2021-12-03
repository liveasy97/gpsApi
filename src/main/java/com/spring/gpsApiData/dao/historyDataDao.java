//package com.spring.gpsApiData.dao;
//
//import com.spring.gpsApiData.entities.historyData;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.format.annotation.DateTimeFormat.ISO;
//import org.springframework.stereotype.Repository;
//
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface historyDataDao extends JpaRepository<historyData, UUID>{
//
//    @Query("select t from historyData t where t.deviceid =7 and t.servertime <=current_time ")
//    public List<historyData> findByImei(String imei);
////    @Query("select t from historyData t where t.imei = :imei and t.timeStamp between :startTime and :endTime")
////    public List<historyData> findByImeiBetweenTimeRange(String imei, String startTime, String endTime);
////
////    @Query("select t from historyData t where t.timeStamp between :startTime and :endTime")
////    public List<historyData> findHistoryDataBetweenTimeRange(String startTime,String endTime);
//}
