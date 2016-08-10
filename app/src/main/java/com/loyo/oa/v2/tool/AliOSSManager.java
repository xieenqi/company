package com.loyo.oa.v2.tool;

import android.content.Context;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

/**
 * 【阿里云oss】工具类
 * Created by yyy on 16/8/10.
 */
public class AliOSSManager {

    private static AliOSSManager instance = null;

    private static OSS oss;

    public AliOSSManager() {
    }

    public static AliOSSManager getInstance() {
        if(instance == null){
            instance = new AliOSSManager();
        }
        return instance;
    }

    public void init(Context context, final String ak, final String sk, final String token, final String expiration){

        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                return new OSSFederationToken(ak, sk, token, expiration);
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
        return oss;
    }

}
