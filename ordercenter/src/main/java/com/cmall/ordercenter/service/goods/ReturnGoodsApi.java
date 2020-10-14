package com.cmall.ordercenter.service.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.ReturnConst;
import com.cmall.ordercenter.model.RetuGoodDetailChild;
import com.cmall.ordercenter.model.ReturnGoods;
import com.cmall.ordercenter.model.ReturnGoodsLog;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * ClassName: ReturnGoodsApi <br/>
 * date: 2013-9-17 下午7:48:46 <br/>
 * 
 * @author hexd
 * @version
 * @since JDK 1.6
 */
public class ReturnGoodsApi extends BaseClass
{
	

	/**
	 * createRetuGoodList:(创建退货单). <br/>
	 * 
	 * @author hexd
	 * @param goods
	 * @param detail
	 * @param log
	 * @return
	 * @since JDK 1.6
	 */
	// CreateReturnGoodsResult result = new CreateReturnGoodsResult();
	public CreateReturnGoodsResult createRetuGoodList(CreateReturnGoodsInput inputParam)
	{
		String goods_no = WebHelper.upCode("RTG");
		//String seller_code = "sys";//UserFactory.INSTANCE.create().getManageCode();
		CreateReturnGoodsResult result = new CreateReturnGoodsResult();
		if(inputParam.getDetailList().size() == 0)
		{
			result.setResultCode(939301020);
			result.setResultMessage(bInfo(939301020));
			return result;
		}
		Map<String, String> mpMap = new HashMap<String, String>();
		mpMap.put("4497463500010001", "面料材质等不符合描述");
		mpMap.put("4497463500010002", "认为是假货");
		mpMap.put("4497463500010003", "尺寸不符");
		mpMap.put("4497463500010004", "做工问题");
		mpMap.put("4497463500010005", "商品功能缺失或故障");
		mpMap.put("4497463500010006", "商品破损");
		mpMap.put("4497463500020001", "不适合/不喜欢");
		mpMap.put("4497463500020002", "其他");
		
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("return_code", goods_no);
		insertDatamap.put("order_code", inputParam.getOrder_code());
		insertDatamap.put("return_reason", mpMap.get(inputParam.getReturn_reason()));
		insertDatamap.put("description", inputParam.getDescription());
		
		insertDatamap.put("create_time", DateUtil.getNowTime());
		insertDatamap.put("contacts", inputParam.getContacts());
		insertDatamap.put("status", ReturnConst.RETURN_GOODS_STATUS);
		insertDatamap.put("transport_money",Float.toString(inputParam.getTransport_money()));
		insertDatamap.put("mobile", inputParam.getMobile());
		insertDatamap.put("address", inputParam.getAddress());
		insertDatamap.put("pic_url", inputParam.getPic_url());
		
		if(StringUtils.isBlank(inputParam.getOrder_code()))
		{
			result.setResultCode(939301036);
			result.setResultMessage(bInfo(939301036));
			return result;
		}
		
		if(getPayedList(inputParam.getOrder_code()).size() == 0)
		{
			result.setResultCode(939301066);
			result.setResultMessage(bInfo(939301066));
			return result;
		}
		
		if(validateCount(inputParam) == false)
		{
			result.setResultCode(939301067);
			result.setResultMessage(bInfo(939301067));
			return result;
		}
		
		result = querySerialNo(inputParam.getOrder_code(), inputParam);
		if(1 != result.getResultCode())
			return result;
		
		
		result = queryOrderCode(inputParam.getOrder_code(),inputParam);
		if(1 != result.getResultCode())
			return result;
		
		
		try {
			insertDatamap.put("seller_code", getSellerCodeByOrderCode(inputParam.getOrder_code()).get("seller_code"));
			insertDatamap.put("buyer_code", getSellerCodeByOrderCode(inputParam.getOrder_code()).get("buyer_code"));
		} 
		catch (NullPointerException e) 
		{
			result.setResultCode(939301037);
			result.setResultMessage(bInfo(939301037));
			e.printStackTrace();
			return result;
			
		}
		
		
		List<RetuGoodDetailChild> detailList = inputParam.getDetailList();
		if (detailList.size() == 0)
		{
			result.setResultCode(939301020);
			result.setResultMessage(bInfo(939301020));
			return result;
		} else
		{
			//进行sku_code非空的判断
			for (int i = 0; i < detailList.size(); i++)
			{
				if(StringUtils.isBlank(detailList.get(i).getSerial_number()))
				{
					result.setResultCode(939301040);
					result.setResultMessage(bInfo(939301040));
					return result;
				}
			}
			//退单详情插入
			for (int i = 0; i < detailList.size(); i++)
			{
				result = createRetuGoodDetail(detailList.get(i),goods_no);
				if(1 != result.getResultCode())
				return result;
			}
			// 添加退货单
			DbUp.upTable("oc_return_goods").dataInsert(insertDatamap);
			ReturnGoodsLog goodsLog = new ReturnGoodsLog();
			goodsLog.setCreate_time(DateUtil.getNowTime());
			goodsLog.setCreate_user("sys");
			goodsLog.setInfo(inputParam.getReturn_reason());
			goodsLog.setStatus(ReturnConst.RETURN_GOODS_STATUS);
			createRetuGoodLog(goodsLog, goods_no);
			return result;
		}
	}

