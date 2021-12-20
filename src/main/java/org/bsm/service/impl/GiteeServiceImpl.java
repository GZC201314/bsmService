package org.bsm.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.service.IGiteeService;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.bsm.utils.Constants.CREATE_REPOS_URL;

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
        log.info("开始向gitee图床上传图片：" + pageGiteeApiCaller);
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
        String accessToken = (String) redisUtil.get("GITEE_ACCESS_TOKEN");
        // String | 分支名称。默认为仓库对默认分支
        String branch = pageGiteeApiCaller.getBranch();
        // String | Committer的名字，默认为当前用户的名字
        String committerName = pageGiteeApiCaller.getCommitterName();
        // String | Committer的邮箱，默认为当前用户的邮箱
        String committerEmail = pageGiteeApiCaller.getCommitterEmail();
        // String | Author的名字，默认为当前用户的名字
        String authorName = pageGiteeApiCaller.getAuthorName();
        // String | Author的邮箱，默认为当前用户的邮箱
        String authorEmail = pageGiteeApiCaller.getAuthorEmail();

        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("access_token", accessToken);
            paramMap.put("message", message);
            paramMap.put("content", content);


            String requestUrl = String.format(CREATE_REPOS_URL, owner,
                    repo, path);


            String resultJson = HttpUtil.post(requestUrl, paramMap);

            JSONObject jsonObject = JSONUtil.parseObj(resultJson);

            JSONObject resultContent = jsonObject.getJSONObject("content");
            String download_url = resultContent.getStr("download_url");
            if (StringUtils.hasText(download_url)) {
                log.info("图片上传成功，图片的地址是：" + download_url);
                return download_url;
            }
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("Exception when calling RepositoriesApi#postV5ReposOwnerRepoContentsPath Error message is:" + e.getMessage());
        }
        return "";
    }
}
