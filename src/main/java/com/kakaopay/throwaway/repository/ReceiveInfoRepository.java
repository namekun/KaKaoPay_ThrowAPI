package com.kakaopay.throwaway.repository;

import com.kakaopay.throwaway.entity.ReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReceiveInfoRepository extends JpaRepository<ReceiveEntity, Long> {
    List<ReceiveEntity> findByToken(String token);
    ReceiveEntity findByTokenAndUserId(String token, long userId);

    // for Test
    @Transactional
    void deleteAllByToken(String token);
}
