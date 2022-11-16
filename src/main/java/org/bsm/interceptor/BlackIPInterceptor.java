package org.bsm.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.bsm.common.BsmException;
import org.bsm.utils.IPUtil;
import org.bsm.utils.RedisUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class BlackIPInterceptor implements HandlerInterceptor {

    private static final int time = 30000;
    private static final int count = 10;

    @Resource
    private RedisUtil redisUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求的url
        String url = request.getRequestURI();
        String ip = IPUtil.getIpAddress(request);
        // 这边应该做缓存，防止每次都访问数据库
        String blockedIPKey = "blockedIPKey:"+ip;
        Object domain = redisUtil.get(blockedIPKey);
        if (domain != null) {
            redisUtil.set(blockedIPKey,Integer.parseInt((String)domain)+1,time/100);
            throw new BsmException(String.valueOf(HttpStatus.FORBIDDEN.value()), "当前IP已经被锁，请等待解锁后重试！");
        }

        // 先查询redis中是否有这个键
        String key = ("blackipList:" + ip + url
                // 先查询redis中是否有这个键
        ).replaceAll("/", ".");
        String value = (String)redisUtil.get(key);
        if (StringUtils.isBlank(value)) {
            // 为空则插入新数据
            redisUtil.set(key, "1", time);
        } else {
            if (Integer.parseInt(value) < count) {
                // 没有超过就累加
                long redisTime = redisUtil.getExpire(key);
                redisUtil.set(key, (Integer.parseInt(value) + 1) + "", redisTime);
            } else {
                // 超过访问次数
                String cou = (String)redisUtil.get(ip);
                if (StringUtils.isBlank(cou)) {
                    redisUtil.set(ip, "1");
                } else {
                    if (Integer.parseInt(cou) <= 5) {
                        redisUtil.set(ip, (Integer.parseInt(cou) + 1) + "");
                    } else {
                        // 超过访问次数 5次以上 进入黑名单
                        redisUtil.set(blockedIPKey, cou,time/100);
                        redisUtil.del(ip);
                    }
                }
            }
        }
        return true;
    }

}

