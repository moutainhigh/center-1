package com.cmall.systemcenter.common;

public class TagHtml {

	
	
	public static String AddWarnHtml(String sContent,String sAddInfo)
	{
		
		String sPathString="<div style=\"height:60px;background-color: #ffff00;border:solid 5px #ff0000;color:#ff0000;text-align:center;font-size:30px;font-weight:bold;line-height:60px;\">"+sAddInfo+"</div>";
		
		String sReturn=sContent.replace("<body>", "<body>"+sPathString);
		
		return sReturn;
	}
	
	
}
