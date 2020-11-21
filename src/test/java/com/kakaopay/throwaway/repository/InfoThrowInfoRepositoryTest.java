package com.kakaopay.throwaway.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class InfoThrowInfoRepositoryTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

//    @Test
//    @DisplayName("될까요?")
//    protected ResultActions sprinkle(String roomID, long userId, long amount, int cnt) throws Exception{
//        RequestInfo requestInfo = new RequestInfo();
//        requestInfo.setAmount(amount);
//        requestInfo.setCnt(cnt);
//
//        return mockMvc.perform(
//                post("/api").contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .header(Header.ROOM_ID, roomID).header(Header.USER_ID, userId)
//
//        )
//                .andDo(print());
//    }

}