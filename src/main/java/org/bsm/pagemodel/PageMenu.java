package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author GZC
 * @create 2021-11-15 22:30
 * @desc menu 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageMenu {
    private String id;
    private String path;
    private String name;
    private String icontype;
    private String icon;
    private Map<String, Object> query;
    private List<PageMenu> children;
}
