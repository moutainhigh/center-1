package com.cmall.groupcenter.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;

import scala.inline;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.third.model.GroupReturnOrderDetail;
import com.cmall.groupcenter.third.model.GroupReturnOrderInput;
import com.cmall.groupcenter.third.model.GroupReturnOrderResult;
import com.cmall.groupcenter.third.model.GroupSetRebateScaleInput;
import com.cmall.groupcenter.third.model.GroupSetRebateScaleResult;
import com.cmall.groupcenter.third.model.GroupUpdateRebateScaleInput;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.RegexConst;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

public class GroupRebateService extends BaseClass{

	public GroupSetRebateScaleResult setRebateScale(GroupSetRebateScaleInput groupSetRebateScaleInput,String manageCode){
		
		GroupSetRebateScaleResult groupSetRebateScaleResult=new GroupSetRebateScaleResult();	
		
		String skuCodeString=groupSetRebateScaleInput.getSkuCode();
		String rebateScaleString=groupSetRebateScaleInput.getRebateScale();
		String productCodeString=groupSetRebateScaleInput.getProductCode();
        String startTimeString=groupSetRebateScaleInput.getStartTime();
        String endTimeString=groupSetRebateScaleInput.getEndTime();
        
        //判断开始时间
  		if(startTimeString==null||StringUtils.isBlank(startTimeString)){
  			startTimeString=FormatHelper.upDateTime();
  		}
  		else{
  			if(DateHelper.parseDate(startTimeString).before(new Date())){
  				groupSetRebateScaleResult.inErrorMessage(918519004);
  			}
  		}
  		
  		//判断结束时间
  		if(groupSetRebateScaleResult.upFlagTrue()){
  			if(endTimeString==null||StringUtils.isBlank(endTimeString)){
  				endTimeString="2099-01-01 00:00:00";
  			}
  			else{
  				if(DateHelper.parseDate(endTimeString).before(new Date())){
  					groupSetRebateScaleResult.inErrorMessage(918519005);
  				}
  			}
  		}
  		
  		//判断开始时间和结束时间
  		if(groupSetRebateScaleResult.upFlagTrue()){
  			if(DateHelper.parseDate(startTimeString).after(DateHelper.parseDate(endTimeString))){
  				groupSetRebateScaleResult.inErrorMessage(918519006);
  			}
  		}
        
		//判断是否选择商品
		if(groupSetRebateScaleResult.upFlagTrue()){
			if(skuCodeString==null||StringUtils.isBlank(skuCodeString)){
				groupSetRebateScaleResult.inErrorMessage(918519001);//没有选择商品
			}
		}
		
		//返现比例个数不对
		String[] rebateScales=rebateScaleString.split(",");
		if(groupSetRebateScaleResult.upFlagTrue()){
			if(rebateScales.length!=4){
				groupSetRebateScaleResult.inErrorMessage(918519011);
			}
		}
		
		//判断数在0-100之间
		if(groupSetRebateScaleResult.upFlagTrue()){
			for(String rebateScale:rebateScales){
				if(!Pattern.matches(RegexConst.REGEX_DEFINE_NUMBER,
						rebateScale)||0>Double.valueOf(rebateScale).doubleValue()||100<Double.valueOf(rebateScale).doubleValue()){
					groupSetRebateScaleResult.inErrorMessage(918519002);//0-100的数
					break;
				}
			}
			
		}
		
		String[] skuCodes=skuCodeString.split(",");
		//判断是否存在有效的商品比例
		if(groupSetRebateScaleResult.upFlagTrue()){
			for(int i=0;i<skuCodes.length;i++){
				if(DbUp.upTable("gc_sku_rebate_scale").
						dataCount(" sku_code=:sku_code and app_code=:app_code and flag_enable=1 and status=1 and end_time>=:startTime ", new MDataMap("sku_code",skuCodes[i],"app_code",manageCode,"datenow",FormatHelper.upDateTime(),
								"startTime",startTimeString,"endTime",endTimeString))>0){
					groupSetRebateScaleResult.inErrorMessage(918519003,skuCodes[0]);//有存在的有效的商品比例
					break;
				}
			}
		}
		
		//判断sku是否重复
		if(groupSetRebateScaleResult.upFlagTrue()){
			Map<String,Object> skuMap=new HashMap<String, Object>();
			for(String skuCode:skuCodes){
				if(skuMap.containsKey(skuCode)){
					groupSetRebateScaleResult.inErrorMessage(918519008,skuCode);
					break;
				}
				else{
					skuMap.put(skuCode, "");
				}
				
			}
		}
		String rebateCodeString="";
		//插入
		if(groupSetRebateScaleResult.upFlagTrue()){
			
			String[] productCodes=productCodeString.split(",");
			
			for(int i=0;i<skuCodes.length;i++){
				MDataMap map=new MDataMap();
				map.inAllValues("product_code",productCodes[i],"sku_code",skuCodes[i],"rebate_scale",rebateScaleString,"start_time",startTimeString,
						"end_time",endTimeString,"operator",manageCode+" set by interface",
						"create_time",FormatHelper.upDateTime(),"flag_enable","1","status","1","app_code",manageCode);
				if(i==skuCodes.length-1){
					rebateCodeString=rebateCodeString+DbUp.upTable("gc_sku_rebate_scale").dataInsert(map);
				}
				else{
					rebateCodeString=rebateCodeString+DbUp.upTable("gc_sku_rebate_scale").dataInsert(map)+",";
				}
				
				
			}
			groupSetRebateScaleResult.setRebateCode(rebateCodeString);
			groupSetRebateScaleResult.setResultMessage(TopUp.upLogInfo(918519012));
		}
		
		return groupSetRebateScaleResult;
	}
	
