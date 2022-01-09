package org.bsm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2022-01-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datasource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "datasourceid", type = IdType.ASSIGN_UUID)
    private String datasourceid;

    private String sourcename;

    private Integer sourcetype;

    private String username;

    private String password;

    private String driveurl;

    private String sourceurl;

    private String description;

    private Boolean pass;

    private String driveclass;
}
