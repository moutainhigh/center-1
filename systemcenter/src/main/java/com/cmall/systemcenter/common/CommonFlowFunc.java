/**
 * 
 */
package com.cmall.systemcenter.common;

import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**   
 *    
 * 项目名称：systemcenter   
 * 类名称：CommonFlowFunc   
 * 类描述：   
 * 创建人：yanzj  
 * 创建时间：2013-9-17 上午9:20:37   
 * 修改人：yanzj
 * 修改时间：2013-9-17 上午9:20:37   
 * 修改备注：   
 * @version    
 *    
 */
public class CommonFlowFunc implements IFlowFunc {
	
	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult ret = new RootResult();
		//System.out.println("CommonFlowFunc-BeforeTest");
		
		if(mSubMap!=null)
		{
			if(mSubMap.contains("remark")){
				
			}
			//	System.out.println(mSubMap.get("remark"));
		}
		
		ret.setResultCode(1);
		
		return ret;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult ret = new RootResult();
		//System.out.println("CommonFlowFunc-AfterTest");
		
		
		if(mSubMap!=null)
		{
			if(mSubMap.contains("remark")){
				
			}
				//System.out.println(mSubMap.get("remark"));
		}
		
		ret.setResultCode(1);
		return ret;
	}

}
