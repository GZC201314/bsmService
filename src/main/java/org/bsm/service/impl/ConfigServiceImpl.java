package org.bsm.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Config;
import org.bsm.mapper.ConfigMapper;
import org.bsm.service.IConfigService;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-11-09
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

    @Resource
    RedisUtil redisUtil;

    /**
     * 新增配置信息
     */
    @Override
    public boolean addConfig(Config config) {

        save(config);
        /*
         * 同步redis缓存中的配置信息
         * */
        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
        configMap.put(config.getName(), config.getValue());
        Map<String, Object> convert = Convert.convert(new TypeReference<Map<String, Object>>() {
        }, configMap);
        redisUtil.hmset(Constants.BSM_CONFIG, convert);
        return true;
    }

    /**
     * 修改配置信息,不允许修改配置名
     */
    @Override
    public boolean editConfig(Config config) {
        boolean b = updateById(config);
        boolean result = false;
        if (b) {
            Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
            Map<String, Object> convert = Convert.convert(new TypeReference<Map<String, Object>>() {
            }, configMap);
            convert.put(config.getName(), config.getValue());
            result = redisUtil.hmset(Constants.BSM_CONFIG, convert);
        }
        return result;
    }

    /**
     * 删除配置信息
     */
    @Override
    public boolean delConfig(Config config) {
        boolean b = removeById(config.getId());
        boolean result = false;
        if (b) {
            Map<Object, Object> hmget = redisUtil.hmget(Constants.BSM_CONFIG);
            hmget.remove(config.getName());
            Map<String, Object> convert = Convert.convert(new TypeReference<Map<String, Object>>() {
            }, hmget);
            result = redisUtil.hmset(Constants.BSM_CONFIG, convert);
        }
        return result;
    }

    /**
     * 批量删除配置信息
     */
    @Override
    public boolean delConfigs(String delIds) {
        String[] split = delIds.split(",");
        List<String> ids = Arrays.asList(split);
        List<Config> configs = listByIds(ids);
        boolean b = removeByIds(Arrays.asList(split));
        Map<Object, Object> cacheConfig = redisUtil.hmget(Constants.BSM_CONFIG);
        if (b) {
            for (Config config : configs) {
                cacheConfig.remove(config.getName());
                redisUtil.hdel(Constants.BSM_CONFIG, config.getName());
            }
        }
        return true;
    }
}
