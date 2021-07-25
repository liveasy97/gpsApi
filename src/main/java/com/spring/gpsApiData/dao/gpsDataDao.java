package com.spring.gpsApiData.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.gpsApiData.entities.gpsData;

@Repository
public interface gpsDataDao extends JpaRepository<gpsData,String>{

}
