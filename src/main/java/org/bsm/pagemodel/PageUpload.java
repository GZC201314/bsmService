package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author GZC
 * @create 2021-11-05 15:38
 * @desc 文件上传实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUpload {
    /**
     * 定义一个File ,变量名要与jsp中的input标签的name一致
     */
    private File upload;
    /**
     * 上传文件的mimeType类型
     */
    private String uploadContentType;
    /**
     * 上传文件的名称
     */
    private String uploadFileName;
}
