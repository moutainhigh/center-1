package com.cmall.groupcenter.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * @author shiyz
 */
public class JobSQLInjectionQuery extends RootJob {

//	static String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"  
//            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)"; 
//	
	static String reg = "([s][e][l][e][c][t])|([u][p][d][a][t][e])|([i][n][s][e][r][t])|([d][e][l][e][t][e]){1}"; 
	static String reg1 = "([\\'][\\\"]){1}"; 
    static String reg2 = "([\\'][\\s*][\\\"]){1}";
	
    static Pattern sqlPatternPrefix = Pattern.compile(reg); 
    static Pattern sqlPatternSuffix = Pattern.compile(reg1);
    static Pattern sqlPatternSuf = Pattern.compile(reg2);
	
	@Override
	public void doExecute(JobExecutionContext context) {
		
		//"C:\\Users\\Administrator\\git"
		
		GetTestXlsFilName(this.getClass().getResource("/").getPath().split("git")[0]+"/git");  
	
	}	  
	    public static void GetTestXlsFilName(String fileAbsolutePath) {  
	    	
	        
	        File file = new File(fileAbsolutePath);  
	        
	        File[] subFile = file.listFiles();  
	        
	        String result = "";
	        
	        MDataMap mDataMap = new MDataMap();
	        
	        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	        
	        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {  
	            // 判断是否为文件夹  
	            if (!subFile[iFileLength].isDirectory()) {  
	            	
	                String fileName = subFile[iFileLength].getName();  
	                
	                // 判断是否为.java结尾  
	                if (fileName.trim().toLowerCase().endsWith(".java")) {  
	                	
	                	result = readfileString(subFile[iFileLength]);  
	                	
	                	String[] reStrings =  result.split(";");
	                	
	                	for(int i=0;i<reStrings.length;i++){
	                		
	                		Matcher m=sqlPatternPrefix.matcher(p.matcher(reStrings[i]).replaceAll(""));
	                		
	                		Matcher m1=sqlPatternSuffix.matcher(p.matcher(reStrings[i]).replaceAll(""));
	                		
	                		Matcher m2=sqlPatternSuf.matcher(p.matcher(reStrings[i]).replaceAll(""));
	                		
	                		if (m.find() && (m1.find()||m2.find())) { 
	    	                	
		                		if(result.contains("import")){
		                		
		                		mDataMap.put("class_name", p.matcher(result.substring(0,result.indexOf("import")).replace("package", "").replace(";","")).replaceAll("")+"."+fileName);
		                		
		                		}else{
		                		
		                		mDataMap.put("class_name", p.matcher(result.substring(0,result.indexOf("public")).replace("package", "").replace(";","")).replaceAll("")+"."+fileName);	
		                			
		                		} 
		                			
		                		if(result.contains("@author")){
		                			
		                			mDataMap.put("class_author", p.matcher(result.substring(result.indexOf("@author")+7,result.indexOf("@author")+14).replace("*", "").replace(" ", "").replace("/","")).replaceAll(""));
		                		
		                		}else {
									
		                			mDataMap.put("class_author", "administrator");
		                			
								}
		                		mDataMap.put("class_content", p.matcher(reStrings[i]).replaceAll(""));
		                		
		                		DbUp.upTable("sc_sql_query_injection").dataInsert(mDataMap);
		                		
		                	}
	                		
	                	}
	                	
	                	
	                }
	            } else {
					
	            	GetTestXlsFilName(subFile[iFileLength].toString());
	            	
				} 
	        }  
	    }  
		
		
		// TODO Auto-generated method stub

	    public static void main(String[] args) throws JobExecutionException {  
	       
	    	JobSQLInjectionQuery jodSql = new JobSQLInjectionQuery();
	    	
	    	jodSql.execute(null);
	    }  
	    
	    
	    public static String readfileString(File file){
	    	
	             String result = "";
	             
	             try{
	    	             BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
	    	            String s = null;
	    	            
	    	            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
	    	            	
	    	            		result = result + "\n" +s;
	    	                
	    	            }
	    	            br.close();    
	    	        }catch(Exception e){
	                 e.printStackTrace();
	    	         }
	    	        return result;
	    	    } 
	    
}
