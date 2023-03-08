package org.bsm.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Updateimginfo;
import org.bsm.pagemodel.PageUpdatePicture;
import org.bsm.service.IUpdateimginfoService;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class ImgtuUtil {
    @Resource
    RedisUtil redisUtil;

    @Resource
    IUpdateimginfoService updateimginfoService;

    String uid = "";
    String token = "";
    final String url = "https://www.imgurl.org/api/v2/upload";

    /**
     * 上传图片
     */
    public Updateimginfo uploadPicture(MultipartFile file) throws IOException {
        if (!StringUtils.hasText(uid)||!StringUtils.hasText(token)){
            Map<Object, Object> bsm_config = redisUtil.hmget("bsm_config");
            uid = (String)bsm_config.get("IMG_UID");
            token = (String)bsm_config.get("IMG_TOKEN");
        }
        //获取当前jar 的执行路径
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        String file1 = jarFile.getParent();
        log.info("file1,{}",file1);
        String fileName = file.getOriginalFilename();
        log.info("fileName,{}",fileName);
        assert fileName != null;
        File tempFile = new File(file1+fileName);
        file.transferTo(tempFile);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid",uid);
        paramMap.put("token",token);
        paramMap.put("file",tempFile);


        String post1 = HttpUtil.post(url, paramMap);
        JSONObject jsonObject = JSONObject.parseObject(post1);
        if (jsonObject.getInteger("code") != 200){
            return new Updateimginfo();
        }
        JSONObject data = jsonObject.getJSONObject("data");
        Updateimginfo updateimginfo = JSON.parseObject(data.toJSONString(), Updateimginfo.class);
        log.info("保存的图片信息，{}",updateimginfo);
        updateimginfoService.save(updateimginfo);
        if (tempFile.exists()){
            if (tempFile.delete()) {
                log.info("文件删除成功，{}",tempFile.getAbsolutePath());
            }
        }
        return updateimginfo;
    }
    /**
     * 删除图片
     */
    public void deletePicture(String fileUrl){
        if (!StringUtils.hasText(fileUrl)){
            return;
        }
        QueryWrapper<Updateimginfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",fileUrl);
        Updateimginfo one = updateimginfoService.getOne(queryWrapper);
        // 找到当前的图片删除路径
        if (one != null){
            String delete = one.getDelete();
            if (StringUtils.hasText(delete)){
                HttpUtil.get(delete);
                updateimginfoService.remove(queryWrapper);
            }
        }
    }


}

