package com.cmall.bbcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * 编辑供应商的状态
 * @author jl
 *
 */
public class FuncEditStatusForSupperInfo  extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		MDataMap mDataMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		
		String action_=mDataMaps.get("action_");
		String uid=mDataMaps.get("uid");

		//判断登陆用户是否为空
		String loginname=UserFactory.INSTANCE.create().getLoginName();
		if(loginname==null||"".equals(loginname)){
			mResult.inErrorMessage(941901073);
			return mResult;
		}
		
		//判断是否可用更改操作
		Map<String, Object> dmap=DbUp.upTable("bc_supplier_info").dataSqlOne("select supplier_code, `status` from bc_supplier_info where uid=:uid", new MDataMap("uid",uid));
		
		if(dmap==null||dmap.size()<1){
			mResult.inErrorMessage(909401006);
			return mResult;
		}
		
		String supplier_code=(String)dmap.get("supplier_code");
		String status=(String)dmap.get("status");//当前供销商状态
		
		status=checkUpdateStatus(uid, action_, status);//检测下一个状态
		
		if("".equals(status)){
			mResult.inErrorMessage(909401007);
			return mResult;
		}
		
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		String now=df.format(new Date());
		mInsertMap.put("update_time", now);   // 更新时间
		mInsertMap.put("update_user", loginname);   // 更新的用户
		mInsertMap.put("uid", uid);
		mInsertMap.put("status", status);
		
		// 该处事务性不强，所以不做事务
		DbUp.upTable("bc_supplier_info").dataUpdate(mInsertMap, "", "uid");
		DbUp.upTable("bc_supplier_log").dataInsert(new MDataMap("supplier_code",supplier_code,"action_",action_,"update_user",loginname,"update_time",now));
		
		mResult.setResultMessage(bInfo(969909001));
		
		return mResult;
	}
	
	//状态：449746570001:未签合同  449746570002:已签合同  449746570003:进行中  449746570004:已冻结  449746570005:合作终止
	//操作: 449746580001:创建   449746580002:编辑  449746580003:已签合同  449746580004:启动  449746580005:冻结   449746580006:终止
	//次操作中不包含   [449746580001:创建   449746580002:编辑 ]
	private String checkUpdateStatus(String uid,String action_,String status){
		String status_new="";
		if("449746580003".equals(action_)){		//已签 操作
			if("449746570001".equals(status)){		//当未签合同时才能做次操作
				status_new="449746570002";
			}
		}else if("449746580004".equals(action_)){		//启动 操作
			if("449746570002".equals(status)||"449746570004".equals(status)){		//当已签合同 和冻结 时才能做次操作
				status_new="449746570003";
			}
		}else if("449746580005".equals(action_)){		//冻结 操作
			if("449746570003".equals(status)){		//当进行中 才能做次操作
				status_new="449746570004";
			}
		}else if("449746580006".equals(action_)){		//终止 操作
			if(!"449746570005".equals(status)){
				status_new="449746570005";			//所有状态都可以终止 并且不可逆
			}
		}
		
		return status_new;
	}
	
}
