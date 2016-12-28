package com.loyo.oa.upload.alioss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 【阿里云oss】工具类
 * Created by yyy on 16/8/10.
 */
public class AliOSSManager {
    public static final int OSS_SUCCESS = 122;
    public static final int OSS_FAILURE = 123;
    public static final int OSS_ERROR1 = 124;
    public static final int OSS_ERROR2 = 125;

    private static AliOSSManager instance = null;

    private static OSS oss;

    public AliOSSManager() {
    }

    public static AliOSSManager getInstance() {
        if (instance == null) {
            instance = new AliOSSManager();
        }
        return instance;
    }

    public void initWithContext(Context context) {

        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {

                OssToken ossToken = AttachmentService.syncGetServerToken();

                if (ossToken != null && ossToken.Credentials != null) {
                    String ak = ossToken.Credentials.AccessKeyId;
                    String sk = ossToken.Credentials.AccessKeySecret;
                    String token = ossToken.Credentials.SecurityToken;
                    String expiration = ossToken.Credentials.Expiration;

                    return new OSSFederationToken(ak, sk, token, expiration);

                } else {
                    return null;
                }
            }
        };

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        oss = new OSSClient(context, Config_project.endpoint, credentialProvider, conf);
    }


    public OSS getOss() {
        if (null == oss) {
            LogUtil.d(" 《《《《《《《《《《《《《《《《《---------------阿里云OSS为空-------------------》》》》》》》》》》》》》》》》》 ");
        }
        return oss;
    }

}
