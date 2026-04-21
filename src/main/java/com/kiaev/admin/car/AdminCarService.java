package com.kiaev.admin.car;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCarService {

    private final CarRepository carRepository;

    public List<Car> getCars() {
        return carRepository.findAll(Sort.by(Sort.Direction.DESC, "carNo")).stream()
                .filter(car -> !"삭제".equals(car.getSaleStatus()))
                .toList();
    }

    public Car getCar(Long carNo) {
        return carRepository.findById(carNo)
                .orElseThrow(() -> new IllegalArgumentException("차량을 찾을 수 없습니다. ID: " + carNo));
    }

    public Car createEmptyCar() {
        Car car = Car.builder().build();
        car.setSaleStatus("판매중");
        return car;
    }

    public void save(Car car) {
        if (car.getPriceDisplay() == null || car.getPriceDisplay().isBlank()) {
            car.setPriceDisplay(formatPrice(car.getPrice()));
        }

        if (car.getSaleStatus() == null || car.getSaleStatus().isBlank()) {
            car.setSaleStatus("판매중");
        }

        carRepository.save(car);
    }

    public void delete(Long carNo) {
        Car car = getCar(carNo);
        car.setSaleStatus("삭제");
        carRepository.save(car);
    }

    private String formatPrice(Long price) {
        if (price == null) {
            return "";
        }
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price) + "원";
    }
}
