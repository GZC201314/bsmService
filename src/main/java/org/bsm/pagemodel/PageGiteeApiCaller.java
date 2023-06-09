package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author GZC
 * @create 2021-11-18 13:10
 * @desc gitee api 调用参数实体类
 */
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageGiteeApiCaller {
    /**
     * String | 仓库所属空间地址(企业、组织或个人的地址path)
     */
    private String owner;
    /**
     * String | 仓库路径(path)
     */
    private String repo;
    /**
     * String | 文件的路径
     */
    private String path;

    /**
     * 可以是分支名(如master)、Commit或者目录Tree的SHA值
     */
    private String sha;

    /**
     * String | 文件内容, 要用 base64 编码
     */
    private String content;
    /**
     * String | 提交信息
     */
    private String message;
    /**
     * String | 用户授权码
     */
    private String accessToken;
    /**
     * String | 分支名称。默认为仓库对默认分支
     */
    private String branch;
    /**
     * String | Committer的名字，默认为当前用户的名字
     */
    private String committerName;
    /**
     * String | Committer的邮箱，默认为当前用户的邮箱
     */
    private String committerEmail;
    /**
     * String | Author的名字，默认为当前用户的名字
     */
    private String authorName;
    /**
     * String | Author的邮箱，默认为当前用户的邮箱
     */
    private String authorEmail;
    /**
     * recursive | 赋值为1递归获取目录
     */
    private String  recursive;
}
