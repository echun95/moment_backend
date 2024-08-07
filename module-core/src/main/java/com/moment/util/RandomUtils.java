package com.moment.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$";
    private static final int USER_CODE_LENGTH = 6;
    private static final int TEMPORARY_PASSWORD_LENGTH = 8;


    // 랜덤으로 숫자 생성
    public int createRandomNumber() {
        return (int) (Math.random() * 100000) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }
    // 랜덤으로 문자 + 숫자 생성
    public String createUserCode() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(USER_CODE_LENGTH);

        for (int i = 0; i < USER_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }
    public String generateTemporaryPassword() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(TEMPORARY_PASSWORD_LENGTH);
        for (int i = 0; i < TEMPORARY_PASSWORD_LENGTH; i++) {
            String randomStr = CHARACTERS + SPECIAL_CHARACTERS;
            int index = random.nextInt(randomStr.length());
            stringBuilder.append(randomStr.charAt(index));
        }
        return stringBuilder.toString();
    }
}
