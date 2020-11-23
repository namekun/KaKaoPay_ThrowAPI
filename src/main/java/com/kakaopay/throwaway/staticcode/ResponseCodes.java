package com.kakaopay.throwaway.staticcode;

public enum ResponseCodes {
    S_200("200", "정상처리"),
    E_101("101", "이미 종료된 뿌리기"),
    E_102("102", "뿌린 사용자와 같은 사용자"),
    E_103("103", "시간 초과"),
    E_104("104", "잘못된 방번호"),
    E_105("105", "이미 주운 사용자"),
    E_106("106", "뿌린 사용자와 다른 사용자"),
    E_107("107", "유효하지 않는 토큰값"),
    E_108("108", "동시성 제어 에러, 재시도 요청 필요");

    public final String code;
    public final String response;

    ResponseCodes(String code, String response) {
        this.code = code;
        this.response = response;
    }
}
