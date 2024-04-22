package com.hd.usercenter.once;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hd.usercenter.mapper.UserMapper;
import com.hd.usercenter.model.domain.User;
import com.hd.usercenter.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @auther hd
 * @Description
 */
// 变成spring bean
@Component
public class InsertUsers {

    @Resource
    private UserService userService;

    /**
     * 批量插入用户 ，  执行在测试
     * 打定时任务注解
     */

    private void doInsertUsers() {
//        计时工具
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakehd");
            user.setAvatarUrl("https://img.ixintu.com/download/jpg/20200901/3e9ce3813b7199ea9588eeb920f41208_512_512.jpg!ys");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("1234141");
            user.setEmail("212121@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

}
