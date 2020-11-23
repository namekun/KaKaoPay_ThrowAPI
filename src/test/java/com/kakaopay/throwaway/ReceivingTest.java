package com.kakaopay.throwaway;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.throwaway.entity.ReceiveEntity;
import com.kakaopay.throwaway.entity.ThrowEntity;
import com.kakaopay.throwaway.repository.ReceiveInfoRepository;
import com.kakaopay.throwaway.repository.ThrowInfoRepository;
import com.kakaopay.throwaway.staticcode.HeaderCodes;
import com.kakaopay.throwaway.staticcode.ResponseCodes;
import com.kakaopay.throwaway.util.PublicUtil;
import com.kakaopay.throwaway.util.PublicUtilImpl;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DisplayName("받기 기능 테스트")
public class ReceivingTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ThrowInfoRepository throwInfoRepository;

    @Autowired
    ReceiveInfoRepository receiveInfoRepository;

    PublicUtil publicUtil = new PublicUtilImpl();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print()).build();
        // 토큰을 지정해서 데이터를 넣어준다.
        ThrowEntity throwEntity = new ThrowEntity("ABC", 123L, "room_test_receiving", 3000L, 3L, LocalDateTime.now());
        throwInfoRepository.save(throwEntity);
        long[] divided = publicUtil.divide(3000L, 3L);
        for (long l : divided) {
            // 상세 테이블에 저장
            ReceiveEntity receiveEntity = new ReceiveEntity("ABC", l);
            receiveInfoRepository.save(receiveEntity);
        }
    }

    @Test
    @DisplayName("받기 기능 정상 작동 확인")
    void isReceived() throws Exception {
        //given
        String roomId = "room_test_receiving";
        String userId = "1234";
        String token = "ABC";
        
        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.S_200.code));
    }

    @Test
    @DisplayName("토큰 값이 다른 경우")
    void isWrongToken() throws Exception {
        //given
        String roomId = "room_test_receiving";
        String userId = "1234";
        String token = "EFG";

        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_107.code));
    }

    @Test
    @DisplayName("방 아이디가 다른 경우")
    void isWrongRoom() throws Exception {
        //given
        String roomId = "room_wrong_num";
        String userId = "1234";
        String token = "ABC";

        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_104.code));
    }

    @Test
    @DisplayName("뿌린 사용자는 주을 수 없습니다.")
    void isSameUser() throws Exception {

        // given
        String roomId = "room_test_receiving";
        String userId = "123";
        String token = "ABC";

        // 미리 받아가도록 저장.
        ReceiveEntity receiveEntity = receiveInfoRepository.findByToken(token).get(0);
        receiveEntity.setUserId(1234L);
        receiveEntity.setDateTime(LocalDateTime.now());
        receiveInfoRepository.save(receiveEntity);

        // when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_102.code));
    }

    @Test
    @DisplayName("이미 주운 사용자 입니다.")
    void isAlreadyTaken() throws Exception {

        // given
        String roomId = "room_test_receiving";
        String userId = "1234";
        String token = "ABC";

        // 미리 받아가도록 저장.
        ReceiveEntity receiveEntity = receiveInfoRepository.findByToken(token).get(0);
        receiveEntity.setUserId(1234L);
        receiveEntity.setDateTime(LocalDateTime.now());
        receiveInfoRepository.save(receiveEntity);

        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_105.code));
    }


    @Test
    @DisplayName("뿌리기 등록한 뒤 10분이 지난 경우")
    void isTimeOver() throws Exception {
        // given
        ThrowEntity throwEntity = throwInfoRepository.findOneByToken("ABC");
        throwEntity.setDateTime(throwEntity.getDateTime().minusMinutes(10).minusSeconds(1)); // 현재가 xx1.9xxx 이고 10분전이 xxx2.0xxx 가 되는 경우가 있어서 minus 1sec을 해주었다.
        throwInfoRepository.save(throwEntity);

        String roomId = "room_test_receiving";
        String userId = "321";
        String token = "ABC";

        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_103.code));
    }

    @Test
    @DisplayName("뿌리기가 끝난 경우")
    void isEndThrowing() throws Exception {
        // given
        List<ReceiveEntity> receiveEntities = receiveInfoRepository.findByToken("ABC");
        for (ReceiveEntity receiveEntity : receiveEntities) {
            if (receiveEntity.getDateTime() == null && receiveEntity.getUserId() == null) {
                receiveEntity.setDateTime(LocalDateTime.now());
                receiveEntity.setUserId(RandomUtils.nextLong());
                receiveInfoRepository.save(receiveEntity);
            }
        }

        String roomId = "room_test_receiving";
        String userId = "321";
        String token = "ABC";

        //when
        mockMvc.perform(
                patch("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E_101.code));
    }
}
