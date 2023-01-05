package org.bsm;

import org.bsm.entity.Role;
import org.bsm.service.IRoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

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

    //    @Transactional
    @Test
    public void testCAS() throws InterruptedException {
        Role role = new Role();
        role.setRolename("Junit role");
        role.setRolecname("Junit 角色");
        role.setDisabled(true);
        role.setIsdeleted(false);
        boolean save = roleService.save(role);
        Assertions.assertTrue(save);
    }

}
