package org.bsm.service;

import org.bsm.pagemodel.PageGiteeApiCaller;

/**
 * @author GZC
 * @create 2021-11-18 12:49
 * @desc gitee service
 */
public interface IGiteeService {
    /**
     * 向gitee 图床上传图片
     */
    public boolean addFile(PageGiteeApiCaller pageGiteeApiCaller);
}
