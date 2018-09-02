package com.perigrine.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

public class NetworkAdapter {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void postWithHttpHeader(Context ctx, String url,
                                          StringEntity entity, String contentType,
                                          AsyncHttpResponseHandler reponseHandler, List<Header> headers) {
        // adding headers source to the client
        client.removeAllHeaders();
        for (Header header : headers) {
            client.addHeader(header.getName(), header.getValue());
        }
        System.out.println("request URL::" + url);
        for (int i = 0; i < headers.size(); i++) {
            System.out.println(headers.get(i).getName() + "::" + headers.get(i).getValue());
        }
        Log.e("entity",":"+entity.toString());
        client.post(ctx, url, entity, contentType, reponseHandler);
    }

    public static void getReponse(Context ctx, String url,
                                  AsyncHttpResponseHandler reponseHandler) {
        System.out.println("request URL::" + url);
        client.get(ctx, url, reponseHandler);
    }


    public static List<Header> getHeaders(Context ctx) {
        List<Header> headers = null;
        try {
            String data = Common.getSavedUserLoginData(ctx);
            JSONObject dataObj = new JSONObject(data).getJSONObject("data");
            headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Content-Type", "application/json"));
            headers.add(new BasicHeader("securityToken",dataObj.getString("securityToken")));
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return headers;
    }

    public static List<Header> getHeadersWithGovtAPI(Context ctx) {
        List<Header> headers = null;
        try {
            String data = Common.getSavedUserLoginData(ctx);
            JSONObject dataObj = new JSONObject(data).getJSONObject("data");
            headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Content-Type", "application/json"));
            headers.add(new BasicHeader("securityToken",dataObj.getString("securityToken")));
            headers.add(new BasicHeader("fuzzyLogic","1"));
            headers.add(new BasicHeader("loggedInOrgId",dataObj.getString("organizationId")));
            Log.e("loggedInOrgId ",":" + dataObj.getString("organizationId"));
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return headers;
    }


}
