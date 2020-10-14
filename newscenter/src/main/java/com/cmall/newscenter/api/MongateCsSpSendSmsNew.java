package com.cmall.newscenter.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.cmall.newscenter.model.MongateCsSpSendSmsNewInput;
import com.cmall.newscenter.model.MongateCsSpSendSmsNewResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class MongateCsSpSendSmsNew extends RootApiForManage<MongateCsSpSendSmsNewResult, MongateCsSpSendSmsNewInput>{

	public MongateCsSpSendSmsNewResult Process(
			MongateCsSpSendSmsNewInput inputParam, MDataMap mRequestMap) {
		
		MongateCsSpSendSmsNewResult result = new MongateCsSpSendSmsNewResult();
		
		if(result.upFlagTrue()){
			
			
			  try {
				  
				String data = "userId="+inputParam.getUserId()+"&password="+inputParam.getPassword()+"&pszMobis="+inputParam.getPszMobis()+"&pszMsg="+ URLEncoder.encode(inputParam.getPszMsg(), "UTF-8")+"&iMobiCount="+inputParam.getiMobiCount()+"&pszSubPort="+inputParam.getPszSubPort()+"";
				  
				URL url = new URL("http://61.145.229.29:9003/MWGate/wmgw.asmx/MongateCsSpSendSmsNew");
				
				URLConnection conn = url.openConnection();    
				
				conn.setDoOutput(true);    
				
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
				
				wr.write(data);   
				
				wr.flush();
				
				
				String ret_str = "";
				
				 BufferedReader rd = new BufferedReader(new InputStreamReader(conn.          getInputStream()));   
				 
				 String line;    
				 while ( (line = rd.readLine()) != null) {  
					 
					 ret_str += line;   
					 }   
				 
				 wr.close();  
				 
				 rd.close();    
				
				 result.setUrl(ret_str);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		return result;
	}
	
	

}
