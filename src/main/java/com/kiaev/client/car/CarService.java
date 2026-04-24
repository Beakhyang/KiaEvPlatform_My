package com.kiaev.client.car;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final CarImagePathNormalizer carImagePathNormalizer;

    public List<Car> searchCars(String keyword, String carType, String sort) {
        String searchKeyword = keyword == null ? "" : keyword;
        String searchType = carType == null ? "" : carType;

        Sort sortOrder;
        if ("latest".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "carNo");
        } else if ("priceAsc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rangeDesc".equals(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "drivingRangeKm");
        } else {
            sortOrder = Sort.by(Sort.Direction.ASC, "modelName");
        }

        return carRepository.findByModelNameContainingAndCarTypeContaining(searchKeyword, searchType, sortOrder).stream()
                .map(carImagePathNormalizer::normalize)
                .filter(this::isVisibleCar)
                .collect(Collectors.toList());
    }

    public Car findCarById(Long carNo) {
        Car car = carRepository.findById(carNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 없음. ID: " + carNo));

        if (!isVisibleCar(car)) {
            throw new IllegalArgumentException("해당 차량 없음. ID: " + carNo);
        }

        carImagePathNormalizer.normalize(car);
        return car;
    }

    public List<Car> findAll() {
        return carRepository.findAll(Sort.by(Sort.Direction.ASC, "modelName")).stream()
                .map(carImagePathNormalizer::normalize)
                .filter(this::isVisibleCar)
                .collect(Collectors.toList());
    }

    public long countVisibleCars() {
        return carRepository.findAll().stream()
                .filter(this::isVisibleCar)
                .count();
    }

    private boolean isVisibleCar(Car car) {
        return car != null && !"삭제".equals(car.getSaleStatus());
    }
}
