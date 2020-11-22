package com.kakaopay.throwaway.service;

import com.kakaopay.throwaway.controller.ThrowRequest;
import com.kakaopay.throwaway.dto.ResponseDto;
import com.kakaopay.throwaway.dto.RetrieveDto;
import com.kakaopay.throwaway.dto.RetrieveInfoDto;
import com.kakaopay.throwaway.entity.ReceiveEntity;
import com.kakaopay.throwaway.entity.ThrowEntity;
import com.kakaopay.throwaway.staticcode.ResponseCodes;
import com.kakaopay.throwaway.repository.ReceiveInfoRepository;
import com.kakaopay.throwaway.repository.ThrowInfoRepository;
import com.kakaopay.throwaway.util.PublicUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThrowServiceImpl implements ThrowService {

    private final PublicUtil publicUtil;
    private final ThrowInfoRepository throwInfoRepository;
    private final ReceiveInfoRepository receiveInfoRepository;

    @Transactional
    @Override
    public ThrowEntity throwing(String roomId, long userId, ThrowRequest throwRequest) {
        String token = publicUtil.makeToken(3);
        ThrowEntity throwEntity = new ThrowEntity(token, userId, roomId, throwRequest.amount, throwRequest.count, LocalDateTime.now());
        throwInfoRepository.save(throwEntity);

        long[] divide = publicUtil.divide(throwRequest.amount, throwRequest.count);
        for (long l : divide) {
            // 상세 테이블에 저장
            ReceiveEntity receiveEntity = new ReceiveEntity(token, l);
            receiveInfoRepository.save(receiveEntity);
        }
        return throwEntity;
    }

    @Transactional
    @Override
    public ResponseDto receiving(String roomId, long userId, String token) {
        ThrowEntity throwEntityByToken = throwInfoRepository.findOneByToken(token);
        List<ReceiveEntity> receiveEntityList = receiveInfoRepository.findByToken(token);

        ResponseDto result = new ResponseDto(null, null, null);
        ReceiveEntity representInfo = null;

        // 기본 데이터 세팅
        ResponseCodes responseCode = ResponseCodes.S200;

        // 유효하지 않은 토큰값.
        if(receiveEntityList.size()==0){
            responseCode = ResponseCodes.E107;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        for (ReceiveEntity receiveEntity : receiveEntityList) {
            if (receiveEntity.getDateTime() == null) {
                representInfo = receiveEntity;
                break;
            }
        }

        // 그렇게 해도 null 이라면? -> 이미 뿌리기가 끝났다는 것
        if (representInfo == null) {
            responseCode = ResponseCodes.E101;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 본인이 뿌린거라면?
        if (throwEntityByToken.getUserId() == userId) {
            responseCode = ResponseCodes.E102;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 10분이 지났다면?
        if (LocalDateTime.now().isAfter(throwEntityByToken.getDateTime().plusMinutes(10))) {
            responseCode = ResponseCodes.E103;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 방번호가 틀리다면?
        if (!Objects.equals(throwEntityByToken.getRoomId(), roomId)) {
            responseCode = ResponseCodes.E104;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 이미 한번 주운 사용자라면?
        ReceiveEntity isAlreadyTake = receiveInfoRepository.findByTokenAndUserId(token, userId);
        if (isAlreadyTake != null) {
            responseCode = ResponseCodes.E105;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        result.setResponseCode(responseCode.code);
        result.setResponseDescription(responseCode.response);

        // 데이터를 가져왔으면, 해당 데이터의 receive_dttm 를 update
        representInfo.setDateTime(LocalDateTime.now());
        representInfo.setUserId(userId);
        receiveInfoRepository.save(representInfo);
        result.setValue(representInfo.getAmount());

        return result;
    }

    /**
     * 조회 기능
     * @param userId :
     * @param token
     * @return
     */
    @Override
    public ResponseDto retrieving(long userId, String token) {
        // token 값으로 throw_info 와 receive_info 에서 데이터를 join 해서 가져온다
        // 주의할 점은 뿌린 사람 자신만이 조회할 수 있음.
        // 조회하는 사람이 뿌린사람 본인이 아니거나, token에 해당되는 뿌리기 건이 없다면 실패응답

        // 현재 상태에 해당되는 변수
        RetrieveDto retrieveDto;
        ArrayList<RetrieveInfoDto> receiverInfoList = new ArrayList<>();
        long receivedMoney = 0;

        ThrowEntity throwEntity = throwInfoRepository.findOneByToken(token);

        ResponseCodes responseCode = ResponseCodes.S200;

        // token 값으로 조회되는 값이 없다면?
        if (throwEntity == null) {
            responseCode = ResponseCodes.E107;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 뿌린자와 요청한자의 id가 다를경우?
        if (throwEntity.getUserId() != userId) {
            responseCode = ResponseCodes.E106;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 뿌린지 7일이 지났다면?
        if (LocalDateTime.now().isAfter(throwEntity.getDateTime().plusDays(7))) {
            responseCode = ResponseCodes.E103;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        List<ReceiveEntity> receiveEntityList = receiveInfoRepository.findByToken(token);
        for (ReceiveEntity receiveEntity : receiveEntityList) {
            // 시간이 있는 컬럼은 누군가가 받아간 컬럼
            if (receiveEntity.getDateTime() != null) {
                RetrieveInfoDto retrieveInfoDto = new RetrieveInfoDto();
                retrieveInfoDto.setUserId(receiveEntity.getUserId());
                retrieveInfoDto.setReceivedMoney(receiveEntity.getAmount());
                receivedMoney += receiveEntity.getAmount();
                receiverInfoList.add(retrieveInfoDto);
            }
        }

        retrieveDto = new RetrieveDto(
                throwEntity.getDateTime(),
                throwEntity.getAmount()
                , receivedMoney,
                receiverInfoList);

        return new ResponseDto(responseCode.code, responseCode.response, retrieveDto);
    }
}
