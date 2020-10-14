package com.cmall.groupcenter.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 
 * 
 * @author fengl
 * @date 2015-10-20
 * 
 *
 */
public class WebGetImgSizeUtil {
	   public static int getImageSize(String imgUrl) {  
	        URL url =null;
	        int imgSize=0;
			try {
				url=new URL(imgUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10 * 1000);
				String contentSize=conn.getHeaderField("Content-Length");
				if(null!=contentSize){
					imgSize=Integer.parseInt(contentSize.trim());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
	        return imgSize;  
	    }
}
