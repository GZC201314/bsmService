package org.bsm.service.impl;

import com.gitee.ApiException;
import com.gitee.api.RepositoriesApi;
import com.gitee.model.CommitContent;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.service.IGiteeService;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public boolean addFile(PageGiteeApiCaller pageGiteeApiCaller) {
        log.info("开始向gitee图床上传图片：" + pageGiteeApiCaller);
        RepositoriesApi apiInstance = new RepositoriesApi();
        String owner = pageGiteeApiCaller.getOwner();
        String repo = pageGiteeApiCaller.getRepo();
        String path = pageGiteeApiCaller.getPath();
        String content = pageGiteeApiCaller.getContent();
        String message = pageGiteeApiCaller.getMessage();
        /*为了安全，从redis中获取accessToken*/
        String accessToken = (String) redisUtil.get("GITEE_ACCESS_TOKEN");
        String branch = pageGiteeApiCaller.getBranch();
        String committerName = pageGiteeApiCaller.getCommitterName();
        String committerEmail = pageGiteeApiCaller.getCommitterEmail();
        String authorName = pageGiteeApiCaller.getAuthorName();
        String authorEmail = pageGiteeApiCaller.getAuthorEmail();

        try {
            CommitContent result = apiInstance.postV5ReposOwnerRepoContentsPath(owner, repo, path, content, message, accessToken, branch, committerName, committerEmail, authorName, authorEmail);
            if (StringUtils.hasText(result.getContent().getDownloadUrl())) {
                log.info("图片上传成功，图片的地址是：" + result.getContent().getDownloadUrl());
                return true;
            }
        } catch (ApiException e) {
            log.error("Exception when calling RepositoriesApi#postV5ReposOwnerRepoContentsPath Error message is:" + e.getMessage());
        }
        return false;
    }
}
