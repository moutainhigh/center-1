package com.cmall.groupcenter.homehas;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigSyncGoodbyColor;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelGoodbyColor;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncGoodbyColor;
import com.cmall.groupcenter.homehas.model.RsyncResponseSyncGoodbyColor;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webclass.WarnCount;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步家有 按商品编号查看颜色款式
 * @author jl
 *
 */
public class RsyncSyncgetSYGoodbyColor extends RsyncHomeHas<RsyncConfigSyncGoodbyColor, RsyncRequestSyncGoodbyColor, RsyncResponseSyncGoodbyColor> {

	final static RsyncConfigSyncGoodbyColor RSYNC_CONFIG_SYNC_GOODBYCOLOR = new RsyncConfigSyncGoodbyColor();
	
	
	public RsyncConfigSyncGoodbyColor upConfig() {
		return RSYNC_CONFIG_SYNC_GOODBYCOLOR;
	}

	private RsyncRequestSyncGoodbyColor request = new RsyncRequestSyncGoodbyColor();
	public RsyncRequestSyncGoodbyColor upRsyncRequest() {
		// 返回输入参数
		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_day(rsyncDateCheck.getStartDate());
		request.setEnd_day(rsyncDateCheck.getEndDate());
		return request;
	}
	
	@Override
	protected boolean isUpdateStaticValue() {
		// 只同步单个商品时不更新定时的时间标量
		return StringUtils.isBlank(request.getGood_id());
	}
	
	private boolean success=false;
	
