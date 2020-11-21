package com.kakaopay.throwaway.entity;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "throw_info")
public class ThrowEntity implements Serializable {

    private static final long serialVersionUID = -1907520853136143564L;

    @Id
    @NonNull
    @Column(name="token")
    private String token;

    @NonNull
    @Column(name = "user_id")
    private long userId;

    @NonNull
    @Column(name = "room_id")
    private String roomId;

    @NonNull
    @Column(name = "amount_money")
    private long amount;

    @NonNull
    @Column(name = "people_cnt")
    private long peopleCnt;

    @NonNull
    @Column(name = "reg_dttm")
    private LocalDateTime dateTime;

    public ThrowEntity(String token, String roomId, long userId, long amount, long cnt, LocalDateTime dateTime) {
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
        this.amount = amount;
        this.peopleCnt = cnt;
        this.dateTime = dateTime;
    }

    public ThrowEntity() {

    }


}
