package com.spring.gpsApiData.dao;

import com.spring.gpsApiData.entities.TraccarData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface TraccarDataDao extends JpaRepository<TraccarData, Integer>{
    @Query("select t from TraccarData t join DeviceData d on t.deviceid=d.id where d.uniqueid=:imei")
    public List<TraccarData> findByImei(String imei);

    //@Query("select t from TraccarData t join DeviceData d on d.uniqueid= :imei")
//    public List<TraccarData> findByImei(String imei);
//    @Query("select t from historyData t where t.imei = :imei and t.timeStamp between :startTime and :endTime")
//    public List<historyData> findByImeiBetweenTimeRange(String imei, String startTime, String endTime);
//
//    @Query("select t from historyData t where t.timeStamp between :startTime and :endTime")
//    public List<historyData> findHistoryDataBetweenTimeRange(String startTime,String endTime);
}
