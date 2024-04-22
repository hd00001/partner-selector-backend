package com.hd.usercenter.Utils;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @auther hd
 * @Description
 */
class AlgorithmUtilsTest {

    @Test
    void test() {
        String str1 = "hd是h";
        String str2 = "hd不是h";
        String str3 = "hd是d不是h";
        // 1
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        // 3
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        System.out.println(score1);
        System.out.println(score2);
    }
}