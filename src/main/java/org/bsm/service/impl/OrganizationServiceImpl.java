package org.bsm.service.impl;

import org.bsm.entity.Organization;
import org.bsm.mapper.OrganizationMapper;
import org.bsm.service.IOrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
