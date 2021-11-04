package org.bsm.service;

/**
 * @author GZC
 * @create 2021-11-02 23:17
 * @desc 发送邮件接口类
 */
public interface ISendEmailService {

    public boolean sendRegisterEmail(String emailaddress);
}
