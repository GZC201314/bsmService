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
public class PageDouBanBook implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;

    private String name;

    private String englishname;

    private String title;

    private String seriesname;

    private String author;

    private String introduction;

    private String publisher;

    private String publishingtime;

    private String edition;

    private String score;

    private String translate;

    private String editor;

    private String folio;

    private String size;

    private String weight;

    private String price;

    private String image;

    /**
     * 分页 参数
     */
    private Page page;

    private Integer current;

    private String delIds;
}
