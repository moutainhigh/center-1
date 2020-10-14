package com.cmall.groupcenter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *
 * 字符串模板的帮助类,即将模板中的特殊符号替换为需要的变量值
 * @author lipengfei
 * @date 2015-5-21
 * email:lipf@ichsy.com
 *
 */
public class StringTemplateHelper {
	
	private static final String LOG_VARIABLE_FLAG="{0}";
	

    static final char SBC_CHAR_START = 65281; // 全角！

   
    static final char SBC_CHAR_END   = 65374; // 全角～

   
    static final int  CONVERT_STEP   = 65248; // 全角半角转换间隔

   
    static final char SBC_SPACE      = 12288; // 全角空格 12288

   
    static final char DBC_SPACE      = ' ';  // 半角空格
    
	/**
	 * 将日志中的变量替换为需要的变量，并输出
	 * @author lipengfei
	 * @date May 10, 2015
	 * @param content 调用日志模版
	 * @param variableFlag 需要替换的变量标记
	 * @param strings
	 * @return
	 */
		private static String outputLog(String content,String variableFlag,String ...variables ){
			
//			for (String variable : variables) {
//				content  = content.replaceFirst(SystemConstants.LOG_VARIABLE_FLAG, variable);
//			}
			
			String flag = variableFlag.replace("{", "\\{").replace("}", "\\}");
			
			for (int i = 0; i < variables.length; i++) {
				
				//每次更新第一个出现的即可
				content = replace(content, 0, flag, variables[i]);
			}
			
			return content;
		}
		
		
		
		
		/**
	     * 在source中，指定位置index的  before 替换成  after
	     * @param   //index = 0,1,2,3,....
	     *返回替换后的结果
	     */
		public static String replace(String source,int index,String before ,String after){
		    Matcher matcher = Pattern.compile(before).matcher(source);
		    for(int counter = 0;matcher.find();counter++) 
		        if(counter == index)
		        return source.substring(0,matcher.start())+ after + source.substring(matcher.end(),source.length());   
		    return source;
		    }
		
		
		/**
		 * 将字串模板中的变量符号替换为需要的变量，并输出
		 * @author lipengfei
		 * @date 2015-5-21
		 * @param content 调用字串模版
		 * @param variables 传入的变量
		 * @return
		 */
		public static String outputLog(String content,String ...variables ){
			return outputLog(content,LOG_VARIABLE_FLAG, variables);
		}
		

}
