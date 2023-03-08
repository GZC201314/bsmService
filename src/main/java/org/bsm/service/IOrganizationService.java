package org.bsm.service;

import org.bsm.entity.CurUser;
import org.bsm.entity.Organization;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.pagemodel.PageOrganization;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author GZC
 * @since 2023-03-06
 */
public interface IOrganizationService extends IService<Organization> {

    boolean updateOrganization(PageOrganization pageOrganization);

    boolean addOrganization(PageOrganization pageOrganization, CurUser curUser);
}
