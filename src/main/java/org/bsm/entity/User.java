package org.bsm.entity;

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

      private String userid;

    private String username;

    private String password;

    private LocalDateTime createtime;

    private LocalDateTime lastmodifytime;

    private Integer roleid;

    private String usericon;

    private Boolean isfacevalid;

    private String salt;

    private Boolean enabled;
}
