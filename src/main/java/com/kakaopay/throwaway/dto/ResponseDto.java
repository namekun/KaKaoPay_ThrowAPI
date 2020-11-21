package com.kakaopay.throwaway.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseDto implements Serializable {
    private static final long serialVersionUID = 7691561665550618864L;

    public String responseCode;
    public String responseDescription;
    public Object value;

    public ResponseDto(String responseCode, String responseDescription, Object value) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.value = value;
    }
}
