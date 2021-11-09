package org.bsm.utils;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;

public class AIInstance {

    //类初始化时，不初始化这个对象(延时加载，真正用的时候再创建)
    private static AipOcr ocrInstance = null;

    private static AipFace faceInstance = null;

    //构造器私有化
    private AIInstance() {
    }

    //方法同步，调用效率低
    public static synchronized AipOcr getOcrInstance(String appId, String appKey, String appSecret) {
        if (ocrInstance == null) {
            ocrInstance = new AipOcr(appId, appKey, appSecret);
        }
        return ocrInstance;
    }

    public static synchronized AipFace getFaceInstance(String appId, String appKey, String appSecret) {
        if (faceInstance == null) {
            faceInstance = new AipFace(appId, appKey, appSecret);
        }
        return faceInstance;
    }
}