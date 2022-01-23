package com.spring.gpsApiData.dao;

import com.spring.gpsApiData.entities.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceDataDao extends JpaRepository<DeviceData, Integer> {
    @Query("select d from DeviceData d where d.uniqueid= :imei")
    public DeviceData findByImei(String imei);
}
