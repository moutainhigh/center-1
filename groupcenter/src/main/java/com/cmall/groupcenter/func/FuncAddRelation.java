package com.cmall.groupcenter.func;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加关系
 * 
 * @author srnpr
 * 
 */
public class FuncAddRelation extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MDataMap mInputMap = upFieldMap(mDataMap);

		GroupAccountSupport groupAccountSupport = new GroupAccountSupport();

		return groupAccountSupport.createRelation(
				mInputMap.get("account_code"), mInputMap.get("parent_code"),
				FormatHelper.join(UserFactory.INSTANCE.create().getUserCode(),
						mInputMap.get("remark")),"");

	}

}
