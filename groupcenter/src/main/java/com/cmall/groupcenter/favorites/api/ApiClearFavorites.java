package com.cmall.groupcenter.favorites.api;



import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.favorites.model.FavoriteInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 清空收藏列表
 * @author chenxk
 *
 */
public class ApiClearFavorites extends RootApiForToken<RootResultWeb, FavoriteInput>{

	
	public RootResultWeb Process(FavoriteInput inputParam,
			MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		
		MDataMap memberInfo = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode());
		if(memberInfo == null){
			result.inErrorMessage(915805334);
		}
		//更新的帖子id
		List<MDataMap> pidList = null;
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, memberInfo.get("account_code"));
		
		if(!StringUtils.isEmpty(sLockCode)){
			if(result.upFlagTrue()){
				//判断是否是全部清空
				if(inputParam.getIsEmpty() == 1){
					//先判断收藏列表是否为空
					if(DbUp.upTable("nc_collections").dataCount("member_code=:member_code and app_code=:app_code and flag=:flag", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001")) > 0){
						pidList = DbUp.upTable("nc_collections").queryAll("post_id", "", "member_code=:member_code and app_code=:app_code and flag=:flag", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001"));
						
						DbUp.upTable("nc_collections").dataExec("update nc_collections set  flag=:flag1  where member_code=:member_code and app_code=:app_code and flag=:flag ", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001","flag1","4497472000020002"));
					}else{
						result.inErrorMessage(918519018);
					}
				}else{//部分删除
					if(StringUtils.isEmpty(inputParam.getcId())){
						result.inErrorMessage(918519018);
					}else{
						if(DbUp.upTable("nc_collections").dataCount("member_code=:member_code and app_code=:app_code and flag=:flag and collection_id=:collection_id", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001","collection_id",inputParam.getcId())) > 0){
							pidList = DbUp.upTable("nc_collections").queryAll("post_id", "", "member_code=:member_code and app_code=:app_code and flag=:flag and collection_id=:collection_id", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001","collection_id",inputParam.getcId()));
							
							DbUp.upTable("nc_collections").dataExec("update nc_collections set  flag=:flag1  where member_code=:member_code and app_code=:app_code and flag=:flag and collection_id=:collection_id", new MDataMap("member_code",getUserCode(),"app_code",getManageCode(),"flag","4497472000020001","flag1","4497472000020002","collection_id",inputParam.getcId()));
						}
					}
				}
			}
			// 解鎖
			WebHelper.unLock(sLockCode);
		}
		//更新帖子收藏数量
		if(pidList != null && pidList.size() > 0) {
			for(MDataMap pidMap : pidList) {
				String pid = pidMap.get("post_id");
				// 锁定帖子
				String sLockCodePid = WebHelper.addLock(500, pid);
				if(!StringUtils.isEmpty(sLockCode)){
					//查询帖子的收藏数量
					Object collectNumObj = DbUp.upTable("nc_post").dataGet("actual_collect_num", "pid=:pid", new MDataMap("pid", pid));
					if(null == collectNumObj) {
						continue;
					}
					int collectNum = (Integer)collectNumObj;
					collectNum--;
					DbUp.upTable("nc_post").dataUpdate(new MDataMap("pid", pid, "actual_collect_num", collectNum+""), "actual_collect_num", "pid");
					
					// 解鎖
					WebHelper.unLock(sLockCodePid);
				}
			}
		}
		return result;
	}
}
