package org.bsm.service.impl;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bsm.drive.DriverAdpter;
import org.bsm.drive.ExtDriveClassLoader;
import org.bsm.drive.MyDriverManager;
import org.bsm.entity.Datasource;
import org.bsm.mapper.DatasourceMapper;
import org.bsm.pagemodel.PageDataSource;
import org.bsm.pagemodel.PageGiteeApiCaller;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.IDatasourceService;
import org.bsm.service.IGiteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-01-06
 */
@Slf4j
@Service
public class DatasourceServiceImpl extends ServiceImpl<DatasourceMapper, Datasource> implements IDatasourceService {

    @Autowired
    IGiteeService giteeService;


    /**
     * 上传驱动
     *
     * @param pageUpload 上传的驱动文件
     * @return 上传后的驱动URL
     */
    @Override
    public String uploadDrive(PageUpload pageUpload) throws IOException {
        String driveUrl = "";

        // 向 Gitee 中提交头像
        PageGiteeApiCaller pageGiteeApiCaller = new PageGiteeApiCaller();
        MultipartFile avatar = pageUpload.getFile();
        /*生成文件地址*/
        String fileName = avatar.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            return "";
        }

        pageGiteeApiCaller.setOwner("GZC201314");
        pageGiteeApiCaller.setPath("BSM/drives/" + fileName);
        pageGiteeApiCaller.setRepo("tuchuang");
        // 判断gitee 中是否已经存在了该驱动，如果存在了，则直接返回改驱动的url地址
        String fileUrl = giteeService.getFile(pageGiteeApiCaller);
        if (StringUtils.hasText(fileUrl)) {
            return fileUrl;
        }

        String fileBase64 = Base64.encode(avatar.getBytes());
        pageGiteeApiCaller.setContent(fileBase64);
        pageGiteeApiCaller.setMessage("上传数据源驱动 " + LocalDateTime.now());

        driveUrl = giteeService.addFile(pageGiteeApiCaller);
        return driveUrl;
    }

    /**
     * 测试驱动
     *
     * @param pageDataSource
     */
    @Override
    public boolean testDrive(PageDataSource pageDataSource) {
        log.info("类加载器======" + getClass().getClassLoader());
        boolean result = false;
        Connection connection = null;
        try {
            URL url = new URL("jar:" + pageDataSource.getDriveurl() + "!/");
            ExtDriveClassLoader extDriveClassLoader = new ExtDriveClassLoader(new URL[]{url}, getClass().getClassLoader());

            Class<?> aClass = extDriveClassLoader.loadClass(pageDataSource.getDriveclass());
            Driver driver = (Driver) aClass.newInstance();
            int majorVersion = driver.getMajorVersion();
            int minorVersion = driver.getMinorVersion();

            String driveVersion = majorVersion + "." + minorVersion;
            String driveType = driver.getClass().getName();

            DriverAdpter mysql5DriverAdpter = new DriverAdpter(driver, driveVersion, driveType);
            MyDriverManager.registerDriver(mysql5DriverAdpter);
            connection = MyDriverManager.getConnection(pageDataSource.getSourceurl(), pageDataSource.getUsername(),
                    pageDataSource.getPassword(), driveVersion, driveType);

            result = true;

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    log.error(throwables.getMessage());
                    throwables.printStackTrace();
                }
            }
        }
        return result;
    }
}
