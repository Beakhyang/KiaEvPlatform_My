package com.kiaev.admin.consult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;
import com.kiaev.client.consult.Consult;
import com.kiaev.client.consult.ConsultRepository;
import com.kiaev.client.member.Member;
import com.kiaev.client.member.MemberRepository;
import com.kiaev.dealer.login.DealerLogin;
import com.kiaev.dealer.login.DealerLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminConsultService {

    private final ConsultRepository consultRepository;
    private final MemberRepository memberRepository;
    private final CarRepository carRepository;
    private final DealerLoginRepository dealerLoginRepository;

    public List<AdminConsultView> getConsultList(String statusFilter) {
        List<Consult> consults = consultRepository.findAll(Sort.by(Sort.Direction.DESC, "requestDate"));
        Map<Long, Member> memberMap = loadMembers(consults);
        Map<Long, Car> carMap = loadCars(consults);
        Map<Integer, DealerLogin> dealerMap = loadDealers(consults);

        return consults.stream()
                .map(consult -> toView(consult, memberMap, carMap, dealerMap))
                .filter(view -> statusFilter == null || statusFilter.isBlank() || matchesStatus(view.getConsultStatus(), statusFilter))
                .toList();
    }

    public AdminConsultView getConsultView(Long consultNo) {
        Consult consult = consultRepository.findById(consultNo)
                .orElseThrow(() -> new IllegalArgumentException("상담을 찾을 수 없습니다. ID: " + consultNo));

        Map<Long, Member> memberMap = loadMembers(List.of(consult));
        Map<Long, Car> carMap = loadCars(List.of(consult));
        Map<Integer, DealerLogin> dealerMap = loadDealers(List.of(consult));
        return toView(consult, memberMap, carMap, dealerMap);
    }

    public void updateConsult(Long consultNo, String consultStatus, Integer dealerNo, String consultMemo) {
        Consult consult = consultRepository.findById(consultNo)
                .orElseThrow(() -> new IllegalArgumentException("상담을 찾을 수 없습니다. ID: " + consultNo));

        if (consult.getCarModelNo() == null && consult.getCarNo() != null) {
            consult.setCarModelNo(consult.getCarNo());
        }

        if (dealerNo != null) {
            consult.setDealerNo(dealerNo);
            if (consult.getAssignedDate() == null) {
                consult.setAssignedDate(LocalDateTime.now());
            }
        }

        if (consultMemo != null) {
            consult.setConsultMemo(consultMemo.trim());
        }

        String normalizedStatus = normalizeStatus(consultStatus);
        if (normalizedStatus != null) {
            consult.setConsultStatus(normalizedStatus);

            if ("진행중".equals(normalizedStatus)) {
                if (consult.getAssignedDate() == null) {
                    consult.setAssignedDate(LocalDateTime.now());
                }
                consult.setCompletedDate(null);
            } else if ("완료".equals(normalizedStatus)) {
                if (consult.getAssignedDate() == null) {
                    consult.setAssignedDate(LocalDateTime.now());
                }
                consult.setCompletedDate(LocalDateTime.now());
            } else if ("대기".equals(normalizedStatus)) {
                consult.setCompletedDate(null);
            }
        }

        consultRepository.save(consult);
    }

    private AdminConsultView toView(Consult consult,
                                    Map<Long, Member> memberMap,
                                    Map<Long, Car> carMap,
                                    Map<Integer, DealerLogin> dealerMap) {

        Member member = memberMap.get(consult.getMemberNo());
        Long modelKey = consult.getCarModelNo() != null ? consult.getCarModelNo() : consult.getCarNo();
        Car car = carMap.get(modelKey);

        if (car == null && consult.getCarNo() != null) {
            car = carMap.get(consult.getCarNo());
        }

        DealerLogin dealer = consult.getDealerNo() == null ? null : dealerMap.get(consult.getDealerNo());

        AdminConsultView view = new AdminConsultView();
        view.setConsultNo(consult.getConsultNo());
        view.setMemberNo(consult.getMemberNo());
        view.setMemberName(member != null ? member.getMemberName() : "-");
        view.setMemberEmail(member != null ? member.getEmail() : "-");
        view.setMemberPhone(member != null ? member.getPhone() : "-");
        view.setCarNo(consult.getCarNo());
        view.setCarModelNo(consult.getCarModelNo());
        view.setCarModelName(car != null ? car.getModelName() : "차량 정보 없음");
        view.setDealerNo(consult.getDealerNo());
        view.setDealerName(dealer != null ? dealer.getDealerName() : "미배정");
        view.setDealerEmpNo(dealer != null ? dealer.getDealerEmpNo() : "-");
        view.setBudgetAmount(consult.getBudgetAmount());
        view.setUsePurpose(consult.getUsePurpose());
        view.setMainRangeKm(consult.getMainRangeKm());
        view.setFellowData(consult.getFellowData());
        view.setConsultContent(consult.getConsultContent());
        view.setConsultStatus(consult.getConsultStatus());
        view.setRequestDate(consult.getRequestDate());
        view.setAssignedDate(consult.getAssignedDate());
        view.setCompletedDate(consult.getCompletedDate());
        view.setConsultMemo(consult.getConsultMemo());
        return view;
    }

    private Map<Long, Member> loadMembers(List<Consult> consults) {
        Set<Long> memberNos = consults.stream()
                .map(Consult::getMemberNo)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Member> memberMap = new HashMap<>();
        memberRepository.findAllById(memberNos).forEach(member -> memberMap.put(member.getMemberNo(), member));
        return memberMap;
    }

    private Map<Long, Car> loadCars(List<Consult> consults) {
        Set<Long> carNos = consults.stream()
                .flatMap(consult -> java.util.stream.Stream.of(consult.getCarNo(), consult.getCarModelNo()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Car> carMap = new HashMap<>();
        carRepository.findAllById(carNos).forEach(car -> carMap.put(car.getCarNo(), car));
        return carMap;
    }

    private Map<Integer, DealerLogin> loadDealers(List<Consult> consults) {
        Set<Integer> dealerNos = consults.stream()
                .map(Consult::getDealerNo)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, DealerLogin> dealerMap = new HashMap<>();
        dealerLoginRepository.findAllById(dealerNos).forEach(dealer -> dealerMap.put(dealer.getDealerNo(), dealer));
        return dealerMap;
    }

    private boolean matchesStatus(String currentStatus, String filter) {
        String normalizedCurrent = normalizeStatus(currentStatus);
        String normalizedFilter = normalizeStatus(filter);
        return normalizedCurrent != null && normalizedCurrent.equals(normalizedFilter);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        return switch (status.trim().toUpperCase()) {
        case "WAITING", "대기" -> "대기";
        case "IN_PROGRESS", "진행중" -> "진행중";
        case "COMPLETED", "완료" -> "완료";
        default -> status.trim();
        };
    }
}
