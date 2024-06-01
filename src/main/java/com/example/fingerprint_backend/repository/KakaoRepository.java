package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.KakaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoRepository extends JpaRepository<KakaoEntity, String> {
}
