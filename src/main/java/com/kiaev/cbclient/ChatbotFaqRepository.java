package com.kiaev.cbclient;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotFaqRepository extends JpaRepository<ChatbotFaq, Long> {

    List<ChatbotFaq> findByActiveYn(String activeYn, Sort sort);
}
