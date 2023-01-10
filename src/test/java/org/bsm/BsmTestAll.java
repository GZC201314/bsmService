package org.bsm;

import org.bsm.entity.Role;
import org.bsm.service.IRoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author GZC
 * @create 2022-05-30 16:50
 * @desc
 */
@DisplayName("BSM 所有的单元测试类")
@SpringBootTest(classes = {BsmServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class BsmTestAll {

    @Autowired
    IRoleService roleService;

    @Autowired
    DataSource dataSource;

    @Autowired
    private Scheduler scheduler;

    @DisplayName("角色测试")
    @Test
    public void testRole() {
        Role role = new Role();
        role.setRolename("Junit role");
        role.setRolecname("Junit 角色");
        role.setDisabled(true);
        role.setIsdeleted(false);
        boolean save = roleService.save(role);
        Assertions.assertTrue(save);
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


}
