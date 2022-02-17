package org.bsm.utils;

import cn.hutool.core.util.PageUtil;
import org.bsm.pagemodel.PageObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 *
 * @author GZC
 */
public class MyPageUtil {

    /**
     * 封装分页对象
     */
    public static <T> PageObject<T> getPageObject(List<T> list, int size, int current) {
        PageUtil.setFirstPageNo(1);
        PageObject<T> pageObject = new PageObject<>();
        int total = list.size();
        List<T> records = new ArrayList<>();

        int[] startAndEnd = PageUtil.transToStartEnd(current, size);
        int totalPage = PageUtil.totalPage(total, size);
        for (int i = startAndEnd[0]; i < startAndEnd[1]; i++) {
            if (i >= total) {
                break;
            }
            T t = list.get(i);
            records.add(t);
        }
        pageObject.setPages(totalPage);
        pageObject.setCurrent(current);
        pageObject.setRecords(records);
        pageObject.setSize(size);
        pageObject.setTotal(total);
        return pageObject;
    }
}