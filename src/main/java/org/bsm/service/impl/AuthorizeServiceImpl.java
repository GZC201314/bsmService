package org.bsm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Authorize;
import org.bsm.mapper.AuthorizeMapper;
import org.bsm.service.IAuthorizeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("authorizeService")
public class AuthorizeServiceImpl extends ServiceImpl<AuthorizeMapper, Authorize> implements IAuthorizeService {
}
