package org.bsm.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.bsm.drive.DriverAdpter;
import org.bsm.drive.ExtDriveClassLoader;
import org.bsm.drive.MyDriverManager;
import org.bsm.entity.Datasource;
import org.bsm.mapper.DatasourceMapper;
import org.bsm.pagemodel.PageDataSource;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.IDatasourceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

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


    /**
     * 上传驱动
     *
     * @param pageUpload 上传的驱动文件
     * @return 上传后的驱动URL
     */
    @Override
    public String uploadDrive(PageUpload pageUpload) throws IOException {
        String driveUrl;

        MultipartFile avatar = pageUpload.getFile();

        /*生成本地文件地址*/
        String fileName = avatar.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        File file = new File("BSM/Drives/" + fileName);

        FileUtil.writeBytes(avatar.getBytes(), file);
        driveUrl = file.getCanonicalPath();

        return driveUrl;
    }

    /**
     * 测试驱动
     */
    @Override
    public boolean testDrive(PageDataSource pageDataSource) {
        log.info("类加载器======" + getClass().getClassLoader());
        boolean result = false;
        Connection connection = null;
        try {
            File file = new File(pageDataSource.getDriveurl());
            URL url = file.toURI().toURL();
            log.warn("url===={}", url.getPath());
            ExtDriveClassLoader extDriveClassLoader = new ExtDriveClassLoader(new URL[]{url}, getClass().getClassLoader());

            Class<?> aClass = extDriveClassLoader.loadClass(pageDataSource.getDriveclass());
            Driver driver = (Driver) aClass.newInstance();
            int majorVersion = driver.getMajorVersion();
            int minorVersion = driver.getMinorVersion();

            String driveVersion = majorVersion + "." + minorVersion;
            String driveType = driver.getClass().getName();

            DriverAdpter driverAdpter = new DriverAdpter(driver, driveVersion, driveType);
            MyDriverManager.registerDriver(driverAdpter);
            connection = MyDriverManager.getConnection(pageDataSource.getSourceurl(), pageDataSource.getUsername(),
                    pageDataSource.getPassword(), driveVersion, driveType);
            result = true;
        } catch (Exception e) {
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
