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
    private String token;

    @NonNull
    @Column(name = "user_id")
    private Long userId;

    @NonNull
    @Column(name = "room_id")
    private String roomId;

    @NonNull
    @Column(name = "amount_money")
    private Long amount;

    @NonNull
    @Column(name = "people_cnt")
    private Long peopleCnt;

    @NonNull
    @Column(name = "reg_dttm")
    private LocalDateTime dateTime;

}
