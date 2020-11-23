package com.kakaopay.throwaway;

import com.kakaopay.throwaway.staticcode.HeaderCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("요청 헤더값 관련 테스트")
public class ThrowRequestTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("ROOM-ID 식별값 누락 테스트")
    void nonRoomId() throws Exception {
        //when //then
        mockMvc.perform(
                post("/throwing")
                .header(HeaderCodes.USERID, "12345"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("USER-ID 식별값 누락 테스트")
    void nonUserId() throws Exception {
        //when //then
        mockMvc.perform(
                post("/throwing")
                        .header(HeaderCodes.ROOMID, "room1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}
