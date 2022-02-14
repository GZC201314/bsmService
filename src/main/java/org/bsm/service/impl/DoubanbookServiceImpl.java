package org.bsm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Doubanbook;
import org.bsm.mapper.DoubanbookMapper;
import org.bsm.service.IDoubanbookService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书本表 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Service
@Slf4j
public class DoubanbookServiceImpl extends ServiceImpl<DoubanbookMapper, Doubanbook> implements IDoubanbookService {

}
