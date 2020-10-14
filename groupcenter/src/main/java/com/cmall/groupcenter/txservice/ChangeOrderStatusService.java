package com.cmall.groupcenter.txservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcAccountChangeLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcGroupAccountMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcWithdrawLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcAccountChangeLog;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccount;
import com.cmall.dborm.txmodel.groupcenter.GcGroupAccountExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderDetailExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLog;
import com.cmall.dborm.txmodel.groupcenter.GcReckonLogExample;
import com.cmall.dborm.txmodel.groupcenter.GcWithdrawLog;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * 提现审核相关
 * @author chenbin@ichsy.com
 *
 */
public class ChangeOrderStatusService extends BaseClass implements IFlowFunc  {

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		RootResult rootResult=new RootResult();
		GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
				.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
		GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
		gcPayOrderInfoExample.createCriteria().andUidEqualTo(flowCode);
		GcPayOrderInfo gcPayOrderInfo=gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample).get(0);
		MUserInfo mUserInfo = UserFactory.INSTANCE.create();
		String remark=mSubMap.get("remark");
		GcPayOrderInfo updateInfo=new GcPayOrderInfo();
		if(StringUtils.isNotEmpty(remark)){
			updateInfo.setRemark(remark);	
		}
		//审核通过
		if(toStatus.equals("4497153900120002")){
			//更新用户付款单据日志表
			GcPayOrderLogMapper gcPayOrderLogMapper=BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
		    GcPayOrderLog gcPayOrderLog=new GcPayOrderLog();
		    gcPayOrderLog.setUid(WebHelper.upUuid());
		    gcPayOrderLog.setPayOrderCode(gcPayOrderInfo.getPayOrderCode());
		    gcPayOrderLog.setOrderStatus("4497153900120002");//审核通过
		    gcPayOrderLog.setPayStatus("4497465200070001");//未支付
		    gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
		    gcPayOrderLog.setUpdateUser(mUserInfo.getUserCode());
		    gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
		}
		//审核不通过
		if(toStatus.equals("4497153900120003")){
			//插入提现账户日志表
			GcWithdrawLog gcWithdrawLog=new GcWithdrawLog();
			gcWithdrawLog.setUid(WebHelper.upUuid());
			gcWithdrawLog.setAccountCode(gcPayOrderInfo.getAccountCode());
			gcWithdrawLog.setMemberCode(gcPayOrderInfo.getMemberCode());
			gcWithdrawLog.setWithdrawMoney(gcPayOrderInfo.getWithdrawMoney());
			gcWithdrawLog.setWithdrawChangeType("4497465200040005");
			gcWithdrawLog.setChangeCodes(gcPayOrderInfo.getPayOrderCode());
			TxGroupAccountService txGroupAccountService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxGroupAccountService");
			List<GcWithdrawLog> listWithdrawLogs = new ArrayList<GcWithdrawLog>();
			listWithdrawLogs.add(gcWithdrawLog);
			txGroupAccountService.updateAccount(null, listWithdrawLogs);
			//更新用户付款单据表,已更新
			//更新用户付款单据日志表
			GcPayOrderLogMapper gcPayOrderLogMapper=BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
		    GcPayOrderLog gcPayOrderLog=new GcPayOrderLog();
		    gcPayOrderLog.setUid(WebHelper.upUuid());
		    gcPayOrderLog.setPayOrderCode(gcPayOrderInfo.getPayOrderCode());
		    gcPayOrderLog.setOrderStatus("4497153900120003");//审核不通过
		    gcPayOrderLog.setPayStatus("4497465200070001");//未支付
		    gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
		    gcPayOrderLog.setUpdateUser(mUserInfo.getUserCode());
		    gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
		    //付款单信息明细对应金额改回
		    GcPayOrderDetailMapper gcPayOrderDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderDetailMapper");
		    GcPayOrderDetailExample gcPayOrderDetailExample=new GcPayOrderDetailExample();
		    gcPayOrderDetailExample.createCriteria().andPayOrderCodeEqualTo(gcPayOrderInfo.getPayOrderCode());
		    List<GcPayOrderDetail> detailList=gcPayOrderDetailMapper.selectByExample(gcPayOrderDetailExample);
		    for(GcPayOrderDetail gcPayOrderDetail:detailList){
		    	GcReckonLogMapper gcReckonLogMapper = BeansHelper
						.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
		        GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
		        gcReckonLogExample.createCriteria().andOrderCodeEqualTo(gcPayOrderDetail.getOrderCode())
		        .andAccountCodeEqualTo(gcPayOrderInfo.getAccountCode()).andReckonChangeTypeEqualTo("4497465200030001");
		        GcReckonLog gcReckonLog=gcReckonLogMapper.selectByExample(gcReckonLogExample).get(0);
		        GcReckonLog updateReckon=new GcReckonLog();
		        updateReckon.setPayedMoney(gcReckonLog.getPayedMoney().subtract(gcPayOrderDetail.getReckonMoney()));
		        updateReckon.setZid(gcReckonLog.getZid());
		        gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
		    }
		    //push申请提现消息
			try {
				AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
				addSinglePushCommentInput.setAccountCode(gcPayOrderInfo.getAccountCode());
				addSinglePushCommentInput.setAppCode(GroupConst.GROUP_APP_MANAGE_CODE);
				addSinglePushCommentInput.setType("44974720000400010002");
				
				addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
				addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
				addSinglePushCommentInput.setTitle("非常抱歉，您于"+gcPayOrderInfo.getCreateTime().substring(0, 10)+"的提现申请失败");
				MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",gcPayOrderInfo.getAccountCode(),"manage_code",GroupConst.GROUP_APP_MANAGE_CODE,"flag_enable","1");
				if(memberMap!=null){
					addSinglePushCommentInput.setUserCode(memberMap.get("member_code"));
				}

			    String content="请核对个人信息后重新申请，如有问题请联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#。";
			    addSinglePushCommentInput.setContent(content);
				
				if(DbUp.upTable("gc_account_push_set").count("account_code",gcPayOrderInfo.getAccountCode(),"push_type_id","1ca93003edb4499aa62ffac0e352bb80","push_type_onoff","449747100002")<1){
				    addSinglePushCommentInput.setSendStatus("4497465000070001");
				}
				else{
					addSinglePushCommentInput.setSendStatus("4497465000070002");
				}
				SinglePushComment.addPushComment(addSinglePushCommentInput);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		updateInfo.setAuditTime(FormatHelper.upDateTime());
		gcPayOrderInfoMapper.updateByExampleSelective(updateInfo,gcPayOrderInfoExample);
		return rootResult;
	}

}
