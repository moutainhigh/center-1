package com.cmall.ordercenter.alipay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequester {
	private URL url;
    private HttpURLConnection httpURLConn;
    public String myDoGet(String responseURL)
    {
    	String temp = new String();
    	StringBuffer str = new StringBuffer();
    	try
        {
            url = new  URL(responseURL);
            httpURLConn= (HttpURLConnection)url.openConnection();
            httpURLConn.setDoOutput(true);
            httpURLConn.setRequestMethod("POST");
            httpURLConn.setIfModifiedSince(999999999);
            httpURLConn.setRequestProperty("Referer", "");
            httpURLConn.setRequestProperty("User-Agent", "");
            httpURLConn.connect();
            InputStream in =httpURLConn.getInputStream();
            BufferedReader bd = new BufferedReader(new InputStreamReader(in));
            while((temp=bd.readLine())!=null)
            {
            	str.append(bd.readLine());
            }            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        finally
        {
            if(httpURLConn!=null)
            {
                httpURLConn.disconnect();
            }
        }
        return str.toString();
    }

}
