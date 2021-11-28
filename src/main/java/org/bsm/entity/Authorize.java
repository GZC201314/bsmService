package org.bsm.entity;

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

    private String authorizeid;

    private String pageid;

    private String pagepath;

    private String rolename;


}
