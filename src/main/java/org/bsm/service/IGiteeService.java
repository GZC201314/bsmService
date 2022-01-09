package org.bsm.service;

import org.bsm.pagemodel.PageGiteeApiCaller;

/**
 * @author GZC
 * @create 2021-11-18 12:49
 * @desc gitee service
 */
public interface IGiteeService {
    /**
     * 向gitee 图床上传文件
     */
    public String addFile(PageGiteeApiCaller pageGiteeApiCaller);

    /**
     * 判断gitee 图床上是否有对应的文件，如果存在则返回对应文件的Url
     */
    public String getFile(PageGiteeApiCaller pageGiteeApiCaller);
}