	/**
	 * createRetuGoodDetail:(生成退货单明细). <br/>
	 * 
	 * @author hexd
	 * @param detail
	 * @return
	 * @since JDK 1.6
	 */
	public CreateReturnGoodsResult createRetuGoodDetail(RetuGoodDetailChild detail ,String goods_no)
	{
		CreateReturnGoodsResult result = new CreateReturnGoodsResult();
		MDataMap insertDatamap = new MDataMap();
		MDataMap mp =  getOrderInfoByZid(detail.getSerial_number());
		insertDatamap.put("return_code", goods_no);
		insertDatamap.put("sku_code", mp.get("sku_code"));
		insertDatamap.put("sku_name", mp.get("sku_name"));
		insertDatamap.put("count", detail.getCount().toString());
		insertDatamap.put("current_price",mp.get("sku_price"));
		int  count = (int) Math.ceil(Float.parseFloat(mp.get("virtual_money_deduction")));
		insertDatamap.put("virtual_money_deduction", String.valueOf(count));//
		insertDatamap.put("serial_number",mp.get("zid"));
		insertDatamap.put("url",mp.get("product_picurl"));
		try
		{
			DbUp.upTable("oc_return_goods_detail").dataInsert(insertDatamap);
		} catch (Exception e)
		{
			result.setResultCode(939301023);
			result.setResultMessage(bInfo(939301023));
			return result;
		}
		return result;
	}

	/**
	 * 
	 * createRetuGoodLog:(生成退货单流水). <br/>
	 * 
	 * @author hexd
	 * @param log
	 * @return
	 * @since JDK 1.6
	 */
	private void createRetuGoodLog(ReturnGoodsLog log,String goods_no)
	{
		MDataMap insertDatamap = new MDataMap();
		String nowTime = DateUtil.getNowTime();
		insertDatamap.put("return_no", goods_no);
		insertDatamap.put("info", log.getInfo());
		insertDatamap.put("create_time", nowTime);
		insertDatamap.put("create_user", log.getCreate_user());
		insertDatamap.put("status", log.getStatus());
		try
		{
			DbUp.upTable("lc_return_goods_status").dataInsert(insertDatamap);
		} catch (Exception e)
		{
			bLogError(939301022);
		}
	}

	/**
	 * getReturnGoods:(根据买家编号查询). <br/>
	 * 
	 * @author hexd
	 * @param buyer_code
	 *            买家编号
	 * @return
	 * @since JDK 1.6
	 */
	public List<ReturnGoods> getReturnGoods(String buyer_code)
	{
		List<ReturnGoods> goodslist = new ArrayList<ReturnGoods>();
		ReturnGoods goods = new ReturnGoods();
		String temp_return_code = "";
		MDataMap mp = new MDataMap();
		mp.put("buyer_code", buyer_code);
		List<MDataMap>  prodcutData =  new ArrayList<MDataMap>();
		// 查询该用户所有的退单
		prodcutData = DbUp.upTable("oc_return_goods").queryAll("", "", "", mp);
		for(int i=0 ;i< prodcutData.size();i++)
		{
			goods =  new SerializeSupport<ReturnGoods>().serialize(prodcutData.get(i),new ReturnGoods());
			temp_return_code  = prodcutData.get(i).get("return_code");
			List<MDataMap>  prodcutData1 = getRetuDetailByReturnCode(temp_return_code);
			List<RetuGoodDetailChild> detail = new ArrayList<RetuGoodDetailChild>();
			for(int j=0;j<prodcutData1.size();j++)
			{
				detail.add(new SerializeSupport<RetuGoodDetailChild>().serialize(prodcutData1.get(j),new RetuGoodDetailChild()))  ;
			}
			goods.setDetailList(detail);
			goodslist.add(goods);
		}
		return goodslist;
	}
	
