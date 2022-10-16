package org.bsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Authorize;
import org.bsm.entity.Pages;
import org.bsm.mapper.AuthorizeMapper;
import org.bsm.mapper.PagesMapper;
import org.bsm.pagemodel.PageMenu;
import org.bsm.pagemodel.PagePages;
import org.bsm.pagemodel.PageRole;
import org.bsm.service.IAuthorizeService;
import org.bsm.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("authorizeService")
public class AuthorizeServiceImpl extends ServiceImpl<AuthorizeMapper, Authorize> implements IAuthorizeService {


    @Resource
    PagesMapper pagesMapper;

    @Resource
    AuthorizeMapper authorizeMapper;

    @Resource
    RedisUtil redisUtil;


    /**
     * 根据rolename来获取当前角色所有的授权页面
     *
     * @param pageRole 前台传参
     * @return JSONObject
     */
    @Override
    public JSONObject getAllAuthorizePagesByRoleName(PageRole pageRole) {
        String rolename = pageRole.getRolename();
        // 1.获取全部的菜单信息
        List<Pages> pages = pagesMapper.selectList(null);

        // 2.获取所有的授权的页面
        QueryWrapper<Authorize> authorizeQueryWrapper = new QueryWrapper<>();
        authorizeQueryWrapper.eq("rolename", rolename);
        /*这边可以直接从redis中取如果redis中不存在，才去数据库中去取*/
        Set<Object> authorizeds = redisUtil.sGet(rolename);
        List<Authorize> authorizes = new ArrayList<>();
        if (CollectionUtils.isEmpty(authorizeds)) {
            authorizes = authorizeMapper.selectList(authorizeQueryWrapper);
        } else {
            for (Object authorized :
                    authorizeds) {
                Authorize authorize = (Authorize) authorized;
                authorizes.add(authorize);
            }
        }

        Set<String> authSet = new HashSet<>();
        Set<String> parentKeySet = new HashSet<>();
        for (Authorize authorize : authorizes) {
            /* 这边通过pagePath 是否为空；来判断页面是否是一级页面，如果是一级页面则不需要把这个页面设置为选中*/
            if (StringUtils.hasText(authorize.getPagepath())) {
                authSet.add(authorize.getPageid());
            }
        }


        Map<String, List<PageMenu>> pagesList = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(pages)) {
            for (Pages page : pages) {
                PageMenu pageMenu = new PageMenu();
                pageMenu.setId(page.getPagekey());
                pageMenu.setTitle(page.getTitle());
                pageMenu.setKey(page.getPageid());

                if (!pagesList.containsKey(page.getParentkey())) {
                    pagesList.put(page.getParentkey(), new ArrayList<>());
                }
                pagesList.get(page.getParentkey()).add(pageMenu);
            }
        }
        List<PageMenu> parentPageMenus = pagesList.get("0");
        if (!CollectionUtils.isEmpty(parentPageMenus)) {
            for (PageMenu pageMenu :
                    parentPageMenus) {
                parentKeySet.add(pageMenu.getKey());
                pageMenu.setChildren(pagesList.get(pageMenu.getId()));
            }
        }
        JSONObject result = new JSONObject();
        result.put("menuList", parentPageMenus);
        result.put("checkKeys", authSet);
        result.put("expandedKeys", parentKeySet);

        return result;
    }

    /**
     * 根据rolename来修改角色的授权页面
     *
     * @param pagePages 前台传参
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAuthorizePagesByRoleName(PagePages pagePages) {
        String[] pagesIds = pagePages.getPagesIds().split(",");

        QueryWrapper<Authorize> authorizeQueryWrapper = new QueryWrapper<>();
        authorizeQueryWrapper.eq("rolename", pagePages.getRolename());
        authorizeMapper.delete(authorizeQueryWrapper);
        redisUtil.del(pagePages.getRolename());
        for (String pageId : pagesIds) {
            Authorize authorize = new Authorize();
            authorize.setAuthorizeid(UUID.randomUUID().toString());
            authorize.setPageid(pageId);
            authorize.setRolename(pagePages.getRolename());
            QueryWrapper<Pages> pagesQueryWrapper = new QueryWrapper<>();
            pagesQueryWrapper.eq("pageid", pageId);
            Pages pages = pagesMapper.selectOne(pagesQueryWrapper);
            authorize.setPagepath(pages.getPagepath());
            authorizeMapper.insert(authorize);
            redisUtil.sSet(pagePages.getRolename(), authorize);
        }
        return true;
    }
}
