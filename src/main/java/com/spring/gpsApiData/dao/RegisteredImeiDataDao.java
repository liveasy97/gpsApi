//package com.spring.gpsApiData.dao;
//
//import com.spring.gpsApiData.entities.RegisteredImeiData;
//import com.spring.gpsApiData.entities.historyData;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface RegisteredImeiDataDao extends JpaRepository<RegisteredImeiData, UUID>{
//
//	 @Query("select t from RegisteredImeiData t where t.status = 'active'")
//	public List<RegisteredImeiData> findEnabledImei();
//
//	public boolean existsByImei(String imei);
//}