	/**
	 * getLogByUid:(根据uid退货信息信息). <br/>
	 * @author hxd
	 * @param uid
	 * @return ReturnGoods
	 * @since JDK 1.6
	 */
	public ReturnGoods getReturnGoodsCodeByUid(String uid)
	{
		ReturnGoods goods= new ReturnGoods();
		MDataMap prodcutData = DbUp.upTable("oc_return_goods").one("uid", uid);
		if (prodcutData == null)
			return null;
		goods = new SerializeSupport<ReturnGoods>().serialize(prodcutData,new ReturnGoods());
		return goods;
		
	}
	/**
	 * insertReturnGoodsLog:(插入流程扭转状态日志). <br/>
	 * @author hxd
	 * @param log
	 * @since JDK 1.6
	 */
	public void insertReturnGoodsLog(ReturnGoodsLog log)
	{
		MDataMap insertDatamap = new MDataMap();
		String nowTime = DateUtil.getNowTime();
		insertDatamap.put("return_no", log.getReturn_no());
		insertDatamap.put("info", log.getInfo());
		insertDatamap.put("create_time", nowTime);
		insertDatamap.put("create_user", log.getCreate_user());
		insertDatamap.put("status", log.getStatus());
		try
		{
			DbUp.upTable("lc_return_goods_status").dataInsert(insertDatamap);
		} catch (Exception e)
		{
			bLogError(939301021);
		}
	}
	/**
	 * getRetuNoByUserCode:(根据买家编号获取退货单号). <br/>
	 * @author hxd
	 * @param userCode
	 * @return
	 * @since JDK 1.6
	 */
	
