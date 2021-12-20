package org.bsm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUser;
import org.bsm.service.IUserService;
import org.bsm.utils.RedisUtil;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.bsm.utils.smscode.SmsCode;
import org.bsm.utils.validateCode.ImageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * @author GZC
 */
@Api(tags = "校验接口类")
@Slf4j
@RestController
public class ValidateController {

    public final static String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";

    public final static String SESSION_KEY_SMS_CODE = "SESSION_KEY_SMS_CODE";

    private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Autowired
    IUserService userService;

    @Autowired
    RedisUtil redisUtil;


    @ApiOperation("验证码生成接口 ")
    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = createImageCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_IMAGE_CODE, imageCode);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    @ApiOperation("短信验证码生成接口")
    @GetMapping("/code/sms")
    public void createSmsCode(HttpServletRequest request, String mobile) {
        SmsCode smsCode = createSMSCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_SMS_CODE + mobile, smsCode);
        // 输出验证码到控制台代替短信发送服务
        System.out.println("您的登录验证码为：" + smsCode.getCode() + "，有效时间为60秒");
    }

    @ApiOperation("校验用户信息接口")
    @GetMapping("/valid/userinfo")
    public ResponseResult<Object> validEmailAddress(PageUser pageUser, HttpServletRequest request) throws IOException {
        if (StringUtils.hasText(pageUser.getEmailaddress())) {
            log.info("校验用户邮箱接口,传入的邮箱地址是：" + pageUser.getEmailaddress());
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("emailaddress", pageUser.getEmailaddress());
            int userCount = userService.count(queryWrapper);
            if (userCount > 0) {
                return Response.makeOKRsp("邮箱已存在").setData(false);
            } else {
                return Response.makeOKRsp("校验成功").setData(true);
            }
        }
        if (StringUtils.hasText(pageUser.getUsername())) {
            log.info("校验用户名称接口,传入的用户名称是：" + pageUser.getUsername());
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", pageUser.getUsername());
            int userCount = userService.count(queryWrapper);
            if (userCount > 0) {
                return Response.makeOKRsp("用户名已存在").setData(false);
            } else {
                return Response.makeOKRsp("校验成功").setData(true);
            }
        }

        if (StringUtils.hasText(pageUser.getPassword())) {
            log.info("校验用户名称接口,传入的用户密码是：" + pageUser.getPassword());

            String sessionId = request.getSession().getId();
            if (!StringUtils.hasText(sessionId)) {
                return Response.makeErrRsp("校验失败,未登录。").setData(false);
            }
            Map<Object, Object> userInfo = redisUtil.hmget(sessionId);
            String userName = (String) userInfo.get(sessionId);
            pageUser.setUsername(userName);
            userService.editUserPassword(pageUser);


        }

        return Response.makeErrRsp("校验失败").setData(false);
    }


    private SmsCode createSMSCode() {
        String code = RandomStringUtils.randomNumeric(6);
        return new SmsCode(code, 60);
    }

    private ImageCode createImageCode() {
        // 验证码图片宽度
        int width = 100;
        // 验证码图片长度
        int height = 36;
        // 验证码位数
        int length = 4;
        // 验证码有效时间 60s
        int expireIn = 60;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }

        g.dispose();

        return new ImageCode(image, sRand.toString(), expireIn);
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }

        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
