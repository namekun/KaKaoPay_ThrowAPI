package com.kakaopay.throwaway.service;

import com.kakaopay.throwaway.controller.ThrowRequest;
import com.kakaopay.throwaway.dto.ResponseDto;
import com.kakaopay.throwaway.dto.RetrieveDto;
import com.kakaopay.throwaway.dto.RetrieveInfoDto;
import com.kakaopay.throwaway.entity.ReceiveEntity;
import com.kakaopay.throwaway.entity.ThrowEntity;
import com.kakaopay.throwaway.repository.ReceiveInfoRepository;
import com.kakaopay.throwaway.repository.ThrowInfoRepository;
import com.kakaopay.throwaway.staticcode.ResponseCodes;
import com.kakaopay.throwaway.util.PublicUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThrowServiceImpl implements ThrowService {

    private final PublicUtil publicUtil;
    private final ThrowInfoRepository throwInfoRepository;
    private final ReceiveInfoRepository receiveInfoRepository;

    /**
     * 뿌리기 기능
     *
     * @param roomId       : 방 번호
     * @param userId       : 뿌리는 유저 아이디
     * @param throwRequest : 뿌리기 요청 [뿌리는 돈 , 받을 수 있는 사람 수]
     * @return
     */
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

    /**
     * 받기 기능
     *
     * @param roomId : 방 번호
     * @param userId : 받는 유저의 아이디
     * @param token  : 토큰
     * @return
     */
    @Transactional
    @Override
    public ResponseDto receiving(String roomId, long userId, String token) {
        ThrowEntity throwEntityByToken = throwInfoRepository.findOneByToken(token);
        List<ReceiveEntity> receiveEntityList = receiveInfoRepository.findByToken(token);

        ResponseDto result = new ResponseDto(null, null, null);
        ReceiveEntity representInfo = null;

        // 기본 데이터 세팅
        ResponseCodes responseCode = ResponseCodes.S_200;

        // 유효하지 않은 토큰값.
        if (receiveEntityList.size() == 0) {
            responseCode = ResponseCodes.E_107;
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
            responseCode = ResponseCodes.E_101;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 본인이 뿌린거라면?
        if (throwEntityByToken.getUserId() == userId) {
            responseCode = ResponseCodes.E_102;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 10분이 지났다면?
        if (LocalDateTime.now().isAfter(throwEntityByToken.getDateTime().plusMinutes(10))) {
            responseCode = ResponseCodes.E_103;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 방번호가 틀리다면?
        if (!Objects.equals(throwEntityByToken.getRoomId(), roomId)) {
            responseCode = ResponseCodes.E_104;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 이미 한번 주운 사용자라면?
        ReceiveEntity isAlreadyTake = receiveInfoRepository.findByTokenAndUserId(token, userId);
        if (isAlreadyTake != null) {
            responseCode = ResponseCodes.E_105;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        result.setResponseCode(responseCode.code);
        result.setResponseDescription(responseCode.response);

        // 데이터를 가져왔으면, 해당 데이터의 receive_dttm 를 update
        representInfo.setDateTime(LocalDateTime.now());
        representInfo.setUserId(userId);
        try {
            receiveInfoRepository.save(representInfo);
        } catch (ObjectOptimisticLockingFailureException e) { // 동시성 에러 발생시, catch 문으로 응답을 날려준다.
            responseCode = ResponseCodes.E_108;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }
        result.setValue(representInfo.getReceivedMoney());

        return result;
    }

    /**
     * 조회 기능
     *
     * @param userId : 뿌렸던 유저 아이디
     * @param token  : 토큰 값
     * @return
     */
    @Override
    public ResponseDto retrieving(long userId, String token) {
        // 현재 상태에 해당되는 변수
        RetrieveDto retrieveDto;
        ArrayList<RetrieveInfoDto> receiverInfoList = new ArrayList<>();
        long receivedMoney = 0;

        ThrowEntity throwEntity = throwInfoRepository.findOneByToken(token);

        ResponseCodes responseCode = ResponseCodes.S_200;

        // token 값으로 조회되는 값이 없다면?
        if (throwEntity == null) {
            responseCode = ResponseCodes.E_107;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 뿌린자와 요청한자의 id가 다를경우?
        if (throwEntity.getUserId() != userId) {
            responseCode = ResponseCodes.E_106;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        // 뿌린지 7일이 지났다면?
        if (LocalDateTime.now().isAfter(throwEntity.getDateTime().plusDays(7))) {
            responseCode = ResponseCodes.E_103;
            return new ResponseDto(responseCode.code, responseCode.response, null);
        }

        List<ReceiveEntity> receiveEntityList = receiveInfoRepository.findByToken(token);
        for (ReceiveEntity receiveEntity : receiveEntityList) {
            if (receiveEntity.getUserId() != null && receiveEntity.getDateTime()!=null) {
                RetrieveInfoDto retrieveInfoDto = new RetrieveInfoDto();
                retrieveInfoDto.setUserId(receiveEntity.getUserId());
                retrieveInfoDto.setReceivedMoney(receiveEntity.getReceivedMoney());
                receivedMoney += receiveEntity.getReceivedMoney();
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
