package org.bsm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 作者
 * @since 2021-10-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private String remark;

    private Boolean disabled;

    @TableId(value = "roleid", type = IdType.AUTO)
    private Integer roleid;

    private String rolename;

    private String rolecname;

    private Boolean isdeleted;
}