	public RootResultWeb updateRebateScaleStatus(GroupUpdateRebateScaleInput groupUpdateRebateScaleInput,String manageCode){
		RootResultWeb rootResultWeb=new RootResultWeb();
		
		MDataMap rebateMap=DbUp.upTable("gc_sku_rebate_scale").one("uid",groupUpdateRebateScaleInput.getRebateCode());
		if(rootResultWeb.upFlagTrue()){
			if(rebateMap==null){
				rootResultWeb.inErrorMessage(918519013);
			}
		}
		
		if(rootResultWeb.upFlagTrue()){
			if(rebateMap.get("flag_enable").equals("0")){
				rootResultWeb.inErrorMessage(918519014);
			}
		}
		
		if(rootResultWeb.upFlagTrue()){
			MDataMap whereDataMap  = new MDataMap();
			whereDataMap.put("flag_enable", "0");
			whereDataMap.put("uid", groupUpdateRebateScaleInput.getRebateCode());
			whereDataMap.put("update_user",manageCode+" update by interface");
			whereDataMap.put("update_time", FormatHelper.upDateTime());
			DbUp.upTable("gc_sku_rebate_scale").dataUpdate(whereDataMap, "flag_enable,update_user,update_time", "uid");
		}
		
		if(rootResultWeb.upFlagTrue()){
			rootResultWeb.setResultMessage(TopUp.upLogInfo(918519015));
		}
		return rootResultWeb;
	}
	
	/**
	 * 订单退货
	 * @param groupReturnOrderInput
	 * @param manageCode
	 * @return
	 */
	public GroupReturnOrderResult groupReturnOrder(GroupReturnOrderInput groupReturnOrderInput,String manageCode){
		GroupReturnOrderResult groupReturnOrderResult=new GroupReturnOrderResult();
		String orderCode=groupReturnOrderInput.getOrderCode();
		List<GroupReturnOrderDetail> returnDetailList=groupReturnOrderInput.getDetailList();
		MDataMap orderMap=DbUp.upTable("gc_reckon_order_info").one("order_code",orderCode);
		//判断订单是否存在
		if(groupReturnOrderResult.upFlagTrue()){
			if(orderMap==null){
				groupReturnOrderResult.inErrorMessage(918533001,orderCode);//订单不存在
			}
		}
		//判断订单是否已签收清分了
		if(groupReturnOrderResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050001","flag_success","1")<1){
				groupReturnOrderResult.inErrorMessage(918533002,orderCode);
			}
		}
		
