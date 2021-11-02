package org.bsm.entity;

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
 * @since 2021-10-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pages implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pageid;

    private String title;

      private String pagekey;

    private String parentkey;

    private String pagepath;
}
