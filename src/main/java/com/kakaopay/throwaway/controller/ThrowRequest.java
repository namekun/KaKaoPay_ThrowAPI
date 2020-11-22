package com.kakaopay.throwaway.controller;

import lombok.Data;

@Data
public class ThrowRequest {
    public long amount;
    public long count;
}
