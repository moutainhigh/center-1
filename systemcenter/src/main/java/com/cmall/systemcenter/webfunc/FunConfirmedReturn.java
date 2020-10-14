package com.cmall.systemcenter.webfunc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.ali.util.AlipaySubmit;
import com.cmall.systemcenter.bill.KqProperties;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加
 * 
 * @author hxd
 * 
 */
public class FunConfirmedReturn extends RootFunc {
	//锁表 添加日志
	private void lockAndlog(String idArray) {
		MDataMap mp = new MDataMap();
		MDataMap mp1 = new MDataMap();
		if(StringUtils.isNotBlank(idArray))
		{
			for(int i=0;i<idArray.split(",").length;i++)
			{
				//锁表
				WebHelper.addLock(idArray.split(",")[i], 60);
				//添加日志
				mp.put("return_money_no", idArray.split(",")[i]);
				mp.put("info", "财务退款确认");
				mp.put("create_time", getNowTime());
				mp.put("create_user", UserFactory.INSTANCE.create().getLoginName());
				DbUp.upTable("lc_return_money_status").dataInsert(mp);
				//更新状态
				mp1.put("return_conf", "是"); // 
				mp1.put("return_money_code", idArray.split(",")[i]);
				DbUp.upTable("oc_return_money").dataUpdate(mp1, "return_conf", "return_money_code");
			}
		}
	}
	
	
	
