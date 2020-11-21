package com.kakaopay.throwaway.enums;

public enum ResponseCodes {
    S200("200", "정상처리"),
    E101("101", "종료된 뿌리기"),
    E102("102", "뿌린 사용자와 같은 사용자"),
    E103("103", "시간 초과"),
    E104("104", "잘못된 방번호"),
    E105("105", "이미 주운 사용자"),
    E106("106", "뿌린 사용자와 다른 사용자"),
    E107("107", "일치하지 않는 토큰값");

    public final String code;
    public final String response;

    ResponseCodes(String code, String response) {
        this.code = code;
        this.response = response;
    }
}
