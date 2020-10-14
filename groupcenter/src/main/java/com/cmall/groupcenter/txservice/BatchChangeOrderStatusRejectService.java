package com.cmall.groupcenter.txservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.groupcenter.GcPayOrderDetailMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderLogMapper;
import com.cmall.dborm.txmapper.groupcenter.GcReckonLogMapper;
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
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 提款单批量审核不通过
 * @Author GaoYang
 * @CreateDate 2015年4月30日上午10:07:10
 */
public class BatchChangeOrderStatusRejectService extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult=new MWebResult();
		
		String pOrderCode = mDataMap.get("zw_f_pay_order_code");
		
		if(mWebResult.upFlagTrue()){
			if(StringUtils.isNotBlank(pOrderCode)){
				//按照换行符截取提款单号
				String[] orderAry = pOrderCode.split("\n");
				
				if(orderAry.length >0){
					
					//先清除表【gc_batchchange_orderstatus】中数据
					DbUp.upTable("gc_batchchange_orderstatus").dataDelete("1=1", new MDataMap(), "");
					
					MUserInfo mUserInfo = UserFactory.INSTANCE.create();
					
					for(int i=0;i<orderAry.length;i++){
						//提款单号为空不录入
						String orderCode = orderAry[i].toString().trim();
						if(StringUtils.isNotBlank(orderCode)){
							
							MDataMap mInsMap = new MDataMap();
							
							GcPayOrderInfoMapper gcPayOrderInfoMapper = BeansHelper
									.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderInfoMapper");
							GcPayOrderInfoExample gcPayOrderInfoExample=new GcPayOrderInfoExample();
							gcPayOrderInfoExample.createCriteria().andPayOrderCodeEqualTo(orderCode);
							
							GcPayOrderInfo gcPayOrderInfo= new GcPayOrderInfo();
							String orderStatus = "";
							List<GcPayOrderInfo> gcOrderInfoList = gcPayOrderInfoMapper.selectByExample(gcPayOrderInfoExample);
							if(gcOrderInfoList != null && gcOrderInfoList.size() > 0){
								gcPayOrderInfo = gcOrderInfoList.get(0);
								orderStatus = gcPayOrderInfo.getOrderStatus();//提款单状态
							}
							
							try{
								//待审核
								if("4497153900120001".equals(orderStatus)){
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
								    gcPayOrderLog.setPayOrderCode(orderCode);
								    gcPayOrderLog.setOrderStatus("4497153900120003");//审核不通过
								    gcPayOrderLog.setPayStatus("4497465200070001");//未支付
								    gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
								    gcPayOrderLog.setUpdateUser(mUserInfo.getUserCode());
								    gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
									
								    //付款单信息明细对应金额改回
								    GcPayOrderDetailMapper gcPayOrderDetailMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderDetailMapper");
								    GcPayOrderDetailExample gcPayOrderDetailExample=new GcPayOrderDetailExample();
								    gcPayOrderDetailExample.createCriteria().andPayOrderCodeEqualTo(orderCode);
								    List<GcPayOrderDetail> detailList=gcPayOrderDetailMapper.selectByExample(gcPayOrderDetailExample);
								    for(GcPayOrderDetail gcPayOrderDetail:detailList){
								    	GcReckonLogMapper gcReckonLogMapper = BeansHelper
												.upBean("bean_com_cmall_dborm_txmapper_GcReckonLogMapper");
								        GcReckonLogExample gcReckonLogExample=new GcReckonLogExample();
								        gcReckonLogExample.createCriteria().andOrderCodeEqualTo(gcPayOrderDetail.getOrderCode())
								        .andAccountCodeEqualTo(gcPayOrderInfo.getAccountCode()).andReckonChangeTypeEqualTo("4497465200030001");//订单返利
								        GcReckonLog gcReckonLog=gcReckonLogMapper.selectByExample(gcReckonLogExample).get(0);
								        GcReckonLog updateReckon=new GcReckonLog();
								        updateReckon.setPayedMoney(gcReckonLog.getPayedMoney().subtract(gcPayOrderDetail.getReckonMoney()));
								        updateReckon.setZid(gcReckonLog.getZid());
								        gcReckonLogMapper.updateByPrimaryKeySelective(updateReckon);
								    }
									
								    //push申请提现消息
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
									
									//更新审核状态
								    GcPayOrderInfo updateInfo=new GcPayOrderInfo();
									updateInfo.setAuditTime(FormatHelper.upDateTime());//审核时间
									updateInfo.setOrderStatus("4497153900120003");//审核不通过
									gcPayOrderInfoMapper.updateByExampleSelective(updateInfo,gcPayOrderInfoExample);
									
									//录入批处理结果数据
									mInsMap.put("pay_order_code", orderCode);
									mInsMap.put("operate_content", "4497153900120003");//操作内容为审核不通过
									mInsMap.put("operate_flag", "449746250001");//操作成功
									AddNewData(mInsMap);
									
								}else{
									//录入批处理结果数据
									mInsMap.put("pay_order_code", orderCode);
									mInsMap.put("operate_content", "4497153900120003");//操作内容为审核不通过
									mInsMap.put("operate_flag", "449746250002");//操作未成功
									AddNewData(mInsMap);
								}
							}catch(Exception e){
								//异常时,录入批处理结果数据
								mInsMap.put("pay_order_code", orderCode);
								mInsMap.put("operate_content", "4497153900120003");//操作内容为审核不通过
								mInsMap.put("operate_flag", "449746250002");//操作未成功
								AddNewData(mInsMap);
							}
						}
					}
				}
			}else{
				mWebResult.setResultMessage(bInfo(915805227));
			}

		}
		
		return mWebResult;
	}

	/**
	 * 向表【gc_batchchange_orderstatus】中录入数据
	 * @param mInsMap
	 */
	private void AddNewData(MDataMap mInsMap) {
		DbUp.upTable("gc_batchchange_orderstatus").dataInsert(mInsMap);
	}
}
