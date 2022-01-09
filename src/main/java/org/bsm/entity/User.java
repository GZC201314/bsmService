package org.bsm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "userid", type = IdType.ASSIGN_UUID)
    private String userid;

    private String username;

    private String password;

    private String emailaddress;

    private LocalDateTime createtime;

    private LocalDateTime lastmodifytime;

    private Integer roleid;

    private String usericon;

    private Boolean isfacevalid;

    private String salt;

    private Boolean enabled;
}
