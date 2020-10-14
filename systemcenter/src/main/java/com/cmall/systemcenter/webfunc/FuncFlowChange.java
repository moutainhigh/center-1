package com.cmall.systemcenter.webfunc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.service.FlowService;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**   
* 通用审批处理函数
* 项目名称：systemcenter   
* 类名称：FuncFlowChange   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-18 下午2:02:06   
* 修改人：yanzj
* 修改时间：2013-9-18 下午2:02:06   
* 修改备注：   
* @version    
*    
*/
public class FuncFlowChange extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			FlowService fs = new FlowService();
			
			String flowCode=mSubMap.get("flow_code");
			String fromStatus= mSubMap.get("current_status");
			String toStatus=mSubMap.get("to_status");
			
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			
			String userCode=userInfo.getUserCode();
			String remark=mSubMap.get("remark");
			String roleCode=StringUtils.defaultIfBlank(
					userInfo.getUserRole(), WebConst.CONST_WEB_EMPTY);
					
			String[] fcs = flowCode.split(",");
			
			int success = 0;
			for(String fc : fcs){
				if(StringUtils.isBlank(fc)) continue;
				RootResult ret = fs.ChangeFlow(fc, fromStatus, toStatus, userCode,roleCode,remark,mSubMap);
				//添加判断跳过财务自动审核
				this.autoJudgeSkipCW(fc, fromStatus, toStatus, userCode,roleCode,remark,mSubMap);
				mResult.setResultCode(ret.getResultCode());
				if(ret.getResultCode() == 1){
					success++;
					mResult.setResultMessage(bInfo(949701000));
				}
				else{
					mResult.setResultMessage(ret.getResultMessage());
				}
			}
			
			if(success > 0 && success < fcs.length){
				mResult.setResultCode(0);
				mResult.setResultMessage("总审批数："+fcs.length+"，成功数 :"+success);
			}
			
			if("4497172300160005".equals(toStatus)) {
				//更新分类商品数量表
				XmasKv.upFactory(EKvSchema.IsUpdateCategoryProductCount).set("isUpdateCateProd","update");
				//更新品牌商品数量表
				XmasKv.upFactory(EKvSchema.IsUpdateBrandProductCount).set("isUpdateBrandProductCount","update");
			}
			
		}

		return mResult;
	}

	private void autoJudgeSkipCW(String fc, String fromStatus, String toStatus, String userCode, String roleCode,
			String remark, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		//方案一：创建一个虚拟系统用户并赋予财务审核权限   
		//方案二：给运营赋予临时财务审核权限，然后对这个操作流程做一个系统审核标记
		//测试/灰度
        /*  if(!roleCode.contains("4677031800010005"))
        {
        	roleCode = roleCode+"|4677031800010005";
        }*/
		//生产
	    if(!roleCode.contains("4677031800010021"))
        {
        	roleCode = roleCode+"|4677031800010021";
        }
		
		FlowService fs = new FlowService();
		if("4497172300130004".equals(fromStatus)&&"4497172300130001".equals(toStatus)) {
			List<Map<String,Object>> listMap =DbUp.upTable("pc_skuprice_change_flow").dataSqlList("select * from pc_skuprice_change_flow  where  flow_code = :flow_code ", new MDataMap("flow_code",fc));
			if(listMap!=null) {
				boolean flag = true;
				for (Map<String, Object> map : listMap) {
					BigDecimal cost_price = new BigDecimal(map.get("cost_price").toString());
					BigDecimal sell_price = new BigDecimal(map.get("sell_price").toString());
					//float pencent =(sell_price.floatValue()-cost_price.floatValue())/sell_price.floatValue();
					BigDecimal pencent = sell_price.subtract(cost_price).divide(sell_price,5,RoundingMode.HALF_EVEN);
					if(pencent.compareTo(new BigDecimal(bConfig("familyhas.autoShenHeRate")))==-1) {
						flag = false;
						break;
					}
				}
				if(flag) {
					fs.ChangeFlow(fc, "4497172300130001", "4497172300130002", userCode,roleCode,"系统自动审核通过",mSubMap);
					//对这条操作记录进行自动标记 操作表：sc_flow_history   pc_skuprice_change_flow
					MDataMap md = new MDataMap();
					md.put("flow_code", fc);
					md.put("current_status", "4497172300130002");
					md.put("status", "4497172300130002");
					md.put("auto_flag", "1");
					DbUp.upTable("sc_flow_history").dataUpdate(md, "auto_flag", "flow_code,current_status");
					DbUp.upTable("pc_skuprice_change_flow").dataUpdate(md, "auto_flag", "flow_code,status");
				}
			}
			
		}

	}

}
