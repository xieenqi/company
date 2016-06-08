package com.loyo.oa.v2.common.http;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.Config_project;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class ServerAPI {

    private static String tag = "ServerAPI";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static AsyncHttpClient asyncHttpClient;

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int PUT = 3;
    public static final int DELETE = 4;

    protected ServerAPI() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, Class<?> cls) {
        request(rootObject, requestMode, urlOperation, null, null, null, null, null, cls, null);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, String strJSON, Class<?> cls) {
        request(rootObject, requestMode, urlOperation, null, null, null, null, strJSON, cls, null);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, HttpEntity entity, String contentType, Class<?> cls) {
        request(rootObject, requestMode, urlOperation, null, entity, contentType, null, null, cls, null);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, HttpEntity entity, String contentType, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
        request(rootObject, requestMode, urlOperation, null, entity, contentType, null, null, cls, lstParamInfo);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, RequestParams params, Class<?> cls) {
        request(rootObject, requestMode, urlOperation, null, null, null, params, null, cls, null);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, RequestParams params, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
        request(rootObject, requestMode, urlOperation, null, null, null, params, null, cls, lstParamInfo);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
        request(rootObject, requestMode, urlOperation, null, null, null, null, null, cls, lstParamInfo);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, Header[] headers, RequestParams params, Class<?> cls) {
        request(rootObject, requestMode, urlOperation, headers, null, null, params, null, cls, null);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, Header[] headers, RequestParams params, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
        request(rootObject, requestMode, urlOperation, headers, null, null, params, null, cls, lstParamInfo);
    }

    public static void request(Object rootObject, int requestMode, String urlOperation, Header[] headers, HttpEntity entity, String contentType, RequestParams params, String strJSON, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
        String url;
        if ("/attachment/".equals(urlOperation.trim())) {
            url = urlOperation.trim().startsWith("http://") ?
                    urlOperation.trim() : Config_project.API_URL_ATTACHMENT() + urlOperation.trim();
        } else {
            url = urlOperation.trim().startsWith("http://") ?
                    urlOperation.trim() : Config_project.API_URL_CUSTOMER() + urlOperation.trim();
        }
        Thread thread = new Thread(new AsyncHttpClienRunnable(rootObject, requestMode, url, headers, entity, contentType, params, strJSON, cls, lstParamInfo));
        thread.run();
    }


    public static void init() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(20000);
    }

    public static class AsyncHttpClienRunnable implements Runnable {
        private String strUrl;
        private RequestParams params;
        private Class<?> cls;
        private ArrayList<ParamInfo> lstParamInfo;
        private Object rootObject;
        private int requestMode;
        private HttpEntity entity;
        private String contentType;
        private String strJSON;

        public AsyncHttpClienRunnable(Object rootObject, int requestMode, String strUrl, Header[] headers, HttpEntity entity, String contentType, RequestParams params, String strJSON, Class<?> cls, ArrayList<ParamInfo> lstParamInfo) {
            this.rootObject = rootObject;
            this.strUrl = strUrl;
            this.params = params;
            this.cls = cls;
            this.lstParamInfo = lstParamInfo;
            this.requestMode = requestMode;
            this.entity = entity;
            this.contentType = contentType;
            this.strJSON = strJSON;
        }

        @Override
        public void run() {
            ResponseHandlerInterface responseHandler = null;
            try {
                //取得全部的构造函数
                Constructor<?> cons[] = cls.getConstructors();

                for (int i = 0; i < cons.length; i++) {
                    int paramNum = cons[i].getParameterTypes().length;
                    if (paramNum == 0) {
                        responseHandler = (ResponseHandlerInterface) cons[i].newInstance();
                    } else if (paramNum == 1) {
                        responseHandler = (ResponseHandlerInterface) cons[i].newInstance(rootObject);
                    }
                }

                if (lstParamInfo != null) {
                    for (ParamInfo paramInfo : lstParamInfo) {
                        setter(responseHandler, paramInfo.getParamName(), paramInfo.getParamValue());
                    }
                }

                Context context_root = null;
                if (rootObject instanceof Activity) {
                    context_root = (Activity) rootObject;
                } else if (rootObject instanceof Fragment) {
                    context_root = ((Fragment) rootObject).getActivity();
                } else if (context_root == null) {
                    context_root = MainApp.getMainApp().getBaseContext();
                }

                asyncHttpClient.removeAllHeaders();
                asyncHttpClient.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));

                switch (requestMode) {
                    case GET:
                        if (entity != null) {
                            String result = getHttpClientJsonData(strUrl, strJSON);
                            Log.d(tag, "result:" + result);
                        } else if (params != null) {
                            asyncHttpClient.get(context_root, strUrl, params, responseHandler);
                        } else {
                            asyncHttpClient.get(context_root, strUrl, responseHandler);
                        }

                        break;
                    case POST:
                        if (entity != null) {
                            asyncHttpClient.post(context_root, strUrl, entity, contentType, responseHandler);
                        } else if (params != null) {
                            asyncHttpClient.post(context_root, strUrl, params, responseHandler);
                        } else {
                            asyncHttpClient.post(strUrl, responseHandler);
                        }
                        break;
                    case PUT:
                        if (entity != null) {
                            asyncHttpClient.put(context_root, strUrl, entity, contentType, responseHandler);
                        } else if (params != null) {
                            asyncHttpClient.put(context_root, strUrl, params, responseHandler);
                        } else {
                            asyncHttpClient.put(strUrl, responseHandler);
                        }
                        break;
                    case DELETE:
                        asyncHttpClient.delete(context_root, strUrl, responseHandler);
                        break;
                }
            } catch (Exception e) {
                Global.ProcException(e);
            }

        }
    }

    public static class ParamInfo {
        private String paramName;
        private Object paramValue;

        public ParamInfo(String paramName, Object paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }

        public Object getParamValue() {
            return paramValue;
        }

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public void setParamValue(Object paramValue) {
            this.paramValue = paramValue;
        }
    }

    /**
     * @param obj        操作的对象
     * @param paramName  操作的属性名
     * @param paramValue 参数值
     */
    public static void setter(Object obj, String paramName, Object paramValue) {
        try {
            String methodName = paramName.replaceFirst(paramName.substring(0, 1), paramName.substring(0, 1).toUpperCase());
            Log.d(tag, "methodName:" + methodName);
            Log.d(tag, "paramValue.getClass():" + paramValue.getClass());
            Log.d(tag, "\"set\" + methodName:" + "set" + methodName);
            Method method = obj.getClass().getMethod("set" + methodName, paramValue.getClass());
            method.invoke(obj, paramValue);
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }

    /**
     * HttpClient访问网络接口 （暂时）
     *
     * @param url
     * @param jsonparams
     * @param
     * @return
     * @throws
     */
    public static String getHttpClientJsonData(String url, String jsonparams) {
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity;
        try {
            Log.d(tag, "jsonparams:" + jsonparams);
            entity = new StringEntity(jsonparams, HTTP.UTF_8);
            entity.setContentType(ServerAPI.CONTENT_TYPE_JSON);
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity resEntity = response.getEntity();
                result = EntityUtils.toString(resEntity, "utf-8");
            } else {

            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Global.ProcException(e);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Global.ProcException(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Global.ProcException(e);
        }

        return result;
    }
}
