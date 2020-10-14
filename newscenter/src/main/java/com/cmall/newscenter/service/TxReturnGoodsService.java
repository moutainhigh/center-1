package com.cmall.newscenter.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.dborm.txmapper.OcReturnGoodsDetailMapper;
import com.cmall.dborm.txmapper.OcReturnGoodsMapper;
import com.cmall.dborm.txmodel.OcReturnGoods;
import com.cmall.dborm.txmodel.OcReturnGoodsDetail;
import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.newscenter.model.ReturnReport;
import com.cmall.newscenter.util.MemberUtil;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.RetuGoodDetailChild;
import com.cmall.ordercenter.model.ReturnGoodsLog;
import com.cmall.ordercenter.service.goods.CreateReturnGoodsInput;
import com.cmall.ordercenter.service.goods.CreateReturnGoodsResult;
import com.cmall.ordercenter.service.goods.ReturnGoodsApi;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class TxReturnGoodsService extends ReturnGoodsApi{
	
	/**
	 * 插入退货报表
	 * @param user 
	 * 
	 * @param sAccountCode
	 * @param sManageCode
	 * @return
	 */
	public MWebResult insertReturnGoods(String fileRemoteUrl, String user, String managerCode){
		MWebResult mWebResult = new MWebResult();
		//excel记录成功条数
		int excelsuccessCount = 0;
		//失败条数
		int excelfaultCount =0;
		try {
			if(StringUtils.isBlank(fileRemoteUrl)){
				throw new Exception("下载地址不存在");
			}
			List<ReturnReport> dataLists = downloadAndAnalysisFile(fileRemoteUrl);//退货报表VO类
		
			OcReturnGoodsMapper ocReturnGoodsMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_OcReturnGoodsMapper");
			OcReturnGoodsDetailMapper ocReturnGoodsDetailMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_OcReturnGoodsDetailMapper");
			
				OcReturnGoods ocReturnGoods = new OcReturnGoods();
				OcReturnGoodsDetail ocReturnGoodsDetail = new OcReturnGoodsDetail();
				ReturnGoodsLog goodsLog = new ReturnGoodsLog();
				for(int i = 0;i<dataLists.size();i++ ){
				
					ReturnReport returnReport =new ReturnReport();
					returnReport =dataLists.get(i);
					//去掉小数点后的0
					//skuCode代表pc_productinfo中的货号sell_productcode！
					String sellProductcode = returnReport.getSkuCode();
					if(sellProductcode.indexOf(".") > 0){    
						sellProductcode = sellProductcode.replaceAll("0+?$", "");//去掉多余的0    
						sellProductcode = sellProductcode.replaceAll("[.]$", "");//如最后一位是.则去掉    
			        }    
					
					
					
					String buyerCode = "";
					//通过ordercode查询oc_orderinfo中的buyerCode
					String sql = "select buyer_code from oc_orderinfo where order_code ='"+returnReport.getOrderCode()+"'";
					Map<String, Object> buyerMap = DbUp.upTable("oc_orderinfo").dataSqlOne(sql, new MDataMap());
					if (null !=buyerMap) {
						buyerCode = (String)buyerMap.get("buyer_code");
					}
					
					String skuCode = "";
					//通过sell_productcode查询oc_orderdetail中的skucode
				    sql = "select sku_code from oc_orderdetail where product_code in(select product_code from productcenter.pc_productinfo where sell_productcode ='"+sellProductcode+"') and order_code = '"+returnReport.getOrderCode()+"'";
					Map<String, Object> skuMap = DbUp.upTable("oc_orderdetail").dataSqlOne(sql, new MDataMap());
					if (null !=skuMap) {
						skuCode = (String) skuMap.get("sku_code");
					}
					String goods_no = WebHelper.upCode("RTG");
					//通过order_code查询出商品编号信息
					MDataMap mp = new MDataMap();
					mp.put("order_code", returnReport.getOrderCode());
					List<MDataMap> listmp = DbUp.upTable("oc_orderdetail").queryAll("", "", "", mp);
					List<String> lst = new ArrayList<String>();
					if (null != listmp ) {
						for(int j = 0;j<listmp.size();j++)
						{
							lst.add(listmp.get(j).get("sku_code"));
						}
					}
					
					if (lst.contains(skuCode)==false) {
						//失败条数记录
						excelfaultCount++;
						MDataMap insertDatamap = new MDataMap();
						String nowTime = DateUtil.getNowTime();
						insertDatamap.put("order_code",returnReport.getOrderCode());
						insertDatamap.put("return_reason", "商品在订单中不存在");
						insertDatamap.put("create_time", nowTime);
						insertDatamap.put("create_user", user);
						try
						{
							DbUp.upTable("lc_not_return_goods").dataInsert(insertDatamap);
						} catch (Exception e)
						{
							bLogError(939301122);
						}
						continue;
					}
					else{
						int orderCount = 0;
						//查询订单详情中单个商品总数
						 sql = "select sku_num from oc_orderdetail where product_code =(select product_code from productcenter.pc_productinfo where sell_productcode ='"+sellProductcode+"') and order_code = '"+returnReport.getOrderCode()+"'";
						Map<String, Object> countMap = DbUp.upTable("oc_orderdetail").dataSqlOne(sql, new MDataMap());
						if (null !=countMap) {
							orderCount =  (Integer) countMap.get("sku_num");
						}
						//查询退货详情单中的单个商品总数
						sql = "select count from oc_return_goods_detail where return_code in(select return_code from oc_return_goods where order_code = '"+returnReport.getOrderCode()+"')";
						List<Map<String, Object>> countList =DbUp.upTable("oc_return_goods_detail").dataSqlList(sql , new MDataMap());
						int returnCount = 0;
						if (null!=countList) {
						for (int j = 0; j < countList.size(); j++) {
						returnCount +=(Integer)countList.get(j).get("count");
						}
					}
						int inCount = new Integer((int)Double.parseDouble(returnReport.getCount()));
						if (orderCount -returnCount -inCount<0) {
							//失败条数记录
							excelfaultCount++;
							MDataMap insertDatamap = new MDataMap();
							String nowTime = DateUtil.getNowTime();
							insertDatamap.put("order_code",returnReport.getOrderCode());
							insertDatamap.put("return_reason", "退货数量超出订单商品数量");
							insertDatamap.put("create_time", nowTime);
							insertDatamap.put("create_user", user);
							try
							{
								DbUp.upTable("lc_not_return_goods").dataInsert(insertDatamap);
							} catch (Exception e)
							{
								bLogError(939301122);
							}
							continue;
						}
						
					/*插入一条审核中的退货订单记录*/
					ocReturnGoods.setUid(WebHelper.upUuid());
					ocReturnGoodsDetail.setUid(WebHelper.upUuid());
					ocReturnGoods.setSellerCode(managerCode);
					ocReturnGoods.setReturnCode(goods_no);
					if(returnReport.getThirdOrderCode()!=null){
						ocReturnGoods.setThirdOrderCode(returnReport.getThirdOrderCode());
					}
					
					
					if(returnReport.getOrderCode()!=null){
						ocReturnGoods.setOrderCode(returnReport.getOrderCode());
					}
					ocReturnGoods.setBuyerCode(buyerCode);
					ocReturnGoods.setMobile(new MemberUtil().getOrderMoblie(returnReport.getOrderCode()));
					ocReturnGoods.setContacts(new MemberUtil().getOrderPerson(returnReport.getOrderCode()));
					
					ocReturnGoodsDetail.setReturnCode(goods_no);
					ocReturnGoodsDetail.setSkuName(returnReport.getSkuName());
					ocReturnGoodsDetail.setSkuCode(skuCode);
					ocReturnGoodsDetail.setCount(new Integer((int)Double.parseDouble(returnReport.getCount())));
					ocReturnGoodsDetail.setCurrentPrice(new BigDecimal(returnReport.getCurrentPrice()));
					ocReturnGoodsDetail.setReturnPrice(new BigDecimal(returnReport.getReturnPrice()));
					ocReturnGoods.setStatus("4497153900050001");//审批中
					ocReturnGoods.setCreateTime(returnReport.getCreateTime());
					
					/*创建退货日志*/
					
					//审核中日志
				
					goodsLog.setCreate_time(DateUtil.getNowTime());
					goodsLog.setCreate_user(user);
					goodsLog.setInfo("");
					goodsLog.setStatus("4497153900050003");
					createRetuGoodLog(goodsLog, goods_no);
						
					
				
					
					/*创建退货日志*/
					
					//审核通过日志
					goodsLog.setCreate_time(DateUtil.getNowTime());
					goodsLog.setCreate_user(user);
					goodsLog.setInfo("");
					goodsLog.setStatus("4497153900050001");
					createRetuGoodLog(goodsLog, goods_no);
						
					
					ocReturnGoodsMapper.insertSelective(ocReturnGoods);
					ocReturnGoodsDetailMapper.insertSelective(ocReturnGoodsDetail);
					//成功条数记录
					excelsuccessCount++;
				
					
					String sSql="select account_code from gc_reckon_order_info where order_code = '"+returnReport.getOrderCode()+"'";
					
					Map<String, Object> map = DbUp.upTable("gc_reckon_order_info").dataSqlOne(sSql, new MDataMap());
					
					if(map!=null){
						
						/*取消订单时取消取消预返利*/
						GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
						
						groupReckonSupport.checkCreateStep(returnReport.getOrderCode(), GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
						
						ReckonStep reckonStep = new ReckonStep();
						reckonStep.setAccountCode(map.get("account_code").toString());
						reckonStep.setExecType(GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
						reckonStep.setOrderCode(returnReport.getOrderCode());

						mWebResult.inOtherResult(new GroupReckonSupport()
								.createReckonStep(reckonStep));
					
						
					}	
				 }
				
		      }
				mWebResult.setResultMessage("导入成功"+excelsuccessCount+"条:导入失败"+excelfaultCount+"条");
		    
		}catch (Exception e) {
			e.printStackTrace();
			mWebResult.setResultCode(-1);
			mWebResult.setResultMessage("excel文件解析失败");
		}
		return mWebResult;
	}
	
	
	/*生成退货单流水*/
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

	private List<ReturnReport> downloadAndAnalysisFile(String fileRemoteUrl) throws Exception{
		
		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? "" : fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<ReturnReport> readExcelUtil = new ReadExcelUtil<ReturnReport>();
		
		return readExcelUtil.readExcel(false, null, content, new String[]{"problemCode","thirdOrderCode","orderCode","sellerCode","skuCode","skuName","standard","count","currentPrice","returnPrice","handleSchedule","status","refundState","createTime"},
				new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class}, ReturnReport.class, extension);
	}
}
