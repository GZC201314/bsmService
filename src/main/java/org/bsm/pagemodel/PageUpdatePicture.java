package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUpdatePicture {

    /**
     * 用户的sessionId
     */
    private String sessionId;

    private String uid;
    private String token;
    /**
     * 上传的文件
     */
    private MultipartFile file;
}
