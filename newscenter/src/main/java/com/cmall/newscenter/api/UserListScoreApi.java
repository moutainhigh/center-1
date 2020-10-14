package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.ScoreHistory;
import com.cmall.newscenter.model.UserListScoreInput;
import com.cmall.newscenter.model.UserListScoreResult;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 用户 - 积分记录
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserListScoreApi extends RootApiForToken<UserListScoreResult, UserListScoreInput> {

	public UserListScoreResult Process(UserListScoreInput inputParam,
			MDataMap mRequestMap) {
		MPageData userScoreList= new MPageData();
		UserListScoreResult result = new UserListScoreResult();
		if(result.upFlagTrue()){
			List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
			MDataMap whereInfoMember = new MDataMap();
			whereInfoMember.put("member_code", getUserCode());
			userScoreList = DataPaging.upPageData("nc_integral_process", "", "", whereInfoMember, inputParam.getPaging());
			if(userScoreList.getListData().size() == 0){
				return result;
			}
			result.setPaged(userScoreList.getPageResults());//分页信息
			wMDataMap = userScoreList.getListData();//用户参与的活动列表
			String operation_code = "";//操作编号
			for (int i=0;i<wMDataMap.size();i++) {
				MDataMap mDataMap = wMDataMap.get(i);
				ScoreHistory history = new ScoreHistory();
				history.setCreated_at(mDataMap.get("create_time"));// 创建时间
				operation_code = mDataMap.get("operation_code");
				MDataMap m = DbUp.upTable("nc_integral").one("operation_code",operation_code,"genus_app",getManageCode());
				if("459746500001".equals(operation_code)){
					//发表评论/回复
					history.setAction(m.get("operation_name"));
					history.setScore("+"+mDataMap.get("socre"));// 积分变化
					MDataMap mComMap = DbUp.upTable("nc_comment").one("info_code",mDataMap.get("evaluation_code"),"manage_code",getManageCode());
					history.setTarget(mComMap == null ? "" : mComMap.get("comment_info"));
					
				}else if("459746500002".equals(operation_code) || "459746500003".equals(operation_code)){
					//商品文字评价    商品图片评价
					history.setAction(m.get("operation_name"));
					history.setScore("+"+mDataMap.get("socre"));// 积分变化
					MDataMap mComMap = DbUp.upTable("nc_order_evaluation").one("order_skuid",mDataMap.get("evaluation_code"));
					history.setTarget(mComMap == null ? "" : mComMap.get("order_assessment"));
				}else if("459746500004".equals(operation_code)){
					//活动报名
					history.setAction(m.get("operation_name"));
					history.setScore("+"+mDataMap.get("socre"));// 积分变化
					MDataMap mComMap = DbUp.upTable("nc_info").one("info_code",mDataMap.get("evaluation_code"),"manage_code",getManageCode());
					history.setTarget(mComMap == null ? "" : mComMap.get("info_title"));
				}else if(null ==operation_code || "".equals(operation_code)){
					history.setAction("试用商品");
					history.setScore("-"+mDataMap.get("socre"));// 积分变化
					MDataMap orderMap = DbUp.upTable("oc_tryout_products").one("sku_code",mDataMap.get("evaluation_code"),"app_code",getManageCode());
					history.setTarget(orderMap == null ? "" : orderMap.get("sku_name"));
				}else{
					history.setAction(m.get("operation_name"));
					history.setScore("+"+mDataMap.get("socre"));// 积分变化
				}
				
				result.getHistory().add(history);
			}
		}
		return result;
	}

}