	private synchronized MWebResult handGood (RsyncModelGoodbyColor goodbyColor) throws Exception{
		MWebResult mWebResult = new MWebResult();
		
		String good_id = goodbyColor.getGood_id();//商品编号
//		String mdf_date = goodbyColor.getMdf_date();//更新时间
		String color_id = goodbyColor.getColor_id();//颜色编号
		String color_desc = goodbyColor.getColor_desc();//颜色名称
		String style_id = goodbyColor.getStyle_id();//款式编号
		String style_desc = goodbyColor.getStyle_desc();//款式名称
		String sale_yn = goodbyColor.getSale_yn();//是否可卖
		
		
		String key="color_id="+color_id+"&style_id="+style_id;
		String keyvalue=FormatHelper.formatString(bConfig("groupcenter.RsyncSyncgetSYGoodbyColor.skuper"),color_desc,style_desc);
		
		
		//同步过来的商品同时属于家有惠和惠家有,所以要循环两遍.
		String [] apps = new String[]{MemberConst.MANAGE_CODE_HPOOL,MemberConst.MANAGE_CODE_HOMEHAS};
		
		for (String app_code : apps) {
			
			String productCode = good_id;
			if(MemberConst.MANAGE_CODE_HPOOL.equals(app_code)){ //家有惠的商品编号前要加9
				productCode = ("9"+good_id);
			}
			//查询现有的sku信息
			List<Map<String, Object>> skuList = DbUp.upTable("pc_skuinfo").dataSqlList(
					"SELECT zid,uid,sku_code_old,sku_code,product_code,sell_price,cost_price,market_price,stock_num,sku_key,sku_keyvalue,sku_picurl,sku_name,sku_adv,sell_productcode,seller_code,security_stock_num,product_code_old,qrcode_link,sell_count,sale_yn "
					+ " FROM pc_skuinfo where product_code=:product_code ",
					new MDataMap("product_code",productCode));
			if(skuList==null||skuList.size()<1){
				
				mWebResult.setResultCode(918501001);
				mWebResult.setResultMessage(bInfo(918501001, good_id));
//				mWebResult.getResultList().add(mWebResult.getResultMessage());
				continue;
			}
			
			boolean statusChange = false; // 商品状态变更标识
			boolean flagMod = true;
			String product_code=(String)skuList.get(0).get("product_code");
			MDataMap productMap=DbUp.upTable("pc_productinfo").one("product_code",product_code);
			String sellerCode = (String) productMap.get("seller_code");			
			String smallSellerCode = (String) productMap.get("small_seller_code");
			String productStatus=(String) productMap.get("product_status");
			String productUID=(String) productMap.get("uid");
			if (AppConst.MANAGE_CODE_HOMEHAS.equals(sellerCode) && !AppConst.MANAGE_CODE_HOMEHAS.equals(smallSellerCode)) {
				flagMod = false;
			}
			if (flagMod) {
				
				
				boolean bflag = true;
				for (Map<String, Object> map : skuList) {
					
					//查看是否已经保存了该属性
					String zid=String.valueOf(map.get("zid"));
					String sku_key=(String)map.get("sku_key");
					String sku_keyvalue=(String)map.get("sku_keyvalue");
					String sale_yn_data=(String)map.get("sale_yn");
					
					if(StringUtils.isBlank(sku_key)||(key.equals(sku_key)&&!keyvalue.equals(sku_keyvalue))){//修改
						DbUp.upTable("pc_skuinfo").dataUpdate(new MDataMap("zid",zid,"sku_key",key,"sku_keyvalue",keyvalue,"sale_yn",sale_yn), "", "zid");
						bflag = false;
						break;
					}else if(key.equals(sku_key)&&!sale_yn_data.equals(sale_yn)){
						DbUp.upTable("pc_skuinfo").dataUpdate(new MDataMap("zid",zid,"sku_key",key,"sku_keyvalue",keyvalue,"sale_yn",sale_yn), "", "zid");
						bflag = false;
						break;
					}else if (key.equals(sku_key)&&keyvalue.equals(sku_keyvalue)){ // 已经添加过的不再添加
						bflag = false;
						break;
					}
					
				}
				
				if(bflag){
					Map<String, Object> map=skuList.get(0);
					map.remove("uid");
					map.remove("zid");
					map.put("sku_code",WebHelper.upCode("8019"));
					map.put("sku_key",key);
					map.put("sku_keyvalue",keyvalue);
					map.put("sale_yn",sale_yn);
					DbUp.upTable("pc_skuinfo").dataInsert(new MDataMap(map));
				}
				
				//添加以下逻辑：商品下所有SKU均不可售时，商品自动下架并发邮件。如果自动下架的商品下SKU可售了，系统发邮件
	//			String product_code=(String)skuList.get(0).get("product_code");
	//			MDataMap productMap=DbUp.upTable("pc_productinfo").one("product_code",product_code);
				MDataMap productExtMap=DbUp.upTable("pc_productinfo_ext").one("product_code",product_code);
				
				// 是否自动上架
				String auto_sell = productMap.get("auto_sell");
				String product_status=productMap.get("product_status");
				String product_name=productMap.get("product_name");
				String poffer=productExtMap.get("poffer");
				
				if("Y".equals(sale_yn)){//商品可售的情况下
					/*if("4497153900060003".equals(product_status)&&"system".equals(poffer)){
						
						//发邮件
						sendWx("Y",product_name,product_code);
					}*/

					if("449748400001".equals(auto_sell)) {
						// 如果允许自动上架,且商品未上架,则调用上架接口直接上架商品
						if("4497153900060003".equals(product_status)){
							//上架商品
							String flowBussinessUid=productUID;
							String fromStatus= "4497153900060003";
							String toStatus="4497153900060002";
							String flowType = "449715390006";
							String userCode = "jobsystem";
							String remark="有可售SKU，系统自动上架！";
							new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
							
							statusChange = true;
						}
					}else {
						// 不能自动上架,走原有功能
						if("4497153900060003".equals(product_status)&&"system".equals(poffer)){
							
							//发邮件
							sendWx("Y",product_name,product_code);
						}
					}
				}else if("N".equals(sale_yn)){
					//重新查询该商品下所有的sku的状态
					
					if("4497153900060002".equals(product_status)){
						
						if(DbUp.upTable("pc_skuinfo").count("product_code",product_code,"sale_yn","Y")<1){
							//下架商品
//							DbUp.upTable("pc_productinfo").dataUpdate(new MDataMap("product_code",product_code,"product_status","4497153900060003"), "product_status", "product_code");
							
							String flowBussinessUid=productUID;
							String fromStatus= "4497153900060002";
							String toStatus="4497153900060003";
							String flowType = "449715390006";
							String userCode = "jobsystem";
							String remark="所有SKU均不可售，系统自动下架！";
							new FlowBussinessService().ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, userCode, remark, new MDataMap());
							
							DbUp.upTable("pc_productinfo_ext").dataUpdate(new MDataMap("product_code",product_code,"poffer","system"), "poffer", "product_code");
							//微信通知
							sendWx("N",product_name,product_code);
							
							statusChange = true;
						}
					}
				}
				//商品信息更新成功，开始刷新缓存
				PlusHelperNotice.onChangeProductInfo(product_code);
				
				// 商品触发状态变更时再刷新缓存
				if(statusChange) {
					//触发消息队列
					ProductJmsSupport pjs = new ProductJmsSupport();
					pjs.onChangeForProductChangeAll(product_code);
				}
			}
		}
		
