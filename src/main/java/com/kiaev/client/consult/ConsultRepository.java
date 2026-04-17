package com.kiaev.client.consult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultRepository extends JpaRepository<Consult, Long> {

    Optional<Consult> findByConsultNoAndMemberNo(Long consultNo, Long memberNo);

    List<Consult> findByMemberNoOrderByRequestDateDesc(Long memberNo);
}