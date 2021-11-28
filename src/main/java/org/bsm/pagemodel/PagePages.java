package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author GZC
 * @create 2021-11-03 0:29
 * @desc 和前台页面相关的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagePages implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pageid;

    private String title;

    private String pagekey;

    private String parentkey;

    private String pagepath;

    private String icontype;

    private String icon;

    /**
     * 分页 参数
     */
    private Integer current;
    private Integer size;
}
