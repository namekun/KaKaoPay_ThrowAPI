package com.kakaopay.throwaway.repository;

import com.kakaopay.throwaway.entity.ThrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThrowInfoRepository extends JpaRepository<ThrowEntity, String> {
    ThrowEntity findOneByToken(String token);
}