	public List<MDataMap> getRetuDetailByReturnCode(String return_code)
	{
		MDataMap mp = new MDataMap();
		mp.put("return_code", return_code);
		return  DbUp.upTable("oc_return_goods_detail").queryAll("", "", "", mp);
	}
	/**
	 * getSellerCodeByOrderCode:(根据order_code获取seller_code). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	public MDataMap getSellerCodeByOrderCode(String orderCode)
	{
		MDataMap prodcutData = DbUp.upTable("oc_orderinfo").one("order_code", orderCode);
		return prodcutData;
	}
	
	
	/**
	 * getSellerCodeByOrderCode:(判断当前订单是否已经退货). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	public CreateReturnGoodsResult queryOrderCode(String orderCode,CreateReturnGoodsInput input)
	{
		boolean flag = true;
		CreateReturnGoodsResult result = new CreateReturnGoodsResult();
		if(input.detailList.size() == 0)
		{
			result.setResultCode(939301060);
			result.setResultMessage(bInfo(939301060));
			return result;
		}
		MDataMap mp = new MDataMap();
		List<MDataMap> glist = new ArrayList<MDataMap>();
		mp.put("order_code", orderCode);
		glist = DbUp.upTable("oc_return_goods").queryAll("", "", "", mp);
		
		
		String sql =  getInSqlForMap(glist);
		
		
		List<MDataMap> list = DbUp.upTable("oc_return_goods_detail").queryAll("", "", "return_code "+sql, new MDataMap());
		List<String> list2 = new ArrayList<String>();
		for(int i= 0;i<input.getDetailList().size();i++)
		{
			list2.add(input.getDetailList().get(i).getSerial_number());
		}
		
		for (int i = 0; i < list.size(); i++) {
			flag=list2.contains(list.get(i).get("serial_number"));
			if(flag == true)
			{
				result.setResultCode(939301054);
				result.setResultMessage(bInfo(939301054));
				return result;
			}
		}
		return result;
	}
	/**
	 * getSellerCodeByOrderCode:(校验订单数据的真实性). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	public CreateReturnGoodsResult querySerialNo(String orderCode,CreateReturnGoodsInput input)
	{
		boolean flag = true;
		Map<String, Boolean> mps = new HashMap<String, Boolean>();
		CreateReturnGoodsResult result = new CreateReturnGoodsResult();
		//一个订单号在订单详情中是可以对应多条记录的  
		MDataMap mp = new MDataMap();
		mp.put("order_code", orderCode);
		List<MDataMap> listmp = DbUp.upTable("oc_orderdetail").queryAll("", "", "", mp);//one("order_code", orderCode);  //  oc_orderdetail
		if(null ==  listmp)
		{
			result.setResultCode(939301057);
			result.setResultMessage(bInfo(939301057));
			return result;
		}
		List<RetuGoodDetailChild> list  = input.getDetailList();
		List<String> lst = new ArrayList<String>();
		for(int i = 0;i<listmp.size();i++)
			{
				lst.add(listmp.get(i).get("zid"));
			}
		
		for(int j = 0;j<list.size();j++)
			{
			    flag = lst.contains(list.get(j).getSerial_number());
				mps.put("mps"+j, flag);
			}
		
		if(mps.containsValue(false))
		{
			result.setResultCode(939301058);
			result.setResultMessage(bInfo(939301058));
			return result;
		}
		else
			return result;
		
	}
	
	
	
	/**
	 * getInSqlForMap:(拼接sql). <br/>
	 * @author hxd
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	private String getInSqlForMap(List<MDataMap> list){
		String sql = "";
		if(!list.isEmpty()){
			for(MDataMap m:list){
				String code = m.get("return_code");
				if("".equals(sql)&&code!=null&&!"".equals(code)){
					sql = " in ('"+code+"'";
				}else if(code!=null&&!"".equals(code)){
					sql+=",'"+code+"'";
				}
			}
		}
		if(!"".equals(sql)){
			sql+=")";
		}
		return sql;
	}
	/**
	 * getOrderInfoByZid:(通过zid获取订单详情). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	public MDataMap getOrderInfoByZid(String zid)
	{
		MDataMap prodcutData = DbUp.upTable("oc_orderdetail").one("zid", zid);
		return prodcutData;
	} 
	/**
	 * getPayedList:(判断订单是否已支付). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	protected List<MDataMap> getPayedList(String orderCode)
	{
		MDataMap dataMap = new MDataMap();
		dataMap.put("order_code", orderCode);
	    return DbUp.upTable("oc_order_pay").queryAll("", "", "", dataMap);
	} 
	/**
	 * validateCount:(校验输入的sku的数量). <br/>
	 * @author hxd
	 * @param inputParam
	 * @return
	 * @since JDK 1.6
	 */
	private boolean validateCount(CreateReturnGoodsInput inputParam)
	{
		boolean flag = true;
		String orderCode = inputParam.getOrder_code();
		Map<String, Integer> inMap = new HashMap<String, Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<MDataMap> mplist = getOrderDetail(orderCode);
		List<RetuGoodDetailChild> detailList =inputParam.getDetailList();
		for(int i = 0;i<mplist.size();i++)
		{
			map.put(mplist.get(i).get("zid"), Integer.parseInt(mplist.get(i).get("sku_num")));
		}
		for(int j = 0;j<detailList.size();j++)
		{
			inMap.put(detailList.get(j).getSerial_number(), detailList.get(j).getCount());
		}
		//map：查询某个订单号下对应的所有的订单详情        inMap： 为前端输入的数据
		for(String key: inMap.keySet())
		{ 
				if(null != map.get(key) && inMap.get(key) > map.get(key))
					return flag = false;
		}
		return flag;
	}
	
	
	/**
	 * getOrderDetail:(根据订单编号获取订单详情). <br/>
	 * @author hxd
	 * @param orderCode
	 * @return
	 * @since JDK 1.6
	 */
	
	public List<MDataMap> getOrderDetail(String orderCode)
	{
		MDataMap mp = new MDataMap();
		mp.put("order_code", orderCode);
		return  DbUp.upTable("oc_orderdetail").queryAll("", "", "", mp);
	}
}
