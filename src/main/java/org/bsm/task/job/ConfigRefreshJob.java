package org.bsm.task.job;

import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Config;
import org.bsm.service.IConfigService;
import org.bsm.utils.RedisUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 每五分钟刷新一下配置缓存
 *
 * @author GZC
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
@Component
public class ConfigRefreshJob extends QuartzJobBean {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IConfigService configService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        List<Config> list = configService.list();
        redisUtil.del("bsm_config");
        for (Config config : list) {
            redisUtil.hset("bsm_config", config.getName(), config.getValue());
        }
    }
}
