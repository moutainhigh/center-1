package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 *  对于商品评论状态进行修改
 * @author houwen
 *
 */
public class productCommentStatus extends RootFunc {

	
	private static String TABLE_TPL="nc_order_evaluation"; //商品评论表
	
	/**
	 * 
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		// 待审核 审核通过 审核拒绝：4497172100030001、4497172100030002、4497172100030003
		String isDisable = mDataMap.get("zw_f_isDisable");
		if("4497172100030001".equals(isDisable)){  //禁用的话 记录时间
			
				dataMap.put("check_flag", "4497172100030002");
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "check_flag", "uid");
		
		}else if("4497172100030003".equals(isDisable)){
			dataMap.put("check_flag", "4497172100030002");
		//	dataMap.put("disableDate", FormatHelper.upDateTime());
			DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "check_flag", "uid");
		}else{
			mResult.setResultCode(934205104);
			mResult.setResultMessage("状态已为审核通过！");
		}
		

		return mResult;
	}

}
