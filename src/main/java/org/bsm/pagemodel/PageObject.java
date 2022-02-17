package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author GZC
 * @create 2021-12-22 19:26
 * @desc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageObject<T> {
    private Integer total;
    private Integer size;
    private Integer pages;
    private Integer current;
    private List<T> records;
}
