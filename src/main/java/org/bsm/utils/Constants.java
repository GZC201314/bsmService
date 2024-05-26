package org.bsm.utils;

/**
 * @author GZC
 * @create 2021-10-28 19:31
 * @desc
 */
public class Constants {

    public static Integer EXPIRATION = 1000 * 3600;
    public static String SECRET = "thisisasecretstringforbsm";

    public static String DEFAULT_PASSWORD = "123456";

    public static String CREATE_REPOS_URL = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";

    public static String GET_REPOSFILE_URL = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";
    public static String GET_USERINFO_URL = "https://gitee.com/api/v5/user?access_token=%s";

    public static String GET_FILESBYDIRSHA_URL = "https://gitee.com/api/v5/repos/%s/%s/git/trees/%s";

    public static String DELETE_REPOSFILE_URL = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";
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

    // 周期任务
    public static int REPEAT_TASK = 0;
    //非周期任务
    public static int NO_REPEAT_TASK = 1;
    /*redis config key*/
    public static String BSM_CONFIG = "bsm_config";
    /*config中对应的name*/
    public static String OCR_APP_ID = "OCR_APP_ID";
    public static String OCR_API_KEY = "OCR_API_KEY";
    public static String OCR_SECRET_KEY = "OCR_SECRET_KEY";
    public static String FACE_APP_ID = "FACE_APP_ID";
    public static String FACE_API_KEY = "FACE_API_KEY";
    public static String FACE_SECRET_KEY = "FACE_SECRET_KEY";

    public static String GITEE_ACCESS_TOKEN = "GITEE_ACCESS_TOKEN";
    public static String GITEE_CLIENT_ID = "GITEE_CLIENT_ID";
    public static String GITEE_CLIENT_SECRET = "GITEE_CLIENT_SECRET";
    public static String REDIRECT_URI = "REDIRECT_URI";
    public static String OAUTH_TOKEN_URL = "https://gitee.com/oauth/token";

}
