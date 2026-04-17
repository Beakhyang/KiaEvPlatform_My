package com.kiaev.client.car;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;

    public List<Car> searchCars(String keyword, String carType, String sort) {
        String searchKeyword = (keyword == null) ? "" : keyword;
        String searchType = (carType == null) ? "" : carType;

        Sort sortOrder;

        if ("latest".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "carNo");
        } else if ("priceAsc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rangeDesc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "drivingRangeKm");
        } else if ("nameAsc".equals(sort)) { // 명시적으로 모델명 순 선택 시
            sortOrder = Sort.by(Sort.Direction.ASC, "modelName");
        } else {
            // 기본값 (null 등) 역시 모델명 순
            sortOrder = Sort.by(Sort.Direction.ASC, "modelName");
        }

        return carRepository.findByModelNameContainingAndCarTypeContaining(searchKeyword, searchType, sortOrder);
    }

    public Car findCarById(Long carNo) {
        return carRepository.findById(carNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 없음. ID: " + carNo));
    }
    
    // 전체 차량 목록 조회 메서드
    public List<Car> findAll() {
    	 return carRepository.findAll(Sort.by(Sort.Direction.ASC, "modelName"));
    }
}