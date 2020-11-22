package com.kakaopay.throwaway.repository;

import com.kakaopay.throwaway.entity.ThrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ThrowInfoRepository extends JpaRepository<ThrowEntity, String> {
    ThrowEntity findOneByToken(String token);

    // for test
    @Transactional
    void deleteAllByToken(String abc);
}
