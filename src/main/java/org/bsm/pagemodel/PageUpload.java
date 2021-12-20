package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
     * 用户的sessionId
     */
    private String sessionId;
    /**
     * 上传文件的mimeType类型
     */
    private String uploadContentType;
    /**
     * 上传文件的名称
     */
    private String uploadFileName;

    /**
     * 上传的文件
     */
    private MultipartFile file;
}
