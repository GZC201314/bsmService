package org.bsm.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.User;
import org.bsm.pagemodel.*;
import org.bsm.service.IAIService;
import org.bsm.utils.AIInstance;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class AIServiceImpl implements IAIService {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String ocr(PageUpload pageUpload) {
        log.info("ocr 接口开始执行");
        StringBuilder result = new StringBuilder();
        if (pageUpload.getFile() != null) {
            // 保存
            try {
                // 初始化一个AipOcr
                String OCR_APP_ID = (String) redisUtil.get("OCR_APP_ID");
                String OCR_API_KEY = (String) redisUtil.get("OCR_API_KEY");
                String OCR_SECRET_KEY = (String) redisUtil.get("OCR_SECRET_KEY");
                AipOcr client = AIInstance.getOcrInstance(OCR_APP_ID, OCR_API_KEY, OCR_SECRET_KEY);

                if (client != null) {
                    // 调用接口
                    MultipartFile file = pageUpload.getFile();

                    HashMap<String, String> map = new HashMap<>();
                    org.json.JSONObject res = client.basicGeneral(file.getBytes(), map);
                    Tpsbresult tpsbresult = JSONObject.parseObject(res.toString(2), Tpsbresult.class);
                    if (tpsbresult != null) {
                        List<Words_result> list = tpsbresult.getWords_result();
                        for (Words_result wordsResult : list) {
                            result.append(wordsResult.getWords());
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }

        return result.toString();
    }


    @Override
    public AipFaceResult facelogin(PageUser pageUser) {
        String FACE_APP_ID = (String) redisUtil.get("FACE_APP_ID");
        String FACE_API_KEY = (String) redisUtil.get("FACE_API_KEY");
        String FACE_SECRET_KEY = (String) redisUtil.get("FACE_SECRET_KEY");

        AipFace aipFace = AIInstance.getFaceInstance(FACE_APP_ID, FACE_API_KEY, FACE_SECRET_KEY);
        org.json.JSONObject resultJson = aipFace.search(pageUser.getBase(), "BASE64", "test", null);
        return JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);
    }


    @Override
    public AipFaceResult faceReg(PageUser pageUser) {

        String FACE_APP_ID = (String) redisUtil.get("FACE_APP_ID");
        String FACE_API_KEY = (String) redisUtil.get("FACE_API_KEY");
        String FACE_SECRET_KEY = (String) redisUtil.get("FACE_SECRET_KEY");

        AipFace aipFace = AIInstance.getFaceInstance(FACE_APP_ID, FACE_API_KEY, FACE_SECRET_KEY);
        //获取用户的人脸识别信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        User reUser = userService.getOne(queryWrapper);

        reUser.setIsfacevalid(true);
        reUser.setLastmodifytime(LocalDateTime.now());

        //获取登录的用户名
        org.json.JSONObject resultJson = aipFace.addUser(pageUser.getBase(), "BASE64", "test", pageUser.getUsername(), null);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", reUser.getUsername());
        userService.update(reUser, updateWrapper);
        return JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);
    }
}
