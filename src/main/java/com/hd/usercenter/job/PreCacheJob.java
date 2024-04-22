package com.hd.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hd.usercenter.model.domain.User;
import com.hd.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @auther hd
 * @Description
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

//        重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

//    每天执行，加载预热用户
    @Scheduled(cron = "1 6 0 * *  *")
    public void doCacheRecommendUser(){

        RLock lock = redissonClient.getLock("hd:precache:user:docache:lock");
        try {
//                只有一个线程能获取锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                for (Long userId : mainUserList){
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        如果有缓存，直接读缓存
                String redisKey = String.format("hd:user:recommed:%S", userId);
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                Page<User> userPage = userService.page(new Page<>(1, 20),wrapper);
//      写缓存
                try {
                    valueOperations.set(redisKey, userPage,50000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("redis set ket error", e);
                }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error ",e);
        } finally {
//               只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }
}
