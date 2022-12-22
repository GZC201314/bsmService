package org.bsm.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.User;
import org.bsm.mapper.UserMapper;
import org.bsm.pagemodel.*;
import org.bsm.service.IAIService;
import org.bsm.utils.AIInstance;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Base64.Encoder;

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
    public String ocr(PageUpload pageUpload, HttpServletResponse response) {
        StringBuilder result = new StringBuilder();
        if (pageUpload.getFile() != null) {
            // 保存
            try {
                // 初始化一个AipOcr
                Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);


                String ocrAppId = (String) configMap.get(Constants.OCR_APP_ID);
                String ocrApiKey = (String) configMap.get(Constants.OCR_API_KEY);
                String ocrSecretKey = (String) configMap.get(Constants.OCR_SECRET_KEY);
                AipOcr client = AIInstance.getOcrInstance(ocrAppId, ocrApiKey, ocrSecretKey);

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

        /*输出流到前台*/
//        try {
//            String fileName = "ocr.txt";
//
//            File directory = new File("");//参数为空
//
//            String path = directory.getAbsolutePath();
//            File file=new File(path+"/"+fileName);
//            FileUtil.writeBytes(result.toString().getBytes(),file);
//            InputStream inputStream=new BufferedInputStream(new FileInputStream(file));
//            response.setContentType("application/octet-stream");
//
//            response.setHeader("content-disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
//            response.addHeader("Access-Control-Expose-Headers","fileName");
//            response.addHeader("fileName", URLEncoder.encode(fileName, "UTF-8"));
//            response.setCharacterEncoding("UTF-8");
//            ServletOutputStream servletOutputStream = response.getOutputStream();
//            int len;
//            byte[] buffer = new byte[1024];
//            while ((len = inputStream.read(buffer)) > 0) {
//                servletOutputStream.write(buffer, 0, len);
//            }
//            servletOutputStream.flush();
//            inputStream.close();
//            servletOutputStream.close();
//            file.delete();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }

        return result.toString();
    }


    @Override
    public AipFaceResult facelogin(PageUpload pageUpload) throws IOException {

        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);

        String faceAppId = (String) configMap.get(Constants.FACE_APP_ID);
        String faceApiKey = (String) configMap.get(Constants.FACE_API_KEY);
        String faceSecretKey = (String) configMap.get(Constants.FACE_SECRET_KEY);
        Encoder encoder = Base64.getEncoder();
        AipFace aipFace = AIInstance.getFaceInstance(faceAppId, faceApiKey, faceSecretKey);
        org.json.JSONObject resultJson = aipFace.search(encoder.encodeToString(pageUpload.getFile().getBytes()), "BASE64", "test", null);
        AipFaceResult aipFaceResult = JSONObject.parseObject(resultJson.toString(), AipFaceResult.class);

        if (aipFaceResult.getError_code() == 0) {
            if (aipFaceResult.getResult() != null && aipFaceResult.getResult().getUser_list() != null && aipFaceResult.getResult().getUser_list().get(0) != null) {
                User_list user_list = aipFaceResult.getResult().getUser_list().get(0);
                if (user_list.getScore() >= 80) {
                    return aipFaceResult;
                }
            }
        }
        return null;
    }


    @Override
    public boolean faceReg(PageUpload pageUpload) throws IOException {

        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
        String faceAppId = (String) configMap.get(Constants.FACE_APP_ID);
        String faceApiKey = (String) configMap.get(Constants.FACE_API_KEY);
        String faceSecretKey = (String) configMap.get(Constants.FACE_SECRET_KEY);

        AipFace aipFace = AIInstance.getFaceInstance(faceAppId, faceApiKey, faceSecretKey);

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

    @Override
    public List<FaceLib> getallFaceLib(PageUpload pageUpload) throws IOException {
        List<FaceLib> ans = new ArrayList<>();
        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
        String faceAppId = (String) configMap.get(Constants.FACE_APP_ID);
        String faceApiKey = (String) configMap.get(Constants.FACE_API_KEY);
        String faceSecretKey = (String) configMap.get(Constants.FACE_SECRET_KEY);

        AipFace aipFace = AIInstance.getFaceInstance(faceAppId, faceApiKey, faceSecretKey);
        org.json.JSONObject groupList = aipFace.getGroupList(new HashMap<>());
        org.json.JSONObject result = groupList.getJSONObject("result");
        JSONArray groupIdList = result.getJSONArray("group_id_list");
        int length = groupIdList.length();
        for (int i = 0; i < length; i++) {
            String groupId = groupIdList.getString(i);
            // 根据GroupId获取人脸库信息
            org.json.JSONObject groupUsers = aipFace.getGroupUsers(groupId, new HashMap<>());
            org.json.JSONObject groupUsersresult = groupUsers.getJSONObject("result");
            JSONArray userIdList = groupUsersresult.getJSONArray("user_id_list");
            int userLen = userIdList.length();
            for (int j = 0; j < userLen; j++) {
                String userId = userIdList.getString(j);
                FaceLib faceLib = new FaceLib(groupId, userId);
                ans.add(faceLib);
            }
        }

        return ans;
    }
}
