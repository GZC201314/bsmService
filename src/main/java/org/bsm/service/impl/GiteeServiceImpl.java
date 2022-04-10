package org.bsm.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.service.IGiteeService;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.bsm.utils.Constants.CREATE_REPOS_URL;
import static org.bsm.utils.Constants.GET_REPOSFILE_URL;

/**
 * @author GZC
 * @create 2021-11-18 12:51
 * @desc gitee service 实现类
 */
@Service
@Slf4j
public class GiteeServiceImpl implements IGiteeService {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String addFile(PageGiteeApiCaller pageGiteeApiCaller) {
        log.info("开始向gitee图床上传文件：" + pageGiteeApiCaller);
        // String | 仓库所属空间地址(企业、组织或个人的地址path)
        String owner = pageGiteeApiCaller.getOwner();
        //String | 仓库路径(path)
        String repo = pageGiteeApiCaller.getRepo();
        // String | 文件的路径
        String path = pageGiteeApiCaller.getPath();
        // String | 文件内容, 要用 base64 编码
        String content = pageGiteeApiCaller.getContent();
        // String | 提交信息
        String message = pageGiteeApiCaller.getMessage();
        /*为了安全，从redis中获取accessToken*/
        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
        String accessToken = (String) configMap.get(Constants.GITEE_ACCESS_TOKEN);

        try {
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("access_token", accessToken);
            paramMap.put("message", message);
            paramMap.put("content", content);


            String requestUrl = String.format(CREATE_REPOS_URL, owner,
                    repo, path);


            String resultJson = HttpUtil.post(requestUrl, paramMap);

            JSONObject jsonObject = JSONUtil.parseObj(resultJson);

            JSONObject resultContent = jsonObject.getJSONObject("content");
            String downloadUrl = resultContent.getStr("download_url");
            if (StringUtils.hasText(downloadUrl)) {
                log.info("文件上传成功，文件的地址是：" + downloadUrl);
                return downloadUrl;
            }
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("Exception when calling RepositoriesApi#postV5ReposOwnerRepoContentsPath Error message is:" + e.getMessage());
        }
        return "";
    }

    /**
     * 判断gitee 图床上是否有对应的文件，如果存在则返回对应文件的Url
     */
    @Override
    public String getFile(PageGiteeApiCaller pageGiteeApiCaller) {

        log.info("获取gitee上的文件：" + pageGiteeApiCaller);
        // String | 仓库所属空间地址(企业、组织或个人的地址path)
        String owner = pageGiteeApiCaller.getOwner();
        //String | 仓库路径(path)
        String repo = pageGiteeApiCaller.getRepo();
        // String | 文件的路径
        String path = pageGiteeApiCaller.getPath();
        /*为了安全，从redis中获取accessToken*/
        Map<Object, Object> configMap = redisUtil.hmget(Constants.BSM_CONFIG);
        String accessToken = (String) configMap.get(Constants.GITEE_ACCESS_TOKEN);

        try {
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("access_token", accessToken);


            String requestUrl = String.format(GET_REPOSFILE_URL, owner,
                    repo, path);


            String resultJson = HttpUtil.get(requestUrl, paramMap);

            if ("[]".equals(resultJson)) {
                return "";
            }
            JSONObject jsonObject = JSONUtil.parseObj(resultJson);
            String downloadUrl = jsonObject.getStr("download_url");
            if (StringUtils.hasText(downloadUrl)) {
                log.info("获取文件，文件的地址是：" + downloadUrl);
                return downloadUrl;
            }
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("Exception when calling RepositoriesApi#postV5ReposOwnerRepoContentsPath Error message is:" + e.getMessage());
        }
        return "";
    }
}
