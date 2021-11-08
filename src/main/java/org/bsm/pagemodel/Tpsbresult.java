package org.bsm.pagemodel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author GZC
 */
@Data
public class Tpsbresult implements Serializable {
    private String log_id;

    private int words_result_num;

    private List<Words_result> words_result;
}