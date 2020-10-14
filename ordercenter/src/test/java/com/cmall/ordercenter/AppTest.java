package com.cmall.ordercenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopTest;
import com.srnpr.zapdata.dbdo.DbUp;

public class AppTest extends TopTest
{
    public static void main(String[] args)
    {
//    	String sWhere = "product_status='4497153900060002' and seller_code='SI2003'";
//    	List<MDataMap> productList = DbUp.upTable("pc_productinfo").queryAll("product_code,small_seller_code", "", sWhere, null);
//    	int i = 0;
//    	for (MDataMap mDataMap : productList) {
//			if (new PlusServiceSeller().isKJSeller(mDataMap.get("small_seller_code"))) {
//				i++;
//				System.out.println(i + ":  " + mDataMap.get("product_code"));
//				XmasKv.upFactory(EKvSchema.Product).del(mDataMap.get("product_code"));
//			}
//		}
    	fun2();
    	
    }
    
    public static void fun1() {
    	String sql = "select * from "
    			+ "(select * from logcenter.lc_apply_for_payment where create_time>='2016-08-14 10:34:36'  order by zid desc ) "
    			+ "as a group by a.pay_code";
    	List<Map<String, Object>> list = DbUp.upTable("lc_apply_for_payment").dataSqlList(sql, new MDataMap());
		if(null != list && list.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for(Map<String, Object> map : list) {
				String payCode = map.get("pay_code").toString();
				String flag = map.get("flag").toString();
				
				sql = "select * from ordercenter.oc_bill_apply_payment where pay_code='" + payCode + "'";
				Map<String, Object> payMap = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, new MDataMap());
				String settleCodes = payMap.get("settle_codes").toString();
				String merchant_code = payMap.get("merchant_code").toString();
				if(settleCodes.contains(",")) {
					settleCodes=settleCodes.replaceAll(",", "','");
				}
				settleCodes = "('" + settleCodes + "')";
				
				switch(flag) {
					case "4497477900010001":
						sb.append("#待审核:4497477900010001 -------- 待审核:4497476900040010\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040010' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010002":
						sb.append("#审核通过:4497477900010002 -------- 待审核:4497476900040010\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040010' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010005":
						sb.append("#拒绝:4497477900010005 -------- 待审核:4497476900040010\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040010' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010003":
						sb.append("#已确认:4497477900010003 -------- 已审核:4497476900040011\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040011' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010004":
						sb.append("#已付款:4497477900010004 -------- 已结算:4497476900040009\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040009' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010006":
						sb.append("#已收款:4497477900010006 -------- 已结算:4497476900040009\r\n");
						sb.append("update oc_bill_merchant_new_bak set flag='4497476900040009' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
				}
			}
			
			File f = new File("D://settle.sql");
			try {
				FileWriter fw = new FileWriter(f);
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
    }

    public static void fun2() {
    	String sql = "select * from ordercenter.oc_bill_apply_payment where flag='4497477900010004' or flag='4497477900010006'";
    	List<Map<String, Object>> list = DbUp.upTable("oc_bill_apply_payment").dataSqlList(sql, new MDataMap());
		if(null != list && list.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for(Map<String, Object> map : list) {
				String payCode = map.get("pay_code").toString();
				String flag = map.get("flag").toString();

				String settleCodes = map.get("settle_codes").toString();
				String merchant_code = map.get("merchant_code").toString();
				if(settleCodes.contains(",")) {
					settleCodes=settleCodes.replaceAll(",", "','");
				}
				settleCodes = "('" + settleCodes + "')";
				
				switch(flag) {
					case "4497477900010004":
						sb.append("#已付款:4497477900010004 -------- 已结算:4497476900040009\r\n");
						sb.append("update oc_bill_merchant_new set flag='4497476900040009' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
					case "4497477900010006":
						sb.append("#已收款:4497477900010006 -------- 已结算:4497476900040009\r\n");
						sb.append("update oc_bill_merchant_new set flag='4497476900040009' "
								+ "where settle_code in " + settleCodes + " and merchant_code='" + merchant_code + "';").append("\r\n");
						break;
				}
			}
			
			File f = new File("D://settle.sql");
			try {
				FileWriter fw = new FileWriter(f);
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
    }
}