package com.spring.gpsApiData.dao;

import com.spring.gpsApiData.entities.historyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.gpsApiData.entities.gpsData;

import java.util.UUID;

@Repository
public interface historyDataDao extends JpaRepository<historyData, UUID>{

}
