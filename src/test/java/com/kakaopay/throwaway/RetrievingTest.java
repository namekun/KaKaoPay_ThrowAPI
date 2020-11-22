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
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DisplayName("조회 기능 테스트")
public class RetrievingTest {
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
        ThrowEntity throwEntity = new ThrowEntity("ABC", 123L, "room_test_retrieving", 3000L, 3L, LocalDateTime.now());
        throwInfoRepository.save(throwEntity);
        long[] divided = publicUtil.divide(3000L, 3L);
        for (long l : divided) {
            // 상세 테이블에 저장
            ReceiveEntity receiveEntity = new ReceiveEntity("ABC", l);
            receiveInfoRepository.save(receiveEntity);
        }
    }

    @AfterEach
    void after(){
        receiveInfoRepository.deleteAllByToken("ABC");
        throwInfoRepository.deleteAllByToken("ABC");
    }

    @Test
    @DisplayName("조회 기능 정상 작동 확인")
    void isRetrieve() throws Exception {
        // given
        List<ReceiveEntity> receiveEntities = receiveInfoRepository.findByToken("ABC");
        for (ReceiveEntity receiveEntity : receiveEntities) {
            if (receiveEntity.getDateTime() == null && receiveEntity.getUserId() == null) {
                receiveEntity.setDateTime(LocalDateTime.now());
                receiveEntity.setUserId(RandomUtils.nextLong());
                receiveInfoRepository.save(receiveEntity);
            }
        }

        // given
        String roomId = "room_test_retrieving";
        String userId= "123";
        String token = "ABC";

        //when
        mockMvc.perform(
                get("/api/receiving/" + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HeaderCodes.ROOMID, roomId)
                .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.S200.code));
    }

    @Test
    @DisplayName("조회되는 토큰값 없음")
    void nonExistToken() throws Exception {
        // given
        String roomId = "room_test_retrieving";
        String userId= "123";
        String token = "EFG";

        //when
        mockMvc.perform(
                get("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E107.code));
    }

    @Test
    @DisplayName("뿌린 사람과 다른 사람이 조회요청")
    void isCorrectUser() throws Exception {
        // given
        String roomId = "room_test_retrieving";
        String userId= "456";
        String token = "ABC";

        //when
        mockMvc.perform(
                get("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E106.code));
    }

    @Test
    @DisplayName("7일이 지난 뿌리기 조회")
    void after7Days() throws Exception {
        // given
        ThrowEntity throwEntity = throwInfoRepository.findOneByToken("ABC");
        throwEntity.setDateTime(throwEntity.getDateTime().minusDays(7).minusSeconds(1)); // 정말정말 재수없을땐 현재가 xx1.9xxx 이고 7일전이 xxx2.0xxx 가 되는 경우가 있어서 minus 1sec을 해주었다.
        throwInfoRepository.save(throwEntity);

        // given
        String roomId = "room_test_retrieving";
        String userId= "123";
        String token = "ABC";

        //when
        mockMvc.perform(
                get("/api/receiving/" + token)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(HeaderCodes.ROOMID, roomId)
                        .header(HeaderCodes.USERID, userId))
                .andDo(print())
                //then
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.E103.code));
    }

}
