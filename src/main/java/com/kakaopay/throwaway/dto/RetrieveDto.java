package com.kakaopay.throwaway.dto;

import lombok.Data;
import lombok.NonNull;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RetrieveDto implements Serializable {

    private static final long serialVersionUID = 153851064371484202L;

    private LocalDateTime throwTime;
    private long moneyAmount;
    private long receivedMoneyAmount;
    private List<RetrieveInfoDto> retrieveInfoList;

    public RetrieveDto(LocalDateTime throwTime, long moneyAmount, long receivedMoneyAmount, List<RetrieveInfoDto> retrieveInfoList) {
        this.throwTime = throwTime;
        this.moneyAmount = moneyAmount;
        this.receivedMoneyAmount = receivedMoneyAmount;
        this.retrieveInfoList = retrieveInfoList;
    }
}
