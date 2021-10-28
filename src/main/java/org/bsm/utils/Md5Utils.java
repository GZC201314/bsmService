package org.bsm.utils;

import java.security.MessageDigest;
import java.util.Random;

import sun.misc.BASE64Encoder;

/**
 * 对帐号口令加密
 * @author shanl
 *
 */
public class Md5Utils {
    private static final MessageDigest md;
    private static final BASE64Encoder b64Encoder;

    static{
        try {
            md = MessageDigest.getInstance("MD5", "SUN");
            b64Encoder = new BASE64Encoder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查密码
     * @param inputPasswd 用户输入的密码
     * @param storePasswd 已存储的密码
     * @return true:通过检查,false:未通过
     */
    synchronized
    public static boolean checkPasswd(String inputPasswd, String storePasswd){
        boolean ok = false;

        try{
            byte[] saltBys = storePasswd.substring(0,2).getBytes("UTF-8");
            String inPwd = toPasswd(inputPasswd, saltBys);
            ok = inPwd.equals(storePasswd);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return ok;
    }

    /**
     * 将客户输入的密码加密
     * @param inputPasswd 客户输入的密码
     */
    synchronized
    public static String toPasswd(String inputPasswd){
        byte[] salt = getSalt(2);
        return toPasswd(inputPasswd, salt);
    }

    /**
     * 将客户输入的密码加密
     * @param inputPasswd 客户输入的密码
     * @param salt 盐
     * @return 加密后的字符串
     */
    synchronized
    public static  String toPasswd(String inputPasswd, byte[] salt){
        String pwd = "";

        try{
            md.reset();
            md.update(salt);
            md.update(inputPasswd.getBytes("UTF-8"));
            byte[] bys = md.digest();;
            //1:AASLexNtFtI7e1IuQIg88ZNA==
            //879:AA/lCM5NEwVQJ25YYomE1ldQ==
            //1000:AARXKQat7z+/iu2w6KpKgLQA==
//			for(int i=0; i<1000; i++){
//				md.reset();
//				bys = md.digest(bys);
//			}

            pwd += (char)salt[0];
            pwd += (char)salt[1];
            pwd += b64Encoder.encode(bys);
        }catch(Exception ex){
            ex.printStackTrace();
            pwd = "";
        }

        return pwd;
    }

    /**
     * 返回指定长度的盐(ASCII码)
     * @param len 长度
     * @return
     */
    public static byte[] getSalt(int len){
        byte[] salt = new byte[len];
        Random rand = new Random();

        for(int i=0; i<len; i++){
//			salt[i] = 'A';
            salt[i] = (byte) ((rand.nextInt('~'-'!')+'!') & 0x007f);
        }

        return salt;
    }
}