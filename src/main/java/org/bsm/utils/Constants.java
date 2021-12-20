package org.bsm.utils;

/**
 * @author GZC
 * @create 2021-10-28 19:31
 * @desc
 */
public class Constants {

    public static Integer EXPIRATION = 1000 * 3600;
    public static String SECRET = "thisisasecretstringforbsm";

    public static String CREATE_REPOS_URL = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";


    /**
     * 性别
     */
    public static enum sex {
        /*男*/
        MAN("1", "男"),
        /*女*/
        FEMAN("2", "女");

        private final String value;
        private final String name;

        private sex(String value, String name) {
            this.value = value;
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public static enum ResultCode {

        // 成功
        SUCCESS(200),

        // 失败
        FAIL(400),

        // 未认证（签名错误）
        UNAUTHORIZED(401),

        // 接口不存在
        NOT_FOUND(404),

        // 服务器内部错误
        INTERNAL_SERVER_ERROR(500);

        public int code;

        ResultCode(int code) {
            this.code = code;
        }
    }
}
