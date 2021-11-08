package org.bsm.utils;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;

public class AIInstance {

    //设置ocr APPID/AK/SK
    public static final String OCR_APP_ID = "18595904";
    public static final String OCR_API_KEY = "1OlEjVz6Zck7h4kdCpSu2GDX";
    public static final String OCR_SECRET_KEY = "1gylrnGUjKdUbFSqUyGd54LwUCCUStno";

    //设置face APPID/AK/SK
    public static final String FACE_APP_ID = "21907238";
    public static final String FACE_API_KEY = "zp59ZHTomg8PrzKGPPHTZH5g";
    public static final String FACE_SECRET_KEY = "hLE2H2XVOKHDhYA38XL5yw24ztrWvBCY";
    //类初始化时，不初始化这个对象(延时加载，真正用的时候再创建)
    private static AipOcr ocrInstance = new AipOcr(OCR_APP_ID, OCR_API_KEY, OCR_SECRET_KEY);

    private static AipFace faceInstance = new AipFace(FACE_APP_ID, FACE_API_KEY, FACE_SECRET_KEY);

    //构造器私有化
    private AIInstance() {
    }

    //方法同步，调用效率低
    public static synchronized AipOcr getOcrInstance() {
        return ocrInstance;
    }

    public static synchronized AipFace getFaceInstance() {
        return faceInstance;
    }
}