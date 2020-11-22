package com.kakaopay.throwaway.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name= "receive_info")
public class ReceiveEntity implements Serializable {

    private static final long serialVersionUID = 6383807555985579131L;

    @Id
    @GeneratedValue
    @NonNull
    @Column(name = "object_id")
    private Long objectId;

    @NonNull
    @Column(name = "token")
    private String token;

    @Setter
    @Column(name = "user_id")
    private Long userId;

    @NonNull
    @Column(name = "money")
    private Long amount;

    @Setter
    @Column(name = "receive_dttm")
    private LocalDateTime dateTime;

    public ReceiveEntity(String token, long l) {
        this.token = token;
        this.amount = l;
    }
}
