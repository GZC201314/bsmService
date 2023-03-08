package org.bsm.pagemodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageOrganization {

    private Integer id;
    private Integer parent;
    private String name;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime modifyDate;

    private String desc;

    /**
     * 分页 参数
     */
    private Page page;

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
    private MultipartFile icon;
    /**
     * 删除参数
     */
    private String delIds;

}
