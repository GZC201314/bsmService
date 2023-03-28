package org.bsm;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.bsm.entity.Organization;
import org.bsm.entity.Role;
import org.bsm.service.IOrganizationService;
import org.bsm.service.IRoleService;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author GZC
 * @create 2022-05-30 16:50
 * @desc
 */
@DisplayName("BSM 所有的单元测试类")
@SpringBootTest(classes = {BsmServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,args = {"mpw.key=ef33af9dea1958f4"})
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
        queryWrapper.eq("rolename","Junit role");
        boolean remove = roleService.remove(queryWrapper);
        if (!remove){
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
        queryWrapper.eq("name","Junit Organization");
        boolean remove = organizationService.remove(queryWrapper);
        if (!remove){
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
    public void testSpringUtil()  {
        String property = SpringUtil.getProperty("server.port");
        System.out.println(SpringUtil.getActiveProfile());
        Assertions.assertEquals(property, "8888");
    }




    /*
     *
     *
     *
     * */
    @DisplayName("测试Flowable")
    @Test
    public void testFlowableMenthod() {
//        RepositoryService repositoryService = processEngine.getRepositoryService();
//        Deployment deploy = repositoryService.createDeployment().addClasspathResource("one-task-process.bpmn20.xml").key("holidayRequest").name("holidayRequest").deploy();
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
//                .deploymentId(deploy.getId())
//                .singleResult();
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//
//        /*开启流程实例*/
//        Map<String, Object> variables = new HashMap<String, Object>();
//        variables.put("employee", "张三");
//        variables.put("nrOfHolidays", 3);
//        variables.put("description", "生病了");
//        ProcessInstance processInstance =
//                runtimeService.startProcessInstanceByKey("holidayRequest", variables);
//
//        TaskService taskService = processEngine.getTaskService();
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
//        System.out.println("You have " + tasks.size() + " tasks:");
//        for (int i=0; i<tasks.size(); i++) {
//            System.out.println((i+1) + ") " + tasks.get(i).getName());
//        }
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Which task would you like to complete?");
//        int taskIndex = Integer.valueOf(scanner.nextLine());
//        Task task = tasks.get(taskIndex - 1);
//        Map<String, Object> processVariables = taskService.getVariables(task.getId());
//        System.out.println(processVariables.get("employee") + " wants " +
//                processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");
//
//        boolean approved = scanner.nextLine().toLowerCase().equals("y");
//        variables = new HashMap<String, Object>();
//        variables.put("approved", approved);
//        taskService.complete(task.getId(), variables);

//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration().setJdbcUrl("jdbc:mysql://localhost:3306/flowable?useUnicode=true&zeroDateTimeBehavior=convertToNull").setJdbcUsername("root").setJdbcPassword("GZCabc123").setJdbcDriver("com.mysql.cj.jdbc.Driver").setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//        // 准备引擎对象
//        ProcessEngine processEngine = cfg.buildProcessEngine();
//        // 加载流程文件 , 部署写好的BPMN2.0文件
//        RepositoryService repositoryService = processEngine.getRepositoryService();
////        Deployment deployment = repositoryService.createDeployment().addClasspathResource("one-task-process.bpmn20.xml").deploy();
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionName("holidayRequest").singleResult();
//        System.out.println("Found process definition : " + processDefinition.getName());
//        // 初始化入参
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Who are you?");
//        String employee = scanner.nextLine();
//        System.out.println("How many holidays do you want to request?");
//        Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());
//        System.out.println("Why do you need them?");
//        String description = scanner.nextLine();
//        // 启动一个流程节点
//        RuntimeService runtimeService = processEngine.getRuntimeService();
//        Map<String, Object> variables = new HashMap();
//        variables.put("employee", employee);
//        variables.put("nrOfHolidays", nrOfHolidays);
//        variables.put("description", description);
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
//        // 配置这个查询只返回’managers’组的任务
//        TaskService taskService = processEngine.getTaskService();
//        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
//        System.out.println("You have " + tasks.size() + " tasks:");
//        for (int i = 0; i < tasks.size(); i++) {
//            System.out.println((i + 1) + ") " + tasks.get(i).getName());
//        }
//        // 使用任务Id获取特定流程实例的变量，并在屏幕上显示实际的申请
//        System.out.println("Which task would you like to complete?");
//        int taskIndex = Integer.valueOf(scanner.nextLine());
//        Task task = tasks.get(taskIndex - 1);
//        Map<String, Object> processVariables = taskService.getVariables(task.getId());
//        System.out.println(processVariables.get("employee") + " wants " + processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");

    }

}
