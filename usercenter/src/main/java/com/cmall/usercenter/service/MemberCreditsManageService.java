package com.cmall.usercenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.usercenter.model.api.ApiMemberCreditsManageInput;
import com.cmall.usercenter.model.api.ApiMemberCreditsManageResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：usercenter 
 * 类名称：     MemberCreditsManageService 
 * 类描述：     会员积分管理
 * 创建人：     GaoYang  
 * 创建时间：2013年11月9日下午2:20:26 
 * 修改人：     GaoYang
 * 修改时间：2013年11月9日下午2:20:26
 * 修改备注：  
 *
 */
public class MemberCreditsManageService extends BaseClass{

	/**
	 * 积分管理
	 * @param mcmi 接口输入参数
	 * @return
	 */
	public ApiMemberCreditsManageResult CreditsManage(
			ApiMemberCreditsManageInput mcmi) {
		
		ApiMemberCreditsManageResult result = new ApiMemberCreditsManageResult();
		try{
			//获取输入接口信息
			//会员ID
			String userId = mcmi.getUserId();
			//积分状态
			int creditsStatus = mcmi.getCreditsStatus();
			//积分值
			int creditsValue = mcmi.getCreditsValue();
			//积分内容
			String creditsContent = mcmi.getCreditsContent();
			//积分备注
			String creditsMemo = mcmi.getCreditsMemo();
			
			//判断会员ID非空
			if(StringUtils.isBlank(userId)){
				result.setResultCode(959701016);
				result.setResultMessage(bInfo(959701016));
				return result;
			}
			
			//判断积分状态（积分状态，0为无操作，1为增加积分，2为消减积分,9为清零）
			if(creditsStatus== 0){
				result.setResultCode(959701017);
				result.setResultMessage(bInfo(959701017,"积分状态为0"));
				return result;
			}else if(creditsStatus != 1 && creditsStatus != 2 && creditsStatus != 9){
				result.setResultCode(959701018);
				result.setResultMessage(bInfo(959701018));
				return result;
			}
			
			//判断本次积分值(积分清零除外)
			if(creditsStatus != 9 && creditsValue == 0){
				result.setResultCode(959701017);
				result.setResultMessage(bInfo(959701017,"积分值为0"));
				return result;
			}else if(creditsStatus != 9 && creditsValue < 0){
				result.setResultCode(959701019);
				result.setResultMessage(bInfo(959701019));
				return result;
			}
			
			//检查会员ID在会员表中是否存在
			if(!checkUserId(userId)){
				result.setResultCode(959701020);
				result.setResultMessage(bInfo(959701020));
				return result;
			}
			
			//获取积分流水表中该用户的现有积分（增减后积分）
			int creditsAmount = 0;
			MDataMap oneDataMap = DbUp.upTable("uc_memberinfo").one("user_id",userId);
			if(oneDataMap != null){
				creditsAmount = Integer.parseInt(oneDataMap.get("user_credits"));
			}
			
			//以下是会员积分增减判断(暂时处理，以后变更)
			//增加积分时，现有积分累加新增积分
			if(creditsStatus == 1){
				creditsAmount += creditsValue;
			}else if(creditsStatus == 2){
				//消减积分大于现有积分时
				if(creditsValue > creditsAmount){
//					creditsAmount = 0;
					result.setResultCode(959701023);
					result.setResultMessage(bInfo(959701023));
					return result;
				}else{
					//消减积分小于等于现有积分时，现有积分减去消减积分
					creditsAmount -= creditsValue;
				}
			}else if(creditsStatus == 9){
				//积分清零
				creditsAmount = 0;
				creditsValue = 0;
			}
			
			//进行积分流水管理
			MDataMap insMap = new MDataMap();
			insMap.put("user_id", userId);
			//积分状态转货为系统状态值
			if(creditsStatus == 1){
				insMap.put("credits_status", "449746320001");
			}else if(creditsStatus == 2){
				insMap.put("credits_status", "449746320002");
			}else if(creditsStatus == 9){
				insMap.put("credits_status", "449746320003");
			}
			insMap.put("credits_time", DateUtil.getSysDateTimeString());
			insMap.put("credits_content", creditsContent);
			insMap.put("credits_value", String.valueOf(creditsValue));
			insMap.put("credits_amount", String.valueOf(creditsAmount));
			insMap.put("credits_memo", creditsMemo);
			DbUp.upTable("lc_credits_detail").dataInsert(insMap);
			
			//将最新积分更新到会员表中
			MDataMap updDataMap = new MDataMap();
			updDataMap.put("user_id", userId);
			updDataMap.put("user_credits", String.valueOf(creditsAmount));
			//以"会员ID"为单位更新
			DbUp.upTable("uc_memberinfo").dataUpdate(updDataMap,"user_credits", "user_id");
			
		}catch(Exception e){
			result.setResultCode(959701021);
			result.setResultMessage(bInfo(959701021));
		}
		
		return result;
	}

	/**
	 * 检查会员ID在会员表中是否存在
	 * @param userId 会员ID
	 * @return
	 */
	private boolean checkUserId(String userId) {
		if(DbUp.upTable("uc_memberinfo").count("user_id", userId) >0){
			return true;
		}
		return false;
	}
	
}
