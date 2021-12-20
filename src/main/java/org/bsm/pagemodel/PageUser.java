package org.bsm.pagemodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author GZC
 * @create 2021-11-03 0:29
 * @desc 和前台页面相关的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userid;

    private String username;

    private String password;

    private String validCode;

    private String emailaddress;

    private String sessionId;

    @ApiModelProperty(value = "用户创建时间", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createtime;

    @ApiModelProperty(value = "最后修改用户时间", example = "2020-02-05 13:30:41")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastmodifytime;

    private Integer roleid;

    private String usericon;

    private Boolean isfacevalid;

    private String salt;

    private Boolean enabled;

    /**
     * 分页 参数
     */
    private Integer current;
    private Integer size;

    // 上传的头像图像
    private File uploadImg;
    private String uploadFileName;
    private String userlog;
    //人脸校验
    private Integer isFaceValid = 0;

    private String base;
}
