package com.kakaopay.throwaway.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RetrieveDto implements Serializable {

    private static final long serialVersionUID = 153851064371484202L;

    private final LocalDateTime throwTime;
    private final long moneyAmount;
    private final long receivedMoneyAmount;
    private final List<RetrieveInfoDto> retrieveInfoList;
}