		return mWebResult;
	}
	
	
	public RsyncResult doProcess(RsyncRequestSyncGoodbyColor tRequest,RsyncResponseSyncGoodbyColor tResponse) {
		RsyncResult result = new RsyncResult();

		
		if(!"true".equals(tResponse.getSuccess())){
			return result;
		}
		
		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (tResponse != null && tResponse.getResult() != null) {
			result.setProcessNum(tResponse.getResult().size());
		} else {
			result.setProcessNum(0);
		}
		
		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				
				result.setProcessNum(tResponse.getResult().size());
				
				//此处过滤变态的响应数据,各种重复数据
				//相同 商品的相同属性，取修改时间最大的
				
				Map<String , RsyncModelGoodbyColor> filterMap=new HashMap<String, RsyncModelGoodbyColor>();
				List<RsyncModelGoodbyColor> list=tResponse.getResult();
				
				for (RsyncModelGoodbyColor goodbyColor : list) {
					String good_id = goodbyColor.getGood_id();//商品编号
					String color_id = goodbyColor.getColor_id();//颜色编号
					String style_id = goodbyColor.getStyle_id();//款式编号
					String mdf_date = goodbyColor.getMdf_date();//更新时间
					
					String key=good_id+color_id+style_id;
					RsyncModelGoodbyColor goodbyColor2=filterMap.get(key);
					if(goodbyColor2==null){
						filterMap.put(key, goodbyColor);
						continue;
					}
					
					//开始比对时间
					String mdf_date2=goodbyColor2.getMdf_date();
					if(compare(mdf_date, mdf_date2)>0){
						filterMap.put(key, goodbyColor);
					}
				}
				
				for (Map.Entry<String , RsyncModelGoodbyColor> map : filterMap.entrySet()) {
					
					String lock_key=WebHelper.addLock(10, map.getValue().getGood_id());
					
					
					MWebResult mResult = new MWebResult();

					if(StringUtils.isNotEmpty(lock_key)){
						try {
							mResult = handGood(map.getValue());
						} catch (Exception e) {
							e.printStackTrace();
						}
						WebHelper.unLock(lock_key);
					}
					
					
					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;

					} else {
						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}
						result.getResultList().add(mResult.getResultMessage());
					}
					
				}
				
				// 设置处理信息
				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));
			}
		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {

			result.setSuccessNum(iSuccessSum);

			result.setStatusData(tRequest.getEnd_day());
		}

		success=true;
		
		return result;
	}

	public RsyncResponseSyncGoodbyColor upResponseObject() {
		return new RsyncResponseSyncGoodbyColor();
	}
	
	
	public boolean isSuccess(){
		return success;
	}
	
	/**
	 * 比较两个时间
	 * 时间格式：2014-12-02 20:14:10
	 * <br>大于结束时间返回正数，等于 0，小于 负数
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	private synchronized int compare(String start_time,String end_time){
		try {
			
			if(StringUtils.isBlank(start_time)){
				return -1;
			}
			
			if(StringUtils.isBlank(end_time)){
				return 1;
			}
			
			Date date1=DateUtil.sdfDateTime.parse(start_time);
			Date date2=DateUtil.sdfDateTime.parse(end_time);
			return date1.compareTo(date2);
		} catch (Exception e) {
			return 1;
		}
	}
	
	
	private void sendMail(String sale_yn,String product_name,String product_code){
		
		String receives[]= bConfig("groupcenter.offPro_sendMail_receives_"+sale_yn).split(",");
		String title= bConfig("groupcenter.offPro_sendMail_title_"+sale_yn);
		String content= bConfig("groupcenter.offPro_sendMail_content_"+sale_yn);
		
		for (String receive : receives) {
			if(StringUtils.isNotBlank(receive)){
				MailSupport.INSTANCE.sendMail(receive, FormatHelper.formatString(title,product_code,product_name), FormatHelper.formatString(content,product_code,product_name));
			}
		}
	}
	
	private void sendWx(String sale_yn,String product_name,String product_code){
		
		String receices[] = bConfig("groupcenter.offPro_sendWx_receives_"+sale_yn).split(",");
		String content = TopUp.upConfig("groupcenter.offPro_sendWx_content_"+sale_yn);
		
		for (String receive : receices) {
			if(StringUtils.isNotBlank(receive)){
				WarnCount count = new WarnCount();
				count.sendWx(receive , FormatHelper.formatString(content,product_code,product_name));
			}
		}
		
	}	
	
}