	//生成提交数据
	private String aliData(MDataMap mDataMap, String notify_url,
			Timestamp nousedate, SimpleDateFormat df1, String idArray) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = df.format(new java.util.Date());
		Map<String, String> sParaTemp = new HashMap<String, String>();
		String detail_data = "";
		if(StringUtils.isNotEmpty(mDataMap.get("sData")))
		{
			//idArray = mDataMap.get("sData");
			String[] a = idArray.split(",");
			List<String> stooges = Arrays.asList(a);
			String sql = "";
			for(String m : stooges)
			{
				if(StringUtils.isBlank(sql))
				{
					sql =sql +"return_money_code ='" +m+"'";
				}
				else if( !StringUtils.isBlank(sql))
				{
					sql = sql +" or return_money_code ='"+m+"'";
				}
			}
			String ssql = "select * from oc_return_money where " +sql;
			List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_return_money_detail").dataSqlList(ssql, new MDataMap());
			
			sParaTemp.put("service", bConfig("systemcenter.service"));
	        sParaTemp.put("partner", bConfig("systemcenter.partner"));
	        sParaTemp.put("_input_charset", bConfig("systemcenter.input_charset"));
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("seller_email", bConfig("systemcenter.seller_email"));
			sParaTemp.put("refund_date", nowTime);
			sParaTemp.put("batch_no", df1.format(new Date()).toString() + nousedate.toString().replace(" ", "")
					.replace("-", "")
					.replace(":", "")
					.replace(".", ""));
			
			sParaTemp.put("batch_num", String.valueOf(a.length));
			for(int i=0;i<dataSqlList.size();i++)
			{
				if(StringUtils.isBlank(detail_data)) 
				{
					detail_data = detail_data+dataSqlList.get(i).get("batch_no")+"^"+dataSqlList.get(i).get("online_money") +"^" +"退货产生的退款";
				}
				else
				{
					detail_data =detail_data +"#"+dataSqlList.get(i).get("batch_no")+"^"+dataSqlList.get(i).get("online_money") +"^" +"退货产生的退款";
				}
				
			}
			sParaTemp.put("detail_data", detail_data);
		}
		String sHtmlText = new AlipaySubmit().buildRequest(sParaTemp,"post","确认");
		return sHtmlText;
	}
	
	
	// 通过moneyCode获取uid
	private List<Map<String, Object>> getUidList(String code)
	{
		String sSql =  "select * from oc_return_money where ";
		String tmf = getSql(code, "return_money_code");
		return DbUp.upTable("oc_return_money").dataSqlList(sSql+tmf, new MDataMap());
	}
	//拼接sql
	private String getSql(String str,String codeName)
	{
		String sql = "";
		for(int i=0;i<str.split(",").length;i++)
		{
			if(StringUtils.isBlank(sql))
			{
				sql = sql +        codeName    +" ='" +str.split(",")[i]+"'";
			}
			else
			{
				sql =  sql +" or " +codeName   +" ='" +str.split(",")[i]+"'";
			}
		}
		return sql;
	}
	
	
	private String getNowTime()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = df.format(new java.util.Date());
		return nowTime;
	}
	
	//去除数组中重复的记录  
	private  String[] array_unique(String[] a) {  
	    // array_unique  
	    List<String> list = new LinkedList<String>();  
	    for(int i = 0; i < a.length; i++) {  
	        if(!list.contains(a[i])) {  
	            list.add(a[i]);  
	        }  
	    }  
	    return (String[])list.toArray(new String[list.size()]);  
	} 
	
	

	
	
	private String getAmountByCode(String moneyCode)
	{
		MDataMap map = DbUp.upTable("oc_return_money").one("return_money_code",moneyCode);
		
		return map.get("online_money");
	}



	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		//HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();   
		MWebResult mResult = new MWebResult();
		mResult.setResultObject("returnMsg('"+ 969912006+ "')");
		mResult.setResultType("116018010");
		String notify_url = "/cmanage/notify_url.jsp";
		Date d = new Date();
		Timestamp nousedate = new Timestamp(d.getTime());
		SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
		String idArray = mDataMap.get("sData");
		String type = mDataMap.get("flag");
		if(StringUtils.isNotBlank(type))
		{
			if(1 !=array_unique(type.split(",")).length)
			{
				mResult.setResultCode(969912005);
				mResult.setResultMessage(bInfo(969912005));
				return mResult;
			}
		}
		if(StringUtils.isBlank(idArray))
		{
			mResult.setResultCode(969912004);
			mResult.setResultMessage(bInfo(969912004));
			return mResult;
		}
		// idArray  returnMoneyCode
		List<Map<String, Object>> list = getUidList(idArray);
		boolean flag =false;
		for(int i = 0; i<list.size();i++)
		{
			if("是".equals(list.get(i).get("return_conf")))
				flag = true;
			continue;
		}
		if(flag)
		{
			mResult.setResultCode(969912004);
			mResult.setResultObject("returnMsg('"+ 969912004+ "')");
			return mResult;
		}
		//#################返还积分开始
		for(int i = 0; i<list.size();i++)
		{
			String orderCode = list.get(i).get("order_code").toString();
			MDataMap map = DbUp.upTable("oc_orderinfo").one("order_code",orderCode);	
			String buyerCode = map.get("buyer_code");
			String scoreCount = list.get(i).get("virtual_money_deduction").toString();
			returnScore(buyerCode, scoreCount);
		}
		//#################返还积分结束
		
		if("快钱支付".equals(array_unique(type.split(","))[0]))    //array_unique
		{
			MDataMap map = DbUp.upTable("oc_return_money_detail").one("return_money_code",idArray.split(",")[0]);
			KqProperties pro = new KqProperties();
			//商户编号，线上的话改成你们自己的商户编号的，发到商户的注册快钱账户邮箱的
			pro.setMerchant_id(map.get("merchant_id"));
			//原商户订单号
			pro.setOrderid(map.get("order_code"));
			//退款金额，整数或小数，小数位为2位   以人民币元为单位
			pro.setAmount(getAmountByCode(idArray.split(",")[0]));  
			StringBuffer htm = new StringBuffer();
			htm.append("merchant_id="+pro.getMerchant_id());
			htm.append("&returnMoney_code="+idArray.split(",")[0]);
			htm.append("&amount="+pro.getAmount());
			mResult.setResultObject("returnKq('"+ htm+ "')");
			//lockAndlog(idArray);
		}
		if("支付宝支付".equals(array_unique(type.split(","))[0]))    //array_unique
		{
			String   sHtmlText = aliData(mDataMap, notify_url, nousedate, df1,idArray);
			mResult.setResultObject("returnMsg('"+ sHtmlText+ "')");
			//request.setAttribute("sHtmlText", sHtmlText);	
			lockAndlog(idArray);
		}
		return mResult;
	
	}
	/**
	 * 
	 * @param buyerCode 买家编号
	 * @param scoreCount 积分数量
	 */
	
	private void returnScore(String buyerCode,String scoreCount)
	{
		MDataMap dataMap = DbUp.upTable("jifen_info").one("object",buyerCode);
		if(null != dataMap)
		{
			float score = Float.parseFloat(dataMap.get("value"));
			score = score +Float.parseFloat(scoreCount);
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("value", String.valueOf(score));
			mDataMap.put("object", buyerCode);
			DbUp.upTable("jifen_info").dataUpdate(mDataMap, "value", "object");
			MDataMap dataMap2 = new MDataMap();
			dataMap2.put("to_id", buyerCode);
			//dataMap2.put("to_name", DbUp.upTable("").one("object",buyerCode));
			dataMap2.put("from_id", UserFactory.INSTANCE.create().getManageCode());
			dataMap2.put("from_name", UserFactory.INSTANCE.create().getLoginName());
			//dataMap2.put("to_value", "");
			//dataMap2.put("from_value", "");
			dataMap2.put("value", scoreCount);
			dataMap2.put("to_balance", String.valueOf(score));
			dataMap2.put("action", "0");
			dataMap2.put("trade_code", new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date())  +String.valueOf((int)(Math.random()*900)+100));
			dataMap2.put("from_type", "0");                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
			dataMap2.put("to_type", "1");
			dataMap2.put("payment", "2");
			dataMap2.put("status", "2");
			dataMap2.put("status_name", "已发放");
			dataMap2.put("op_time", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date()));
			dataMap2.put("from_balance", "0");
			DbUp.upTable("jifen_log").dataInsert(dataMap2);
		}
	}
}
