package org.bsm.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.bsm.common.BsmException;
import org.bsm.utils.IPUtil;
import org.bsm.utils.RedisUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


@Slf4j
@Component
public class BlackIPInterceptor implements HandlerInterceptor {

    private static final int time = 30000;
    private static final int count = 10;

    private final RedisUtil redisUtil;

    public BlackIPInterceptor(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求的url
        String url = request.getRequestURI();
        log.info("url==={}", url);
        String ip = IPUtil.getIpAddress(request);
        // 这边应该做缓存，防止每次都访问数据库
        String blockedIPKey = "blockedIPKey:" + ip;
        Object domain = redisUtil.get(blockedIPKey);
        if (domain != null) {
            redisUtil.set(blockedIPKey, (int)domain + 1, time / 100);
            throw new BsmException(String.valueOf(HttpStatus.FORBIDDEN.value()), "当前IP已经被锁，请等待解锁后重试！");
        }

        // 先查询redis中是否有这个键
        String key = ("blackipList:" + ip + url
                // 先查询redis中是否有这个键
        ).replaceAll("/", ".");
        Object value = redisUtil.get(key);
        if (Objects.isNull(value)) {
            // 为空则插入新数据
            redisUtil.set(key, 1, time/1000);
        } else {
            int valueInt = (int) value;
            if (valueInt < count) {
                // 没有超过就累加
                long redisTime = redisUtil.getExpire(key);
                redisUtil.set(key, (valueInt + 1), redisTime);
            } else {
                // 超过访问次数
                Object count = redisUtil.get(ip);
                if (Objects.isNull(count)) {
                    redisUtil.set(ip, 1,time/1000);
                } else {
                    int countInt = (int) count;
                    if (countInt <= 5) {
                        redisUtil.set(ip, (countInt + 1));
                    } else {
                        // 超过访问次数 5次以上 进入黑名单
                        redisUtil.set(blockedIPKey, countInt, time / 100);
                        redisUtil.del(ip);
                    }
                }
            }
        }
        return true;
    }

}

