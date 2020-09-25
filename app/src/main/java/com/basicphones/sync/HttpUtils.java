package com.basicphones.sync;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpUtils {

    private static final String api_url = "http://192.168.100.61:8000/api";
//    private static final String api_url = "https://staging.backup.sunbeamwireless.com/api";
    public static String token_info = "";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get( String url,  RequestParams params, String token,  AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Token " + token);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.get(getAbsoluteUrl(url), params, responseHandler);

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if(!token_info.equals("")){
            client.addHeader("Authorization", "Token " + token_info);
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);

    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
//        return BASE_URL + relativeUrl;
        return api_url + relativeUrl;
    }
    public static void setToken(String token) {
        token_info = token;
    }
    public static void post(MainActivity activity, String url, JSONObject jsonObject, String token, AsyncHttpResponseHandler responseHandler ) throws UnsupportedEncodingException {
        client.addHeader("Authorization", "Token " + token);
        StringEntity entity = new StringEntity(jsonObject.toString());
        client.post(activity.getApplicationContext(), getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }
    public static void post(Context context, String url, JSONObject jsonObject, String token, AsyncHttpResponseHandler responseHandler ) throws UnsupportedEncodingException {
        client.addHeader("Authorization", "Token " + token);
        StringEntity entity = new StringEntity(jsonObject.toString());
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }
}
