package com.hd.usercenter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @auther hd
 * @Description
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("String","hdhd");
        valueOperations.set("int",1);
        valueOperations.set("Double",2.0);
        Object string = valueOperations.get("String");
        Assertions.assertTrue("hdhd".equals((String) string));
    }
}
