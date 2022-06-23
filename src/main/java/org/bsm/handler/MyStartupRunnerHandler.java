package org.bsm.handler;

import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Config;
import org.bsm.service.IConfigService;
import org.bsm.service.impl.ConfigServiceImpl;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GZC
 * @create 2021-11-09 22:14
 * @desc 项目初始化加载类
 */
@Component
@Slf4j
public class MyStartupRunnerHandler implements CommandLineRunner {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IConfigService configService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始执行项目初始化后的数据加载");
        /*把数据库中的配置信息都初始化到redis中去*/
        List<Config> configs = configService.list();
        Map<String, Object> configMap = new HashMap<>();
        for (Config config : configs) {
            configMap.put(config.getName(), config.getValue());
        }
        redisUtil.del("bsm_config");
        redisUtil.hmset("bsm_config", configMap);
    }
}
