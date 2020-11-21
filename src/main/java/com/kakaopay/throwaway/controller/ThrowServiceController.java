package com.kakaopay.throwaway.controller;

import com.kakaopay.throwaway.dto.ResponseDto;
import com.kakaopay.throwaway.entity.ThrowEntity;
import com.kakaopay.throwaway.enums.ResponseCodes;
import com.kakaopay.throwaway.repository.ThrowInfoRepository;
import com.kakaopay.throwaway.service.ThrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ThrowServiceController {

    private final ThrowService throwService;
    private final ThrowInfoRepository throwInfoRepository;

    //헤더에 담겨올 요청자 식별값과, 대화방 id
    public static final String ROOMID = "X-ROOM-ID";
    public static final String USERID = "X-USER-ID";

    @Autowired
    public ThrowServiceController(ThrowService throwService, ThrowInfoRepository throwInfoRepository) {
        this.throwService = throwService;
        this.throwInfoRepository = throwInfoRepository;
    }


    @PostMapping("/throwing")
    ResponseDto throwing(@RequestHeader(ROOMID) String roomId,
                                         @RequestHeader(USERID) long userId,
                                         @RequestBody ThrowRequest throwRequest,
                                         UriComponentsBuilder uriComponentsBuilder) {
        ResponseCodes apiResponseCode;
        ThrowEntity throwEntity = throwService.throwing(roomId, userId, throwRequest);
        throwInfoRepository.save(throwEntity);
        apiResponseCode = ResponseCodes.S200;

        return new ResponseDto(apiResponseCode.code, apiResponseCode.response, throwEntity.getToken());
    }

    @PatchMapping(value = "/receiving/{token:[A-Za-z]{3}}")
    ResponseDto receiving(@RequestHeader(ROOMID) String roomId,
                          @RequestHeader(USERID) long userId,
                          @PathVariable String token){
        return throwService.receiving(roomId, userId, token);
    }

    @GetMapping(value = "/receiving/{token:[A-Za-z]{3}}")
    ResponseDto receivingInfo(@RequestHeader(USERID) long userId,
                              @PathVariable String token){
        return throwService.retrieving(userId, token);
    }

}
