package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AipFaceResult {
    private Result result;


    private String error_msg;

    private int cached;

    private int error_code;

    private int timestamp;
}