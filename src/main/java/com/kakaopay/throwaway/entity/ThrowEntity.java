package com.kakaopay.throwaway.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name= "throw_info")
public class ThrowEntity{

    @Id
    @NonNull
    @Column(name="token")
    private String token; // 토큰

    @NonNull
    @Column(name = "user_id")
    private Long userId; // 유저 아이디

    @NonNull
    @Column(name = "room_id")
    private String roomId; // 방 번호

    @NonNull
    @Column(name = "amount_money")
    private Long amount; // 뿌릴 돈의 총합

    @NonNull
    @Column(name = "people_cnt")
    private Long peopleCnt; // 나눠 받을 수 있는 사람의 수

    @NonNull
    @Column(name = "reg_dttm")
    private LocalDateTime dateTime; // 뿌리기 등록 시간
}
