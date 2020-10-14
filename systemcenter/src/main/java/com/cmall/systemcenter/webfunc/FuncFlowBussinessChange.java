package com.cmall.systemcenter.webfunc;

import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cmall.systemcenter.common.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import com.cmall.systemcenter.dcb.PushReturnGoodsStatusService;
import com.cmall.systemcenter.dcb.PushSkuStatusService;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapdata.helper.KvHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncFlowBussinessChange extends RootFunc {
	
	private static final long TIMEOUT = 30000;

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			FlowBussinessService fs = new FlowBussinessService();
			
			String flowBussinessUid=mSubMap.get("flow_bussinessid");
			String fromStatus= mSubMap.get("from_status");
			String toStatus=mSubMap.get("to_status");
			String flowType = mSubMap.get("flow_type");
			
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			String userName = userInfo.getRealName();
			String userCode=userInfo.getUserCode();
			String remark=mSubMap.get("remark");
			MDataMap product=DbUp.upTable("pc_productinfo").one("uid",flowBussinessUid);
			//三方商品上架时进行sku是否有可售判断
            mResult = this.checkSanFangProductIfSale(product,toStatus);
            if(mResult.getResultCode()==0) {
            	return mResult;
            }

			RootResult ret =
					fs.ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, mSubMap);
			
			mResult.setResultCode(ret.getResultCode());
			if(ret.getResultCode() == 1){
				mResult.setResultMessage(bInfo(949701000));
				if (null != product) {
					PlusHelperNotice.onChangeProductInfo(product.get("product_code"));
					//触发消息队列
					ProductJmsSupport pjs = new ProductJmsSupport();
					pjs.onChangeForProductChangeAll(product.get("product_code"));
					
					//下架进行缓存刷新
					if("4497153900060003".equals(toStatus)) {
						pjs.updateSolrData(product.get("product_code"));
						//更新分类商品数量表
						XmasKv.upFactory(EKvSchema.IsUpdateCategoryProductCount).set("isUpdateCateProd","update");
						//更新品牌商品数量表
						XmasKv.upFactory(EKvSchema.IsUpdateBrandProductCount).set("isUpdateBrandProductCount","update");
					
					}
			        }
				//自营商品处理为下架状态是，向多彩宝推送商品状态
				if("4497153900060003".equals(toStatus)&&"449715390006".equals(flowType)){
					this.pushProductStatus(flowBussinessUid);
				}
				//售后商家确认退货状态通知多彩
				if("449715390005".equals(flowType)||"449715390002".equals(flowType)){
					this.pushReturnGoodsStatus(toStatus, flowBussinessUid,flowType);
					this.returnBalanceForChannel(toStatus, flowBussinessUid,flowType);//如果是渠道商用户，需要归还渠道商扣款金额。
				}
				//添加下架记录任务表，用于定时刷新缓存，更新数据使用
				if(("4497153900060003".equals(toStatus)||"4497153900060004".equals(toStatus))&& null != product) {
					MDataMap paramMap = new MDataMap();
					paramMap.put("uid",  UUID.randomUUID().toString().replace("-", ""));
					paramMap.put("product_code",product.get("product_code"));
					paramMap.put("create_time", FormatHelper.upDateTime());
					paramMap.put("operator", userName);
					DbUp.upTable("pc_product_xiajia_recording_task").dataInsert(paramMap);
					// 手动下架,更新商品"自动上架"字段为"否"
					DbUp.upTable("pc_productinfo").dataUpdate(new MDataMap("product_code",product.get("product_code"),"auto_sell","449748400002"), "auto_sell", "product_code");

				}
				//商品重新上架时，删除已有的下架记录，防止定时更新查表时，仍然把重新上架的商品作为下架商品处理
				if("4497153900060002".equals(toStatus)&& null != product) {
					DbUp.upTable("pc_product_xiajia_recording_task").delete("product_code",product.get("product_code"));
				}
			}else{
				mResult.setResultMessage(ret.getResultMessage());
			}

		}

		return mResult;
	}
	
	/**
	 * 归还渠道商扣款金额
	 * @param toStatus
	 * @param flowBussinessUid
	 * @param flowType
	 */
	private void returnBalanceForChannel(String toStatus, String flowBussinessUid, String flowType) {
		if("449715390005".equals(flowType)) {//退货
			if(!"4497153900050001".equals(toStatus)){//4497153900050001
				return;//非退货完成的单子不做处理
			}
			MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",flowType);
			MDataMap flowMain = DbUp.upTable(flowTypeData.get("table_name")).one("uid",flowBussinessUid);
			String return_code =  flowMain.get("return_code");
			String order_code = flowMain.get("order_code");
			//校验是否是渠道商订单
			MDataMap channelOrder = DbUp.upTable("oc_order_channel").one("order_code",order_code);
			if(channelOrder == null|| channelOrder.isEmpty()) {
				return;//不是渠道商订单
			}
			String channel_seller_code = channelOrder.get("channel_seller_code");
			String returnMoney = flowMain.get("expected_return_money");
			BigDecimal  return_money = new BigDecimal(returnMoney); 
			Integer count = rebackBalance(channel_seller_code,return_money,return_code);
			if(count == 0) {//更新失敗
				//写入定时任务，定时执行返还渠道商预付款
				MDataMap jobMap = new MDataMap();
				jobMap.put("uid", WebHelper.upUuid());
				jobMap.put("exec_code", WebHelper.upCode("ET"));
				jobMap.put("exec_type", "449746990023");
				jobMap.put("exec_info", return_code);
				jobMap.put("create_time", DateUtil.getSysDateTimeString());
				jobMap.put("begin_time", "");
				jobMap.put("end_time", "");
				jobMap.put("exec_time", DateUtil.getSysDateTimeString());
				jobMap.put("flag_success","0");
				jobMap.put("remark", "渠道商退货返回预付款");
				jobMap.put("exec_number", "0");
				DbUp.upTable("za_exectimer").dataInsert(jobMap);
			}
		}
		
	}
	
	/**
	 * 
	 */
	public Integer rebackBalance(String channel_seller_code,BigDecimal balance,String returnCode) {
		Integer count = 0;
		long begin = System.currentTimeMillis();
		String lockKey = "deduction" + channel_seller_code;
		String lockCode = "";
		try {
			//锁定渠道商
			while("".equals(lockCode = KvHelper.lockCodes(10, lockKey))) {
				if(System.currentTimeMillis() - begin > TIMEOUT) {
				}
			}
			MDataMap channel = DbUp.upTable("uc_channel_sellerinfo").one("channel_seller_code", channel_seller_code);
			BigDecimal advanceBalance = new BigDecimal(channel.get("advance_balance"));
			//增加
			BigDecimal newAdvanceBalance = advanceBalance.add(balance).setScale(2, RoundingMode.HALF_DOWN);
			count = DbUp.upTable("uc_channel_sellerinfo").dataUpdate(new MDataMap("channel_seller_code", channel_seller_code, "advance_balance",newAdvanceBalance.toString()),"advance_balance","channel_seller_code");
			//生成扣减日志
			if(count > 0) {
				DbUp.upTable("lc_operation_channel_money")
				.insert("uid", UUID.randomUUID().toString().replace("-", ""),
						"channel_seller_code", channel_seller_code,
						"operation_type","449748420003",
						"operation_money",balance.toString(),
						"advance_balance",newAdvanceBalance.toString(),
						"operation_time",FormatHelper.upDateTime(),
						"trigger_code",returnCode,
						"remark","退货完成返还用户预付款");
			}
		} finally {
			if(!"".equals(lockCode)) KvHelper.unLockCodes(lockCode, lockKey);
		}
		return count;
	}

	private MWebResult checkSanFangProductIfSale(MDataMap product,String toStatus) {
		MWebResult mResult=new MWebResult();
		if(product!=null&&"4497153900060002".equals(toStatus)&&("SF03WYKLPT".equals(product.get("small_seller_code"))
				|| "SF031JDSC".equals(product.get("small_seller_code")))) {
			//上架时对商品中是否有可售的sku进行判断
		    boolean flag = false;
			List<MDataMap> productSkuInfoListMap = DbUp.upTable("pc_skuinfo").queryAll("", "",
					"product_code=:product_code", new MDataMap("product_code", product.get("product_code")));
			if (productSkuInfoListMap != null) {
				int size = productSkuInfoListMap.size();
				for (int i = 0; i < size; i++) {
					//存在可售的sku即可上架
					if("Y".equals(productSkuInfoListMap.get(i).get("sale_yn"))) {
						flag=true;
					    break;	
					}
				}
			}
			if(!flag) {
				mResult.setResultCode(0);
				mResult.setResultMessage("无可售的sku,不可上架！");
				return mResult;
			}
		}
		return mResult;
	}

	private void pushReturnGoodsStatus(String toStatus,String uuid,String flowType){
		MDataMap flowTypeData = DbUp.upTable("sc_flow_bussinesstype").one("flow_type",flowType);
		MDataMap flowMain = DbUp.upTable(flowTypeData.get("table_name")).one("uid",uuid);
		PushReturnGoodsStatusService service = new PushReturnGoodsStatusService();
		String return_code = "";
		String type = "";
		String status = "";
		String statusCode = toStatus;
		if("449715390005".equals(flowType)){//退货处理
			type = "0";//退货Type
			if("4497153900050001".equals(toStatus)){
				status = "通过审核(收货入库)";
			}else{
				status = "否决审核";
			}
			return_code = flowMain.get("return_code");
		}
		if("449715390002".equals(flowType)){//换货处理
			type = "1";//换货处理
			if("4497153900020004".equals(toStatus)){
				status = "审核通过";
			}else{
				status = "审核失败";
			}
			return_code = flowMain.get("exchange_no");
		}
		service.pushReturnGoodsStatus(return_code, statusCode,type,status);
		/**
		 * 退货流转，如果是商家审核通过入库，需要检查是否是分销商品。
		 */
		if("4497153900050001".equals(toStatus)) {//退货申请成功并且是分销订单，需要写入定时
			String order_code = "";
			MDataMap returnInfo = DbUp.upTable("oc_return_goods").one("return_code",return_code);
			if(returnInfo != null && !returnInfo.isEmpty()) {
				order_code = returnInfo.get("order_code");
			}
			if(StringUtils.isNotEmpty(order_code)&&DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0) {
				JobExecHelper.createExecInfo("449746990027",return_code, FormatHelper.upDateTime(DateUtils.addMinutes(new Date(), 5), "yyyy-MM-dd HH:mm:ss"));
			}
		}
	}
	
	/**
	 * @desc 处理多彩宝商品状态为下架。
	 * @author AngelJoy
	 * @param flowBussinessUid 商品SKU uid
	 */
	private void pushProductStatus(String flowBussinessUid){
		MDataMap product=DbUp.upTable("pc_productinfo").one("uid",flowBussinessUid);
		if(null != product){
			String productCode =  product.get("product_code")!=null?product.get("product_code").toString():"";
			String sql = "select * from productcenter.pc_bf_skuinfo where product_code = "+"\""+productCode+"\"";
			List<Map<String,Object>> list = DbUp.upTable("pc_bf_skuinfo").dataSqlList(sql, null);
			String skuCode = "";
			for(int i = 0;i<list.size();i++){
				if(i != list.size()-1){
					skuCode += list.get(i).get("sku_code").toString()+",";
				}else{
					skuCode += list.get(i).get("sku_code").toString();
				}
			}
			PushSkuStatusService service = new PushSkuStatusService();
			service.pushSkuStatus(skuCode, "N", 1, "自营商品下架，多彩宝对应商品下架");
		}
	}

}
