package org.bsm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 书本表
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doubanbook implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "isbn", type = IdType.ASSIGN_ID)
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

}
