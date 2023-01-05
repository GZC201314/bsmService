package org.bsm;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.ECIES;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUser;
import org.bsm.utils.RedisUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author GZC
 * @create 2021-11-03 16:10
 * @desc
 */
@Disabled
public class CopyValueTest1 {
    @Resource
    private RedisUtil redisUtil;

    @Test
    public void copyValue() {
        User user = new User();
        user.setUserid(UUID.randomUUID().toString());
        user.setUsername("GZC");
        user.setCreatetime(LocalDateTime.now());
        PageUser pageUser = new PageUser();
        pageUser.setUsername("admin");
//        BeanUtils.copyProperties(pageUser, user);
        BeanUtils.copyProperties(pageUser, user, "userid", "createtime");
        System.out.println("复制后的用户信息是 :" + user);
    }

    @Test
    public void encodeAnddecodeTest() throws JSONException {
        /*对称解密算法*/
//        AESTest();

//        DESedeTest();

//        DESWithSalt();

//        SM4Test();

        /*非对称加密算法*/

//        RSATest();

//        generateKeyPair();

//        RSAPrivateKeyDecode();

//        ECIESTest();

        // 随机生成需要加密的数据
        int size = 10000;
        List<User> userList = getUserList(size);

        long start = new Date().getTime();
        String content = JSON.toJSONString(userList);
        System.out.println("序列化 " + size + " 个 对象需要的时间是: " + (new Date().getTime() - start));
        //这边采用的序列化工具是JSON,如果后期考虑到性能问题可以采用gPRC
        JSONObject jsonObject = new JSONObject();

        /*生成AES密钥*/
        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);


        // 对数据进行加密
//        SymmetricCrypto aes1 = new SymmetricCrypto(SymmetricAlgorithm.AES, secretKey.getEncoded());
        byte[] encoded = secretKey.getEncoded();
        AES aes1 = SecureUtil.aes(encoded);
        String encodeContent = aes1.encryptHex(content);

        // 使用私钥对AES密钥进行加密
//        String rsaPrivateKey = (String) redisUtil.get("rsaPrivateKey");
        String rsaPrivateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJxRfUGKfd0O3yixEUQqKr7UPh/itypkzk5HMqgaDIDLwArLpboaGnYu2cquj55lQw7vnsIp+/QwquCXV+lkWQBS9V2tEnGOSQ8RG5Vghib1Za3U4Ccq0vhupYdQSlU28XQAjOhZSXLI4yOewFhvsPZTUKDjShk1X7IFlRKxWqE3AgMBAAECgYA631v/07N8jo3QiwOYOxNXRKNTKfVXI5kplRoIgqsD1Pjyd+lmUk92BEYlViIkAzpCxU9zDBHm0W+B364N6iu0Y033auSAWmARS86NS7helabD0/CvfA+PZYiYt+kwdSCV2U6gTAUXQWlN8gDLlJ7usiLsfzY4LppQb1/SjGB6rQJBAM1feo62S04iBbZCHNmCGr+THHNg7mFKa8B/Ngr+rzshIlfK5BuogrBy7eqwGHjpDkOtpz0mN2EpbUw2dFoZH8MCQQDC2k6feSFx0CfylDe/fPVDOW9RZ7RpEMhTuqVhTNOxl+BWwmI4+mVzkvI3cdt1K6iN5+jRyDkhK5HTVSETBnV9AkEAsNs5Gm7HmMhZrONwHqsYx8My6/UcM3I4KnQiIQPD+SKGhZ32JA9QRA0k70aoPG9OVl/TtigT5rsbIVd/iRs7qwJBAIzZGw4hlDBeBBJQW3/ahruCL9pLOVjdHcGQYG1WCIwOOcbdGf0P2vfRF69GaRloZp21LlE+BzXX9cAqgA7tk00CQQCrWFrJKizFIXBljcXhCTz4//rIZgnKCc99Eq7eK5pT4HbM6j9OJLSsl8SHv8oipa/M5TUbFOSXmNYukgSkQkG1";
        RSA rsa = new RSA(rsaPrivateKey, null);
        String encodedHex = HexUtil.encodeHexStr(encoded);
        String encodeAESKey = rsa.encryptHex(encodedHex, KeyType.PrivateKey);

        jsonObject.put("data", encodeContent);
        jsonObject.put("encodeAESKey", encodeAESKey);

        // 模拟发送数据给客户端
        //获取加密后的AESKey
        String encodeAESKey1 = (String) jsonObject.get("encodeAESKey");
        // 获取加密后的数据
        String data = (String) jsonObject.get("data");

