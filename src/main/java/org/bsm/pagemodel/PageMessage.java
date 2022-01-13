package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author GZC
 * @create 2021-11-03 0:29
 * @desc 和前台页面相关的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subject;


    private String content;

    private String sentDate;

    private Date starttime;
    private Date endtime;

    /**
     * 分页 参数
     */
    private Page page;

}
