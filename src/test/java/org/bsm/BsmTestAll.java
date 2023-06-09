package org.bsm;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.bsm.entity.Organization;
import org.bsm.entity.Role;
import org.bsm.service.IOrganizationService;
import org.bsm.service.IRoleService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author GZC
 * @create 2022-05-30 16:50
 * @desc
 */
@DisplayName("BSM 所有的单元测试类")
@SpringBootTest(classes = {BsmServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, args = {"mpw.key=ef33af9dea1958f4"})
@AutoConfigureMockMvc
public class BsmTestAll extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    IRoleService roleService;

    @Resource
    IOrganizationService organizationService;

    @Resource
    DataSource dataSource;

    @Resource
    private Scheduler scheduler;

    @Autowired
    private ProcessEngine processEngine;

    @DisplayName("角色测试")
    @Test
    public void testRole() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("rolename", "Junit role");
        boolean remove = roleService.remove(queryWrapper);
        if (!remove) {
            Role role = new Role();
            role.setRolename("Junit role");
            role.setRolecname("Junit 角色");
            role.setDisabled(true);
            role.setIsdeleted(false);
            boolean save = roleService.save(role);
            Assertions.assertTrue(save);
        }
    }

    @DisplayName("组织测试")
    @Test
    public void testOrganization() {
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", "Junit Organization");
        boolean remove = organizationService.remove(queryWrapper);
        if (!remove) {
            Organization organization = new Organization();
            organization.setName("Junit Organization");
            organization.setCreateBy("Junit userId");
            organization.setCreateDate(LocalDateTime.now());
            organization.setModifyDate(LocalDateTime.now());
            organization.setIcon("http://n.sinaimg.cn/sinacn10119/100/w1600h900/20190325/e17c-hutwezf3366889.jpg");
            organization.setDesc("Junit 测试 Organization");
            organization.setParent(-1);
            boolean save = organizationService.save(organization);
            Assertions.assertTrue(save);
        }
    }

    @DisplayName("测试数据源")
    @Test
    public void testDataSource() throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select 1 from dual");
        boolean execute = preparedStatement.execute();
        Assertions.assertTrue(execute);
    }

    @DisplayName("测试定时任务")
    @Test
    public void testTask() throws SchedulerException {
        boolean shutdown = scheduler.isShutdown();
        Assertions.assertFalse(shutdown);
    }

    @DisplayName("测试Spring工具类")
    @Test
    public void testSpringUtil() {
        String property = SpringUtil.getProperty("server.port");
        System.out.println(SpringUtil.getActiveProfile());
        Assertions.assertEquals(property, "8888");
    }


    @DisplayName("测试Flowable流程查询")
    @Test
    public void testFlowableProcessQuery() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("1")
                .singleResult();
        Assertions.assertNull(processDefinition);
    }

//    @DisplayName("测试Flowable流程实例化")
//    @Test
//    public void testFlowableNewTask() {
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
//    }

    @DisplayName("测试Flowable流程删除")
    @Test
    public void testFlowableDelete() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }
}
