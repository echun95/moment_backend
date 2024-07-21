package com.moment.redis;

import com.moment.redis.service.RedisServiceImpl;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
public class RedisTest {
    @Autowired
    RedisServiceImpl redisService;

//    @Test
    void insertRedisValue(){
        redisService.setValues("testKey", "testValue");
        String testKey = redisService.getValue("testKey");
        Assertions.assertThat(testKey).isEqualTo("testValue");
    }
//        @Test
    void redisValueExpired(){
        redisService.setValues("testKey", "testValue", Duration.ofSeconds(5));
        String testKey = redisService.getValue("testKey");
        Assertions.assertThat(testKey).isEqualTo("testValue");
    }
}
