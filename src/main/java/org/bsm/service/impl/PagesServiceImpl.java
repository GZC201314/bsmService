package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Pages;
import org.bsm.mapper.PagesMapper;
import org.bsm.pagemodel.PagePages;
import org.bsm.service.IPagesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 页面实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service
public class PagesServiceImpl extends ServiceImpl<PagesMapper, Pages> implements IPagesService {

    @Autowired
    PagesMapper pagesMapper;

    /**
     * 新增页面
     *
     * @param pagePages 前台传参
     * @return 成功状态
     */
    @Override
    public boolean addPages(PagePages pagePages) {
        //如果是一级菜单，需要给菜单设置orderId
        if ("0".equals(pagePages.getParentkey())) {
            pagePages.setIcontype("fa");
            QueryWrapper<Pages> pagesQueryWrapper = new QueryWrapper<>();
            pagesQueryWrapper.eq("parentkey", "0");
            pagesQueryWrapper.orderByDesc("orderid");
            Pages pages = pagesMapper.selectList(pagesQueryWrapper).stream().findFirst().get();
            if (pages != null) {
                pagePages.setOrderid(pages.getOrderid() + 1);
            } else {
                pagePages.setOrderid(1);
            }
        } else {
            pagePages.setIcontype("antd");
        }
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagePages, pages);
        int insert = pagesMapper.insert(pages);
        return insert == 1;
    }
}