		//是否已逆向清分
		if(groupReturnOrderResult.upFlagTrue()){
			if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderCode,"exec_type","4497465200050002")>0){
				groupReturnOrderResult.inErrorMessage(918533003,orderCode);
			}
		}
		
		//对应退货详情是否存在并且退货数量不能超量
		if(groupReturnOrderResult.upFlagTrue()){
			//原商品详情
			List<MDataMap> detailMapList=DbUp.upTable("gc_reckon_order_detail").queryByWhere("order_code",orderCode,"flag_reckon","1");
			if(detailMapList!=null&&detailMapList.size()>0){
				Map<String, MDataMap> detailMap=new HashMap<String,MDataMap>();
				for(MDataMap oneMap:detailMapList){
					detailMap.put(oneMap.get("detail_code"),oneMap );
				}
				
				//已存在退货详情
				String gcSql="select detail_code,sum(product_number) as product_number from gc_reckon_order_return_detail where order_code=:orderCode group by detail_code";
				List<Map<String, Object>> returnedMapList=DbUp.upTable("gc_reckon_order_return_detail").dataSqlList(gcSql, new MDataMap("orderCode",orderCode));
				Map<String, Map<String, Object>> returnedMap=new HashMap<String, Map<String, Object>>();
				if(returnedMapList!=null&&returnedMapList.size()>0){
					for(Map<String, Object> oneMap:returnedMapList){
						returnedMap.put(oneMap.get("detail_code").toString(), oneMap);
					}
					
				}
				
				//判断退货详情
				if(returnDetailList.size()>0){
					for(GroupReturnOrderDetail groupReturnOrderDetail:returnDetailList){
						if(detailMap.containsKey(groupReturnOrderDetail.getDetailCode())){
							if(groupReturnOrderDetail.getProductNumber()<=0){
								groupReturnOrderResult.inErrorMessage(918533004,groupReturnOrderDetail.getDetailCode());//数量大于0
								break;
							}
							if(returnedMap.containsKey(groupReturnOrderDetail.getDetailCode())){
								if(groupReturnOrderDetail.getProductNumber()+Integer.valueOf(returnedMap.get(groupReturnOrderDetail.getDetailCode()).get("product_number").toString())>Integer.valueOf(detailMap.get(groupReturnOrderDetail.getDetailCode()).get("product_number"))){
								    groupReturnOrderResult.inErrorMessage(918533005,groupReturnOrderDetail.getDetailCode());//数量超过量
								    break;
								}
							}
							else{
								if(groupReturnOrderDetail.getProductNumber()>Integer.valueOf(detailMap.get(groupReturnOrderDetail.getDetailCode()).get("product_number"))){
									groupReturnOrderResult.inErrorMessage(918533005,groupReturnOrderDetail.getDetailCode());//数量超过量
									break;
								}
							}
						}
						else{
							groupReturnOrderResult.inErrorMessage(918533006,groupReturnOrderDetail.getDetailCode());//未找到对应商品详情
							break;
						}
					}
				}
				else{
					groupReturnOrderResult.inErrorMessage(918533007);//没有详情
				}
			}
			else{
				groupReturnOrderResult.inErrorMessage(918533008);//没有详情
			}
		}
		
		
		
	    
		//插入退货表中
	    if(groupReturnOrderResult.upFlagTrue()){
	    	String returnCode=WebHelper.upCode("RETURN");
	    	for(GroupReturnOrderDetail groupReturnOrderDetail:returnDetailList){
	    		MDataMap returnMap=new MDataMap();
		    	returnMap.put("return_code", returnCode);
		    	returnMap.put("order_code", orderCode);
		    	returnMap.put("detail_code", groupReturnOrderDetail.getDetailCode());
		    	returnMap.put("create_time", FormatHelper.upDateTime());
		    	returnMap.put("sku_code", groupReturnOrderDetail.getSkuCode());
		    	returnMap.put("product_number", String.valueOf(groupReturnOrderDetail.getProductNumber()));
		    	//returnMap.put("uq_code",orderCode.concat("_").concat(detailCode));
		    	returnMap.put("manage_code", manageCode);
		    	DbUp.upTable("gc_reckon_order_return_detail").dataInsert(returnMap);
	    	}
	    	//插入步骤执行表
	    	String uqCode=GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK+WebConst.CONST_SPLIT_DOWN+returnCode;
	    	DbUp.upTable("gc_reckon_order_step").insert("step_code",
	    			WebHelper.upCode("GCROS"), "order_code",
					orderCode, "exec_type",
					GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK, "create_time",
					FormatHelper.upDateTime(), "account_code",
					orderMap.get("account_code"), "uqcode", uqCode);
	       }
	    
		return groupReturnOrderResult;
	}
}
