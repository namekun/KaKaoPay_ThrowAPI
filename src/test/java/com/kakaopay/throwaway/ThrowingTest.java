package com.kakaopay.throwaway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.throwaway.controller.ThrowRequest;
import com.kakaopay.throwaway.repository.ReceiveInfoRepository;
import com.kakaopay.throwaway.repository.ThrowInfoRepository;
import com.kakaopay.throwaway.staticcode.ResponseCodes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.kakaopay.throwaway.staticcode.HeaderCodes.ROOMID;
import static com.kakaopay.throwaway.staticcode.HeaderCodes.USERID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("뿌리기 관련 테스트")
public class ThrowingTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ThrowInfoRepository throwInfoRepository;

    @Autowired
    ReceiveInfoRepository receiveInfoRepository;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print()).build();
    }

    @Test
    @DisplayName("뿌리기 기능 정상 작동 확인")
    void isSave() throws Exception {
        // given
        String roomId = "room_test";
        String userId = "1234";
        ThrowRequest throwRequest = new ThrowRequest();
        throwRequest.setCount(5);
        throwRequest.setAmount(10000);
        // when

        mockMvc.perform(
                post("/api/throwing")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(ROOMID, roomId)
                        .header(USERID, userId)
                        .content(objectMapper.writeValueAsString(throwRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value(ResponseCodes.S200.code));
    }
}
