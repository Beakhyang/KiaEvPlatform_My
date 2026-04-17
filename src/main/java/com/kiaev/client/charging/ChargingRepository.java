package com.kiaev.client.charging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargingRepository extends JpaRepository<Charging, String> {
    List<Charging> findByStatNmContainingIgnoreCaseOrAddrContainingIgnoreCaseOrAddrDetailContainingIgnoreCaseOrLocationDescContainingIgnoreCase(
            String statNmKeyword,
            String addrKeyword,
            String addrDetailKeyword,
            String locationDescKeyword
    );
}
