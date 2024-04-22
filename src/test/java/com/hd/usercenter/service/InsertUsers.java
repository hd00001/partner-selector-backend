package com.hd.usercenter.service;

import com.hd.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

/**
 * @auther hd
 * @Description  用户插入单元测试，注意打包时要删掉或忽略，不然打一次包就插入一次
 */
@SpringBootTest
public class InsertUsers {
    @Resource
    private UserService userService;

    //线程设置
    private ExecutorService executorService = new ThreadPoolExecutor(16, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     * 打定时任务注解
     */
//    @Test
//    public void doInsertUsers() {
////        计时工具
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 1000;
//         List<User> userList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假用户");
//            user.setUserAccount("fakehd");
//            user.setAvatarUrl("https://img.ixintu.com/download/jpg/20200901/3e9ce3813b7199ea9588eeb920f41208_512_512.jpg!ys");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setPhone("1234141");
//            user.setEmail("212121@qq.com");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            user.setTags("[]");
//            userList.add(user);
//        }
//        userService.saveBatch(userList, 100);
//        stopWatch.stop();
//        System.out.println(stopWatch.getLastTaskTimeMillis());
//    }

//    /**
//     * 并发批量插入用户
//     * 打定时任务注解
//     */
//    @Test
//    public void doConcurrencyInsertUsers() {
////        计时工具
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 1000;
////        分组
//        int j = 0;
//        //批量插入数据的大小
//        int batchSize = 5000;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM / batchSize; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true){
//                j++;
//                User user = new User();
//                user.setUsername("假用户");
//                user.setUserAccount("fakehd");
//                user.setAvatarUrl("https://img.ixintu.com/download/jpg/20200901/3e9ce3813b7199ea9588eeb920f41208_512_512.jpg!ys");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("1234141");
//                user.setEmail("212121@qq.com");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setTags("[]");
//                userList.add(user);
//                if (j % batchSize == 0) {
//                    break;
//                }
//            }
////            异步操作 使用CompletableFuture开启异步任务
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println("ThreadName：" + Thread.currentThread().getName());
//                userService.saveBatch(userList, batchSize);
//            }, executorService);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getLastTaskTimeMillis());
//    }


    }

