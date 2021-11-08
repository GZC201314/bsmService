package org.bsm.pagemodel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author GZC
 */
@Data
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private String face_token;

    private List<User_list> user_list;

}