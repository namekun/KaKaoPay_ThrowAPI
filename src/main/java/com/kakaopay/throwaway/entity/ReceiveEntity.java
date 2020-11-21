package com.kakaopay.throwaway.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@DynamicUpdate
@Table(name= "receive_info")
public class ReceiveEntity implements Serializable {

    private static final long serialVersionUID = 6383807555985579131L;

    @Id
    @GeneratedValue
    @Column(name = "object_id", nullable = false)
    private long objectId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "money",nullable = false)
    private long amount;

    @Column(name = "receive_dttm")
    private LocalDateTime dateTime;

    public ReceiveEntity(String token, long l) {
        this.token = token;
        this.amount = l;
    }

    public ReceiveEntity() {

    }
}
