package com.kakaopay.throwaway.service;

import com.kakaopay.throwaway.controller.ThrowRequest;
import com.kakaopay.throwaway.dto.ResponseDto;
import com.kakaopay.throwaway.entity.ThrowEntity;
import lombok.RequiredArgsConstructor;

public interface ThrowService {

    ThrowEntity throwing(String roomId, long userId, ThrowRequest throwRequest);

    ResponseDto receiving(String roomId, long userId, String token);

    ResponseDto retrieving(long userId, String token);
}
