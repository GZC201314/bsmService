package org.bsm.controller;


import cn.hutool.core.codec.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.IGiteeService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "Gitee文件上传控制类")
@Slf4j
@RestController
@RequestMapping("/giteeUpload")
public class GiteeUploadController {


    @Autowired
    private IGiteeService giteeService;


    @ApiOperation("gitee文件上传接口")
    @PostMapping(value = "uploadFile", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> uploadFile(PageUpload pageUpload, HttpServletRequest req) {
        log.info("into the uploadFile function");
        try {
            String sessionId = req.getSession().getId();
            pageUpload.setSessionId(sessionId);


            // 向 Gitee 中文件
            PageGiteeApiCaller pageGiteeApiCaller = new PageGiteeApiCaller();
            MultipartFile file = pageUpload.getFile();
            /*生成文件地址*/
            String fileName = file.getOriginalFilename();
            if (!StringUtils.hasText(fileName)) {
                return Response.makeErrRsp("文件上传失败");
            }

            pageGiteeApiCaller.setOwner("GZC201314");
            pageGiteeApiCaller.setPath("BSM/doubanbook/" + fileName);
            pageGiteeApiCaller.setRepo("tuchuang");
            // 判断gitee 中是否已经存在了该驱动，如果存在了，则直接返回改驱动的url地址
            String fileUrl = giteeService.getFile(pageGiteeApiCaller);
            if (StringUtils.hasText(fileUrl)) {
                return Response.makeOKRsp("上传豆瓣图书封面成功").setData(fileUrl);
            }

            String fileBase64 = Base64.encode(file.getBytes());
            pageGiteeApiCaller.setContent(fileBase64);
            pageGiteeApiCaller.setMessage("上传豆瓣图书封面 " + LocalDateTime.now());

            String newFileUrl = giteeService.addFile(pageGiteeApiCaller);
            if (StringUtils.hasText(newFileUrl)) {
                return Response.makeOKRsp("上传豆瓣图书封面成功").setData(newFileUrl);
            } else {
                return Response.makeErrRsp("上传豆瓣图书封面失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("修改用户头像失败");
        }
    }

}

