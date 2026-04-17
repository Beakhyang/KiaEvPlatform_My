package com.kiaev.cbclient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotClientRepository extends JpaRepository<ChatInquiry, Long> { }