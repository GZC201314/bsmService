package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.common.BsmException;
import org.bsm.entity.CurUser;
import org.bsm.entity.Organization;
import org.bsm.entity.Updateimginfo;
import org.bsm.entity.User;
import org.bsm.mapper.OrganizationMapper;
import org.bsm.mapper.UserMapper;
import org.bsm.pagemodel.PageOrganization;
import org.bsm.service.IOrganizationService;
import org.bsm.utils.ImgtuUtil;
import org.bsm.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author GZC
 * @since 2023-03-06
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements IOrganizationService {

    @Resource
    UserMapper userMapper;
    @Resource
    ImgtuUtil imgtuUtil;

    @Resource
    OrganizationMapper organizationMapper;

    @Resource
    private HttpSession session;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public <E extends IPage<Organization>> E page(E page, Wrapper<Organization> queryWrapper) {
        E organizationPage = super.page(page, queryWrapper);
        // 先获取所有的userid 然后再转换成name
        List<String> idList = new ArrayList<>();
        organizationPage.getRecords().forEach(item -> idList.add(item.getCreateBy()));
        if (idList.size() > 0){
            Map<String, String> id2NameMap = new HashMap<>();
            List<User> users = userMapper.selectBatchIds(idList);
            users.forEach(item -> id2NameMap.put(item.getUserid(), item.getUsername()));
            organizationPage.getRecords().forEach(item -> item.setCreateBy(id2NameMap.get(item.getCreateBy())));
        }
        return organizationPage;
    }

    @Override
    public boolean updateOrganization(PageOrganization pageOrganization) {

        // 获取组织标识文件，上传到图库
        MultipartFile icon = pageOrganization.getIcon();

        try {
            Organization org = organizationMapper.selectById(pageOrganization.getId());
            if (!Objects.isNull(icon)){
                Updateimginfo updateimginfo = imgtuUtil.uploadPicture(icon);
                String url = updateimginfo.getUrl();
                org.setIcon(url);
            }
            org.setDesc(pageOrganization.getDesc());
            org.setParent(pageOrganization.getParent());
            org.setModifyDate(LocalDateTime.now());
            organizationMapper.updateById(org);
            return true;
        } catch (IOException e) {
            throw new BsmException("文件上传失败！");
        }


    }

    @Override
    public boolean addOrganization(PageOrganization pageOrganization, CurUser curUser) {
        // 获取组织标识文件，上传到图库
        MultipartFile icon = pageOrganization.getIcon();

        try {
            Organization org = new Organization();
            if (!Objects.isNull(icon)){
                Updateimginfo updateimginfo = imgtuUtil.uploadPicture(icon);
                String url = updateimginfo.getUrl();
                org.setIcon(url);
            }
            org.setCreateDate(LocalDateTime.now());
            if (curUser != null){
                org.setCreateBy(curUser.getUserid());
            }
            org.setName(pageOrganization.getName());
            org.setDesc(pageOrganization.getDesc());
            org.setParent(pageOrganization.getParent());
            org.setModifyDate(LocalDateTime.now());
            organizationMapper.insert(org);
            return true;
        } catch (IOException e) {
            throw new BsmException("文件上传失败！");
        }
    }
}
