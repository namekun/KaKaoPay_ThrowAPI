package com.kakaopay.throwaway.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import static java.lang.Math.min;
import static java.lang.Math.toIntExact;

@Component
public class PublicUtilImpl implements PublicUtil {
    @Override
    public String makeToken(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    /**
     * 금액을 나눈다.
     * @return
     */
    @Override
    public long[] divide(long amount, long count) {
        long[] array = new long[toIntExact(count)];
        long max = RandomUtils.nextLong(amount / count, amount / count * 2);
        for (int i = 0; i < count - 1; i++) {
            array[i] = RandomUtils.nextLong(1, min(max, amount));
            amount -= array[i];
        }
        array[toIntExact(count - 1)] = amount;
        return array;
    }

}
