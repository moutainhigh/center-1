package com.cmall.ordercenter.common;

/**   
*    
* 项目名称：ordercenter   
* 类名称：SqlCommon   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 下午3:03:13   
* 修改人：yanzj
* 修改时间：2013-9-2 下午3:03:13   
* 修改备注：   
* @version    
*    
*/
public class SqlCommon {
	

	/**
	 * 成功标志
	 */
	public final static int SuccessFlag=1;
	
	/**
	 * 插入数据库的值的过滤条件
	 * 
	 * @param str
	 * @return
	 */
	public static String TransactSQLInjection(String str)
	{
		return str.replaceAll(".*([';]+|(--)+).*", " ");
	}

}
