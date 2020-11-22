package com.kakaopay.throwaway.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name= "receive_info")
public class ReceiveEntity {

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

    // 동시성 제어를 위한 version
    @Version
    @Column(name = "version")
    private int version;

    public ReceiveEntity(String token, long l) {
        this.token = token;
        this.amount = l;
    }

}
