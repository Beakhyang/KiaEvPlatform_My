package com.kiaev.client.car;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    
    // [차량 통합 검색 전용 쿼리] 모델명 키워드 포함 + 차량 유형 포함 + 정렬 조건을 조합하여 데이터 조회
    List<Car> findByModelNameContainingAndCarTypeContaining(String modelName, String carType, Sort sort);

}