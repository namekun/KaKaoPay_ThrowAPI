package com.kakaopay.throwaway.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name= "receive_info")
public class ReceiveEntity {

    @Id
    @GeneratedValue
    @NonNull
    @Column(name = "object_id")
    private Long objectId; // 구분을 위한 pk

    @NonNull
    @Column(name = "token")
    private String token; // 토큰 값

    @Setter
    @Column(name = "user_id")
    private Long userId; // 유저 아이디

    @NonNull
    @Column(name = "money")
    private Long receivedMoney; // 받는 돈의 액수

    @Setter
    @Column(name = "receive_dttm")
    private LocalDateTime dateTime; // 유저가 돈을 받아간 시간

    // 동시성 제어를 위한 version
    @Version
    @Column(name = "version")
    private Integer version;

    public ReceiveEntity(String token, long l) {
        this.token = token;
        this.receivedMoney = l;
    }

}
