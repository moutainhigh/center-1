package com.cmall.ordercenter.tallyorder.exceldata;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.ExcelSupport;

/**
 * 扣费模板导入
 * @author zmm
 *
 */
public class FeeDeductionImport extends RootFunc{
	
	/**
	 * 读取上传的excel流
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
	     MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
	    String uid= mSubMap.get("uid");
	    Map<String, String> skumap = DbUp.upTable("oc_bill_finance_amount").oneWhere("uid, settle_code, settle_type, settle_status,"
	    		+ "start_time, end_time, tuistart, tuiend", "","uid=:uid", "uid", uid);
	    //结算单唯一编号
	    String settleCode = skumap.get("settle_code").toString();
	    //结算类型
	    String settleType = skumap.get("settle_type").toString();
	    //销售结算开始及结束时间
	    String start_time = skumap.get("start_time").toString();
		String end_time = skumap.get("end_time").toString();
		//退货开始及结束时间
	    String retStartTime = skumap.get("tuistart").toString();
		String retEndTime = skumap.get("tuiend").toString();
		
		//帐务结算单状态
		String settle_status = skumap.get("settle_status").toString();

	    MDataMap mInputMap = upFieldMap(mDataMap);
	    String fileRemoteUrl = mInputMap.get("small_seller_code");
	    
	    if(StringUtils.isBlank(fileRemoteUrl)) {
	    	mResult.setResultCode(0);
			mResult.setResultMessage("请上传文件");
			return mResult;
	    }
	    
		if (mResult.upFlagTrue()) {
			try {
				//结算单未确认才可导入扣费模板1.未发布 0已发布
				if(settle_status.equals("1")) {
				   mResult = readExcel(fileRemoteUrl,start_time,end_time, retStartTime, retEndTime, settleCode, settleType, uid);
				}else{
					mResult.setResultMessage("请在待确认的状态下导入模板!");
				}
			} catch (Exception e) {
				mResult.setResultMessage("更新失败");
				e.printStackTrace();
			}
		}
		return mResult;
	}
	
	/**
	 * 读取Excel数据写入数据库
	 * 
	 * @param file
	 */
	public MWebResult readExcel(String url,String start_time,String end_time,String retStartTime, 
			String retEndTime, String settleCode, String settleType, String olduid) 
	{
		MWebResult mWebResult = new MWebResult();
		MDataMap amount = DbUp.upTable("oc_bill_finance_amount").one("uid", olduid);
		try {
			List<MDataMap> dataList = new ExcelSupport().upExcelFromUrl(url);
			for(MDataMap dataMap : dataList) {
				//通路编码
				String passage = StringUtils.trimToEmpty(dataMap.get("通路"));
				//商品编码
				String product_code = StringUtils.trimToEmpty(dataMap.get("商品编码"));
				//SKU编码
				String sku_code = StringUtils.trimToEmpty(dataMap.get("SKU编码"));
				//商户编码
				String small_seller_code = StringUtils.trimToEmpty(dataMap.get("商户编码"));	
				//促销费用
				String sale_price = dataMap.get("促销费用");	
				//邮费
				String postage = dataMap.get("邮费");	
				//平台管理费
				String manage_money = dataMap.get("平台管理费");
				//其他
				String others = dataMap.get("其他");
				//附加扣费合计
				String add_pay_money = dataMap.get("附加扣费合计");
				//其它扣费原因
				String other_pay_reason = StringUtils.trimToEmpty(dataMap.get("其它扣费原因"));
				
				/**
				 * 平台结算
				 */
				//代收单价（售价）
				String ds_price = dataMap.get("代收单价");	
				//应付代收单价（成本价）
				String yfds_price = dataMap.get("应付代收单价");
				
				/**
				 * 常规结算
				 */
				//进价
				String cost = dataMap.get("进价");	
				
				if("4497477900040001".equals(amount.get("settle_type"))) {
					// 常规结算需要检查进价
					if(StringUtils.isBlank(cost)) {
						mWebResult.setResultCode(0);
						mWebResult.setResultMessage("进价不能为空: "+product_code);
						return mWebResult;
					}
				} else if("4497477900040004".equals(amount.get("settle_type"))) {
					// 平台结算需要检查代收
					if(StringUtils.isBlank(ds_price) || StringUtils.isBlank(yfds_price)) {
						mWebResult.setResultCode(0);
						mWebResult.setResultMessage("代收单价/应付代收单价 不能为空: "+product_code);
						return mWebResult;
					}
				} else {
					mWebResult.setResultCode(0);
					mWebResult.setResultMessage("不支持的结算类型");
					return mWebResult;
				}
				
				try {
					BigDecimal bg0 = new BigDecimal(sale_price);
					bg0 = bg0.setScale(2, BigDecimal.ROUND_HALF_UP);
					 
					BigDecimal bg1 = new BigDecimal(postage);
					bg1 = bg1.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					BigDecimal bg2 = new BigDecimal(manage_money);
					bg2 = bg2.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					BigDecimal bg3 = new BigDecimal(others);
					bg3 = bg3.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					BigDecimal bg = new BigDecimal(add_pay_money);
					bg = bg.setScale(2, BigDecimal.ROUND_HALF_UP);
				
					if(bg0.add(bg1).add(bg2).add(bg3).compareTo(bg) !=0) {
						mWebResult.setResultCode(939303302);
						mWebResult.setResultMessage(bInfo(939303302));
						return mWebResult;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				
				
				MDataMap map1 = new MDataMap();
				map1.put("passage",passage);
				map1.put("product_code",product_code);
				map1.put("sku_code", sku_code);
				map1.put("small_seller_code", small_seller_code);
				
				UUID uuid = UUID.randomUUID();
				String uid = uuid.toString().replace("-", "");
				DbUp.upTable("oc_bill_free_import").insert("uid", uid, "settle_code", settleCode, "passage",passage,"product_code",
						product_code, "sku_code",sku_code, "small_seller_code",
						small_seller_code,"sale_price", sale_price.toString(), "postage",
						postage.toString(), "manage_money",	manage_money.toString(), "others", others.toString(),
						"add_pay_money", add_pay_money.toString(), "other_pay_reason", other_pay_reason, 
						"start_time", start_time, "end_time", end_time, "tuistart", retStartTime, "tuiend", retEndTime);
				
				
				//原表没有settlecode,用max(zid)方式只能对某商户某sku最新的一条结算信息扣费
				//如果对上一个帐期财务结算导入扣费模板,则扣除的是当前帐期该商户sku的结算数据,肯定是
				//不合理的bug.加入settleCode后,每个帐期的settleCode是不同的,解决了这个问题
				
//				Map<String,Object> map=DbUp.upTable("oc_bill_final_export").dataSqlOne("select max(zid) as zid from oc_bill_final_export where"
//						+ " passage=:passage and sku_code=:sku_code and product_code=:product_code and small_seller_code=:small_seller_code", map1);
//				String zid=map.get("zid")==null?"":map.get("zid").toString();
				
				MDataMap detailMap = null;
				if("4497477900040001".equals(amount.get("settle_type"))) {
					// 常规结算需要检查进价
					detailMap = DbUp.upTable("oc_bill_final_export").one("settle_code", settleCode, "sku_code", sku_code, "cost_price", cost);
				} else if("4497477900040004".equals(amount.get("settle_type"))) {
					// 平台结算需要检查代收
					detailMap = DbUp.upTable("oc_bill_final_export").one("settle_code", settleCode, "sku_code", sku_code, "cost_price", yfds_price, "sell_price", ds_price);
				}
				
				if(detailMap == null) {
					mWebResult.setResultCode(0);
					mWebResult.setResultMessage("未查询到要扣费的商品: "+product_code);
					return mWebResult;
				}
				
				DbUp.upTable("oc_bill_final_export").dataUpdate(
						new MDataMap(
								"sale_money",String.valueOf(sale_price),
								"postage",String.valueOf(postage),
								"manage_money",String.valueOf(manage_money),
								"others",String.valueOf(others),
								"add_amount",String.valueOf(add_pay_money), 
								"other_pay_reason",other_pay_reason,
								"zid", detailMap.get("zid")),
						"sale_money,postage,manage_money,others,add_amount,other_pay_reason",
						"zid");
			}

			UpdateAmount(olduid, settleCode, settleType);
			mWebResult.setResultMessage("成功导入扣费模板！");
		} catch (Exception e) {
			mWebResult.setResultMessage("导入扣费模板失败！");
			e.printStackTrace();
		}
		return mWebResult;
	}
	
	/**
	 * 更新财务结算列表数据
	 * @param uid
	 * @param start_time
	 * @param end_time
	 */
	private static void UpdateAmount(String uid, String settleCode, String settleType) {
		switch(settleType) {
			case "4497477900040001":
				updateStdAmount(uid, settleCode);
				break;
			case "4497477900040002":
				updateCrossBorderBSAmount(uid, settleCode);
				break;
			case "4497477900040003":
				updateCrossBorderDirectMailAmount(uid, settleCode);
				break;
			case "4497477900040004":
				updatePlatformAmount(uid, settleCode);
				break;
		}
	}
	
	/**
	 * 更新常规结算财务汇总数据
	 */
	private static void updateStdAmount(String uid, String settleCode) {
		MDataMap map = new MDataMap();
//		map.put("start_time", start_time);
//		map.put("end_time", end_time);
		map.put("settle_code", settleCode);
		//取出时间段内的扣费总金额
//		String sql="select sum(add_amount) as add_pay_money from oc_bill_final_export where start_time=:start_time and end_time=:end_time";
		String sql="select sum(add_amount) as add_pay_money from oc_bill_final_export where settle_code=:settle_code";
		Map<String, Object> mapinfo=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql, map);
		String add_pay_money=mapinfo.get("add_pay_money").toString();
		//根据结算单uid查出本期结算的总金额
		String fiancesql="select settle_amount from oc_bill_finance_amount where uid="+"'"+uid+"'";
		Map<String, Object> mapfinance=DbUp.upTable("oc_bill_finance_amount").dataSqlOne(fiancesql, null);
		String settle_amount=mapfinance.get("settle_amount").toString();
		//根据总金额和扣费总金额查出应结付款金额
		Double settle_pay_moeny=Double.valueOf(settle_amount)-Double.valueOf(add_pay_money);
		//根据本结算id更新扣费数据与结算总金额
		DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"related_charges",add_pay_money,
				"settle_pay_moeny",String.valueOf(settle_pay_moeny)),"related_charges,settle_pay_moeny","uid");
	}
	
	/**
	 * 更新跨境商户(保税)结算财务汇总数据
	 */
	private static void updateCrossBorderBSAmount(String uid, String settleCode) {
		MDataMap map = new MDataMap();
		map.put("settle_code", settleCode);
		//取出时间段内的扣费总金额
		String sql="select sum(add_amount) as add_pay_money,sum(service_fee) as service_fee from oc_bill_final_export where settle_code=:settle_code";
		Map<String, Object> mapinfo=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql, map);
		String add_pay_money = mapinfo.get("add_pay_money").toString();
		String service_fee = mapinfo.get("service_fee").toString();
		//根据结算单uid查出本期结算的总金额
		String fiancesql="select settle_amount from oc_bill_finance_amount where uid="+"'"+uid+"'";
		Map<String, Object> mapfinance=DbUp.upTable("oc_bill_finance_amount").dataSqlOne(fiancesql, null);
		String settle_amount=mapfinance.get("settle_amount").toString();
		//根据总金额和扣费总金额查出应结付款金额
		Double settle_pay_moeny=Double.valueOf(settle_amount)-Double.valueOf(add_pay_money)-Double.valueOf(service_fee);
		//根据本结算id更新扣费数据与结算总金额
		DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"related_charges",add_pay_money,
				"settle_pay_moeny",String.valueOf(settle_pay_moeny)),"related_charges,settle_pay_moeny","uid");
	}	
	
	/**
	 * 更新跨境直邮结算财务汇总数据
	 */
	private static void updateCrossBorderDirectMailAmount(String uid, String settleCode) {
		MDataMap map = new MDataMap();
		map.put("settle_code", settleCode);
		//取出时间段内的扣费总金额
		String sql="select sum(add_amount) as add_pay_money,sum(service_fee) as service_fee from oc_bill_final_export where settle_code=:settle_code";
		Map<String, Object> mapinfo=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql, map);
		String add_pay_money = mapinfo.get("add_pay_money").toString();
		String service_fee = mapinfo.get("service_fee").toString();
		//根据结算单uid查出本期结算的总金额
		String fiancesql="select settle_amount from oc_bill_finance_amount where uid="+"'"+uid+"'";
		Map<String, Object> mapfinance=DbUp.upTable("oc_bill_finance_amount").dataSqlOne(fiancesql, null);
		String settle_amount=mapfinance.get("settle_amount").toString();
		//根据总金额和扣费总金额查出应结付款金额
		Double settle_pay_moeny=Double.valueOf(settle_amount)-Double.valueOf(add_pay_money)-Double.valueOf(service_fee);
		//根据本结算id更新扣费数据与结算总金额
		DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"related_charges",add_pay_money,
				"settle_pay_moeny",String.valueOf(settle_pay_moeny)),"related_charges,settle_pay_moeny","uid");
	}	
	
	
	/**
	 * 更新平台入驻结算财务汇总数据
	 */
	private static void updatePlatformAmount(String uid, String settleCode) {
		MDataMap map = new MDataMap();
		map.put("settle_code", settleCode);
		//取出时间段内的扣费总金额
		String sql="select sum(add_amount) as add_pay_money,sum(service_fee) as service_fee from oc_bill_final_export where settle_code=:settle_code";
		Map<String, Object> mapinfo=DbUp.upTable("oc_bill_final_export").dataSqlOne(sql, map);
		String add_pay_money = mapinfo.get("add_pay_money").toString();
		String service_fee = mapinfo.get("service_fee").toString();
		//根据结算单uid查出本期结算的总金额
		String fiancesql="select settle_amount from oc_bill_finance_amount where uid="+"'"+uid+"'";
		Map<String, Object> mapfinance=DbUp.upTable("oc_bill_finance_amount").dataSqlOne(fiancesql, null);
		String settle_amount=mapfinance.get("settle_amount").toString();
		//根据总金额和扣费总金额查出应结付款金额
		Double settle_pay_moeny=Double.valueOf(settle_amount)-Double.valueOf(add_pay_money)-Double.valueOf(service_fee);
		//根据本结算id更新扣费数据与结算总金额
		DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"related_charges",add_pay_money,
				"settle_pay_moeny",String.valueOf(settle_pay_moeny)),"related_charges,settle_pay_moeny","uid");
	}
	
	
	
	//only for test
	public static void main(String[] args) {
		System.out.println(String.valueOf("1.5"));
//		// TODO 自动生成方法存根
//		File file = new File("f:/deduteTemplate.xls");
//		if (!file.exists()) {
//			System.out.println("文件不存在");
//		}
//		FileInputStream fs;
//		try {
//			fs = new FileInputStream(file);
//			//FeeDeductionImport.readExcel(fs,"","","");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
