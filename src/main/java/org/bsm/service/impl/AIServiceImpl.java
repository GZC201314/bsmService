package org.bsm.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.User;
import org.bsm.mapper.UserMapper;
import org.bsm.pagemodel.AipFaceResult;
import org.bsm.pagemodel.PageUpload;
import org.bsm.pagemodel.Tpsbresult;
import org.bsm.pagemodel.Words_result;
import org.bsm.service.IAIService;
import org.bsm.utils.AIInstance;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GZC
 */
@Slf4j
@Service
public class AIServiceImpl implements IAIService {

    @Autowired
    UserMapper userMapper;

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
    public AipFaceResult facelogin(PageUpload pageUpload) throws IOException {
        String FACE_APP_ID = (String) redisUtil.get("FACE_APP_ID");
        String FACE_API_KEY = (String) redisUtil.get("FACE_API_KEY");
        String FACE_SECRET_KEY = (String) redisUtil.get("FACE_SECRET_KEY");
        Encoder encoder = Base64.getEncoder();
        AipFace aipFace = AIInstance.getFaceInstance(FACE_APP_ID, FACE_API_KEY, FACE_SECRET_KEY);
        org.json.JSONObject resultJson = aipFace.search(encoder.encodeToString(pageUpload.getFile().getBytes()), "BASE64", "test", null);
        return JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);
    }


    @Override
    public boolean faceReg(PageUpload pageUpload) throws IOException {

        String FACE_APP_ID = (String) redisUtil.get("FACE_APP_ID");
        String FACE_API_KEY = (String) redisUtil.get("FACE_API_KEY");
        String FACE_SECRET_KEY = (String) redisUtil.get("FACE_SECRET_KEY");

        AipFace aipFace = AIInstance.getFaceInstance(FACE_APP_ID, FACE_API_KEY, FACE_SECRET_KEY);
        //获取用户的人脸识别信息
        Map<Object, Object> userInfo = redisUtil.hmget(pageUpload.getSessionId());
        String username = (String) userInfo.get("username");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User reUser = userMapper.selectOne(queryWrapper);

        Encoder encoder = Base64.getEncoder();

        //获取登录的用户名
        org.json.JSONObject resultJson = aipFace.addUser(encoder.encodeToString(pageUpload.getFile().getBytes()), "BASE64", "test", reUser.getUsername(), null);
        AipFaceResult aipFaceResult = JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);
        /*判断人脸注册是否成功*/
        if (aipFaceResult != null && aipFaceResult.getError_code() == 0) {
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", reUser.getUsername());
            reUser.setIsfacevalid(true);
            reUser.setLastmodifytime(LocalDateTime.now());
            int updateCount = userMapper.update(reUser, updateWrapper);
            return updateCount > 0;
        }
        return false;
    }
}
