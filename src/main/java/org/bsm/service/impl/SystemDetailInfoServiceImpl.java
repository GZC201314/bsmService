package org.bsm.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.*;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bsm.service.ISystemDetailInfoService;
import org.springframework.stereotype.Service;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-11-09
 */
@Slf4j
@Service
public class SystemDetailInfoServiceImpl implements ISystemDetailInfoService {


    /**
     * 获取系统详细信息
     */
    @Override
    public JSONObject getSystemDetailInfo() {
        JSONObject result = new JSONObject();

        /*获取java虚拟机的信息*/
        JvmSpecInfo jvmSpecInfo = SystemUtil.getJvmSpecInfo();

        result.put("jvmSpecInfo", jvmSpecInfo);

        /*Java Virtual Machine Implementation信息*/
        JvmInfo jvmInfo = SystemUtil.getJvmInfo();
        result.put("jvmInfo", jvmInfo);
        /*Java Specification信息*/
        JavaSpecInfo javaSpecInfo = SystemUtil.getJavaSpecInfo();
        result.put("javaSpecInfo", javaSpecInfo);
        /*Java运行时信息*/
        JavaInfo javaInfo = SystemUtil.getJavaInfo();
        result.put("javaInfo", javaInfo);

        /*Java运行时信息*/
        JavaRuntimeInfo javaRuntimeInfo = SystemUtil.getJavaRuntimeInfo();
        result.put("javaRuntimeInfo", javaRuntimeInfo);

        /*系统信息*/
        OsInfo osInfo = SystemUtil.getOsInfo();
        result.put("osInfo", osInfo);

        /*用户信息*/
        UserInfo userInfo = SystemUtil.getUserInfo();
        result.put("userInfo", userInfo);

        /*当前主机网络地址信息*/
        HostInfo hostInfo = SystemUtil.getHostInfo();
        result.put("hostInfo", hostInfo);

        /*运行时信息，包括内存总大小、已用大小、可用大小等*/
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        JSONObject runtimeInfoJson = new JSONObject();
        runtimeInfoJson.put("maxMemory", FileUtil.readableFileSize(runtimeInfo.getMaxMemory()));
        runtimeInfoJson.put("totalMemory", FileUtil.readableFileSize(runtimeInfo.getTotalMemory()));
        runtimeInfoJson.put("freeMemory", FileUtil.readableFileSize(runtimeInfo.getFreeMemory()));
        runtimeInfoJson.put("usableMemory", FileUtil.readableFileSize(runtimeInfo.getUsableMemory()));
        result.put("runtimeInfo", runtimeInfoJson);

        /*JVM 类加载信息*/
        ClassLoadingMXBean classLoadingMXBean = SystemUtil.getClassLoadingMXBean();
        result.put("classLoadingMXBean", classLoadingMXBean);
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = SystemUtil.getGarbageCollectorMXBeans();
        result.put("garbageCollectorMXBeans", garbageCollectorMXBeans);
        int totalThreadCount = SystemUtil.getTotalThreadCount();
        result.put("totalThreadCount", totalThreadCount);


        return result;
    }
}
