package com.kakaopay.throwaway.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class RandomTokenUtilImpl implements RandomTokenUtil {
    @Override
    public String makeToken(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
