package org.bsm.service;

import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.pagemodel.PageGiteeFile;

import java.util.List;

/**
 * @author GZC
 * @create 2021-11-18 12:49
 * @desc gitee service
 */
public interface IGiteeService {
    /**
     * 向gitee 图床上传文件
     */
    String addFile(PageGiteeApiCaller pageGiteeApiCaller);

    /**
     * 判断gitee 图床上是否有对应的文件，如果存在则返回对应文件的Url
     */
    String getFile(PageGiteeApiCaller pageGiteeApiCaller);

    /**
     * 获取文件夹下面的所有文件
     */
    List<PageGiteeFile> getFilesByDir(PageGiteeApiCaller pageGiteeApiCaller);

    /**
     * 删除指定的文件
     */
    boolean deleteFile(PageGiteeApiCaller pageGiteeApiCaller);


    String oAuthLogin(String code);
}
