package com.cmall.groupcenter.txservice;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.cmall.dborm.txmapper.groupcenter.GcPayOrderInfoMapper;
import com.cmall.dborm.txmapper.groupcenter.GcPayOrderLogMapper;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfo;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderInfoExample;
import com.cmall.dborm.txmodel.groupcenter.GcPayOrderLog;
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
 * 提款单批量审核通过
 * @Author GaoYang
 * @CreateDate 2015年4月29日下午3:58:15
 */
public class BatchChangeOrderStatusPassService extends RootFunc{

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
									//更新用户付款单据日志表
									GcPayOrderLogMapper gcPayOrderLogMapper=BeansHelper
											.upBean("bean_com_cmall_dborm_txmapper_GcPayOrderLogMapper");
								    GcPayOrderLog gcPayOrderLog=new GcPayOrderLog();
								    gcPayOrderLog.setUid(WebHelper.upUuid());
								    gcPayOrderLog.setPayOrderCode(orderCode);
								    gcPayOrderLog.setOrderStatus("4497153900120002");//审核通过
								    gcPayOrderLog.setPayStatus("4497465200070001");//未支付
								    gcPayOrderLog.setUpdateTime(FormatHelper.upDateTime());
								    gcPayOrderLog.setUpdateUser(mUserInfo.getUserCode());
								    gcPayOrderLogMapper.insertSelective(gcPayOrderLog);
									
								    //更新审核状态
								    GcPayOrderInfo updateInfo=new GcPayOrderInfo();
									updateInfo.setAuditTime(FormatHelper.upDateTime());//审核时间
									updateInfo.setOrderStatus("4497153900120002");//审核通过
									gcPayOrderInfoMapper.updateByExampleSelective(updateInfo,gcPayOrderInfoExample);
									
									//录入批处理结果数据
									mInsMap.put("pay_order_code", orderCode);
									mInsMap.put("operate_content", "4497153900120002");//操作内容为审核通过
									mInsMap.put("operate_flag", "449746250001");//操作成功
									AddNewData(mInsMap);
									
								}else{
									//录入批处理结果数据
									mInsMap.put("pay_order_code", orderCode);
									mInsMap.put("operate_content", "4497153900120002");//操作内容为审核通过
									mInsMap.put("operate_flag", "449746250002");//操作未成功
									AddNewData(mInsMap);
								}
							}catch(Exception e){
								//异常时,录入批处理结果数据
								mInsMap.put("pay_order_code", orderCode);
								mInsMap.put("operate_content", "4497153900120002");//提款单现有的状态
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
