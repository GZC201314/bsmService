package org.bsm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2022-10-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Updateimginfo implements Serializable {

    private static final long serialVersionUID = 1L;

      private Integer id;

    private String imgid;

    private String relative_path;

    @TableId
    private String url;

    private String thumbnail_url;

    private Integer image_width;

    private Integer image_height;

    private String client_name;

    @TableField(value = "`delete`")
    private String delete;

}
