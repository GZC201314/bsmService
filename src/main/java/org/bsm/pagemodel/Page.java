package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GZC
 * @create 2021-12-22 19:26
 * @desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    private Integer total = 0;
    private Integer page = 1;
    private Integer pageSize = 10;
}
