package org.bsm.pagemodel;

import lombok.*;

/**
 * @author GZC
 * @create 2021-11-18 13:10
 * @desc gitee api 文件信息类
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageGiteeFile {

    /**
     * String | 文件的路径
     */
    private String path;

    private String mode;

    private String type;

    private String sha;

    private String size;

    private String url;


}
