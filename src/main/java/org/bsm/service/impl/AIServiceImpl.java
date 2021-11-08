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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class AIServiceImpl implements IAIService {

    @Autowired
    UserServiceImpl userService;

    @Override
    public String uploadHeadIcon(PageUpload pageUpload) {
        StringBuilder result = new StringBuilder();
        if (pageUpload.getUpload() != null) {
            // 保存
            try {
                // 初始化一个AipOcr
                AipOcr client = AIInstance.getOcrInstance();

                if (client != null) {
                    // 调用接口
                    String path = pageUpload.getUpload().getCanonicalPath();
                    HashMap<String, String> map = new HashMap<>();
                    org.json.JSONObject res = client.basicGeneral(path, map);
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
        AipFace aipFace = AIInstance.getFaceInstance();
        org.json.JSONObject resultJson = aipFace.search(pageUser.getBase(), "BASE64", "test", null);
        AipFaceResult result = JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);
        return result;
    }


    @Override
    public AipFaceResult faceReg(PageUser pageUser) {
        AipFace aipFace = AIInstance.getFaceInstance();
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
