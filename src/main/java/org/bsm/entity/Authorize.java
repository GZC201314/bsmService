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
public class Authorize implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "authorizeid", type = IdType.ASSIGN_UUID)
    private String authorizeid;

    private String pageid;

    private String pagepath;

    private String rolename;


}