        // 使用公钥解密
//        String rsaPublicKey = (String) redisUtil.get("rsaPublicKey");
        String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcUX1Bin3dDt8osRFEKiq+1D4f4rcqZM5ORzKoGgyAy8AKy6W6Ghp2LtnKro+eZUMO757CKfv0MKrgl1fpZFkAUvVdrRJxjkkPERuVYIYm9WWt1OAnKtL4bqWHUEpVNvF0AIzoWUlyyOMjnsBYb7D2U1Cg40oZNV+yBZUSsVqhNwIDAQAB";
        RSA rsaPublic = new RSA(null, rsaPublicKey);
        String aesKey = rsaPublic.decryptStr(encodeAESKey1, KeyType.PublicKey);


        //构建
        AES aes = SecureUtil.aes(HexUtil.decodeHex(aesKey));
//        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, decrypt);


        //解密为字符串
        String decryptStr = aes.decryptStr(data, CharsetUtil.CHARSET_UTF_8);

        //反序列化
        List<User> userList1 = JSON.parseArray(decryptStr, User.class);


    }


    private void ECIESTest() {
        final ECIES ecies = new ECIES();
        String textBase = "我是一段特别长的测试";
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            text.append(textBase);
        }
        // 公钥加密，私钥解密
        String encryptStr = ecies.encryptBase64(text.toString(), KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(ecies.decrypt(encryptStr, KeyType.PrivateKey));
        System.out.println(decryptStr);
    }

    // 生成需要加密的数据
    private List<User> getUserList(long size) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            User user = new User();
            user.setUsericon(RandomUtil.randomString(16));
            user.setCreatetime(LocalDateTime.now());
            user.setPassword(RandomUtil.randomString(16));
            user.setSalt(RandomUtil.randomString(16));
            user.setLastmodifytime(LocalDateTime.now());
            user.setIsfacevalid(RandomUtil.randomBoolean());
            user.setEnabled(RandomUtil.randomBoolean());
            user.setUsername(RandomUtil.randomString(16));
            user.setUserid(RandomUtil.randomString(16));
            userList.add(user);
        }
        return userList;
    }

    private void RSAPrivateKeyDecode() {
        String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA"
                + "f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ"
                + "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta"
                + "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz"
                + "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP"
                + "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW"
                + "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK"
                + "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI"
                + "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg"
                + "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ";

        RSA rsa = new RSA(PRIVATE_KEY, null);

        String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F"
                + "75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A"
                + "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8";


        byte[] aByte = HexUtil.decodeHex(a);
        byte[] decrypt = rsa.decrypt(aByte, KeyType.PrivateKey);
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
    }

    private void generateKeyPair() {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        pair.getPrivate();
        pair.getPublic();
    }

    private void RSATest() {

        RSA rsa = new RSA();

        //获得私钥
        rsa.getPrivateKey();
        rsa.getPrivateKeyBase64();
        //获得公钥
        rsa.getPublicKey();
        rsa.getPublicKeyBase64();

        //公钥加密，私钥解密
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("我是一段测试aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);

        //Junit单元测试
        Assert.isTrue("我是一段测试aaaa".equals(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8)), "解密失败");
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        //私钥加密，公钥解密
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("我是一段测试aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        Assert.isTrue("我是一段测试aaaa".equals(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8)), "解密失败");
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
    }

    private void SM4Test() {
        String content = "test中文";
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4");
        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);//test中文
        System.out.println(decryptStr);
    }

    private void DESWithSalt() {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
        DES des = new DES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "01020304".getBytes());
        String bsm_encode_string = des.encryptHex("BSM_encode_string");
        System.out.println(des.decryptStr(bsm_encode_string));
    }

    private void DESedeTest() {
        String content = "test中文";

        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DESede.getValue()).getEncoded();

        SymmetricCrypto des = new SymmetricCrypto(SymmetricAlgorithm.DESede, key);
        //加密
        byte[] encrypt = des.encrypt(content);
        //解密
        byte[] decrypt = des.decrypt(encrypt);

        //加密为16进制字符串（Hex表示）
        String encryptHex = des.encryptHex(content);
        //解密为字符串
        String decryptStr = des.decryptStr(encryptHex);
        System.out.println(decryptStr);
    }

    private void AESTest() {
        String content = "test中文";
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        //加密
        byte[] encrypt = aes.encrypt(content);
        //解密
        byte[] decrypt = aes.decrypt(encrypt);
        //加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        //解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println(decryptStr);
    }

}
