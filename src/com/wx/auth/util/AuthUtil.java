package com.wx.auth.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

public class AuthUtil {

	public static final String APPID="wx5353f36c08558bf9";
	public static final String APPSECRET="7ccfb7587fb1396332e0ade869f22d83";
	public static JSONObject doGetJson(String url) throws ClientProtocolException, IOException{
		JSONObject jsonObject=null;
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet httpGet=new HttpGet(url);
		HttpResponse response=client.execute(httpGet);
		HttpEntity entity=response.getEntity();
		if(entity!=null){
			String result=EntityUtils.toString(entity,"UTF-8");
			jsonObject=JSONObject.fromObject(result);
		}
		httpGet.releaseConnection();
		return jsonObject;
	}
}
