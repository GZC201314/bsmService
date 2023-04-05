package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GZC
 * @create 2021-11-05 15:38
 * @desc 流程实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageFlow {


    private String xml;

    private String flowName;

    private String key;
    private String id;


    /**
     * 分页 参数
     */
    private Page page;


    private String delIds;
}
