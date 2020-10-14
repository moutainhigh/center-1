package com.cmall.newscenter.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ArrayAnnotationValue;

import com.cmall.dborm.txmapper.OcExchangeGoodsDetailMapper;
import com.cmall.dborm.txmapper.OcExchangeGoodsMapper;
import com.cmall.dborm.txmodel.OcExchangeGoods;
import com.cmall.dborm.txmodel.OcExchangeGoodsDetail;
import com.cmall.groupcenter.accountmarketing.util.ReadExcelUtil;
import com.cmall.newscenter.model.ExchangeReport;
import com.cmall.newscenter.util.MemberUtil;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.model.ExchangegoodsStatusLogModel;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class TxExchangeGoodsService extends BaseClass{
	/**
	 * 插入退货报表
	 * @param user 
	 * 
	 * @param sAccountCode
	 * @param sManageCode
	 * @return
	 */
	public MWebResult insertExchangeGoods(String fileRemoteUrl, String user,String manageCode){
		MWebResult mWebResult = new MWebResult();
				//excel记录成功条数
				int excelsuccessCount = 0;
				//失败条数
				int excelfaultCount =0;
		try {
			if(StringUtils.isBlank(fileRemoteUrl)){
				throw new Exception("下载地址不存在");
			}
			List<ExchangeReport> dataLists = downloadAndAnalysisFile(fileRemoteUrl);//退货报表VO类
		
			OcExchangeGoodsMapper ocExchangeGoodsMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_OcExchangeGoodsMapper");
			OcExchangeGoodsDetailMapper ocExchangeGoodsDetailMapper = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_OcExchangeGoodsDetailMapper");
			
			
				OcExchangeGoods ocExchangeGoods = new OcExchangeGoods();
				OcExchangeGoodsDetail ocExchangeGoodsDetail = new OcExchangeGoodsDetail();
				ExchangegoodsStatusLogModel logModel = new ExchangegoodsStatusLogModel();
				for(int i = 0;i<dataLists.size();i++ ){
					
					ExchangeReport exchangeReport =new ExchangeReport();
					
					exchangeReport =dataLists.get(i);
					//去掉小数点后的0
					String sellProductcode = exchangeReport.getSkuCode();
					if(sellProductcode.indexOf(".") > 0){    
						sellProductcode = sellProductcode.replaceAll("0+?$", "");//去掉多余的0    
						sellProductcode = sellProductcode.replaceAll("[.]$", "");//如最后一位是.则去掉    
			        }    
					
					String buyerCode = "";
					//通过ordercode查询oc_orderinfo中的buyerCode
					String sql = "select buyer_code from oc_orderinfo where order_code ='"+exchangeReport.getOrderCode()+"'";
					Map<String, Object> buyerMap = DbUp.upTable("oc_orderinfo").dataSqlOne(sql, new MDataMap());
					if (null !=buyerMap) {
						buyerCode = (String)buyerMap.get("buyer_code");
					}
					
					
					String skuCode = "";
					//通过sell_productcode查询oc_orderdetail中的skucode
					sql = "select sku_code from oc_orderdetail where product_code in(select product_code from productcenter.pc_productinfo where sell_productcode ='"+sellProductcode+"')and order_code = '"+exchangeReport.getOrderCode()+"'";
					Map<String, Object> skuMap = DbUp.upTable("oc_orderdetail").dataSqlOne(sql, new MDataMap());
					if (null !=skuMap) {
						skuCode = (String) skuMap.get("sku_code");
					}
					String exchange_no = WebHelper.upCode("RTG");
					
					//通过order_code查询出商品编号信息
					MDataMap mp = new MDataMap();
					mp.put("order_code", exchangeReport.getOrderCode());
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
						insertDatamap.put("order_code",exchangeReport.getOrderCode());
						insertDatamap.put("exchange_reason", "商品在订单中不存在");
						insertDatamap.put("create_time", nowTime);
						insertDatamap.put("create_user", user);
						try
						{
							DbUp.upTable("lc_not_exchange_goods").dataInsert(insertDatamap);
						} catch (Exception e)
						{
							bLogError(939301123);
						}
						continue;
					}
					else{
						//exchangeGoodscount 换货的数量  inCount 订货的数量
						int exchangeGoodscount = new Integer((int)Double.parseDouble(exchangeReport.getExchangeGoodscount()));
						int inCount = new Integer((int)Double.parseDouble(exchangeReport.getCount()));
						if (exchangeGoodscount>inCount) {
							//失败条数记录
							excelfaultCount++;
							MDataMap insertDatamap = new MDataMap();
							String nowTime = DateUtil.getNowTime();
							insertDatamap.put("order_code",exchangeReport.getOrderCode());
							insertDatamap.put("exchange_reason", "换货数量超过订货数量");
							insertDatamap.put("create_time", nowTime);
							insertDatamap.put("create_user", user);
							try
							{
								DbUp.upTable("lc_not_exchange_goods").dataInsert(insertDatamap);
							} catch (Exception e)
							{
								bLogError(939301123);
							}
							continue;
						}	
					/*插入一条审核中的换货订单记录*/
					ocExchangeGoods.setUid(WebHelper.upUuid());
					ocExchangeGoods.setExchangeNo(exchange_no);
					ocExchangeGoods.setSellerCode(manageCode);
					
					ocExchangeGoods.setStatus("4497153900050001");
					ocExchangeGoods.setMobile(new MemberUtil().getOrderMoblie(exchangeReport.getOrderCode()));
					ocExchangeGoods.setContacts(new MemberUtil().getOrderPerson(exchangeReport.getOrderCode()));
					if(exchangeReport.getThirdOrderCode()!=null){
					ocExchangeGoods.setThirdOrderCode(exchangeReport.getThirdOrderCode());
					}
                    if(exchangeReport.getOrderCode()!=null){
						ocExchangeGoods.setOrderCode(exchangeReport.getOrderCode());
						
					}
                    ocExchangeGoods.setBuyerCode(buyerCode);
					ocExchangeGoodsDetail.setUid(WebHelper.upUuid());
					ocExchangeGoodsDetail.setExchangeNo(exchange_no);
					ocExchangeGoodsDetail.setSkuCode(exchangeReport.getSkuCode());
					ocExchangeGoodsDetail.setSkuName(exchangeReport.getSkuName());
					ocExchangeGoodsDetail.setCurrentPrice(new BigDecimal(exchangeReport.getCurrentPrice()));
					ocExchangeGoodsDetail.setCount(new Integer((int)Double.parseDouble(exchangeReport.getExchangeGoodscount())));
					ocExchangeGoods.setCreateTime(exchangeReport.getCreateTime());
					
					/*创建换货日志*/
					
					//审核中日志
					logModel.setCreateTime(DateUtil.getNowTime());
					logModel.setCreateUser(user);
					logModel.setExchangeNo(exchange_no);
					logModel.setInfo("");
					logModel.setOldStatus("");
					logModel.setNowStatus("4497153900050003");
					createExchGoodLog(logModel, exchange_no);
										
					/*创建换货日志*/
					
					//审核通过日志
					logModel.setCreateTime(DateUtil.getNowTime());
					logModel.setCreateUser(user);
					logModel.setExchangeNo(exchange_no);
					logModel.setInfo("");
					logModel.setOldStatus("4497153900050003");
					logModel.setNowStatus("4497153900050001");
					createExchGoodLog(logModel, exchange_no);
					
					ocExchangeGoodsMapper.insertSelective(ocExchangeGoods);
					ocExchangeGoodsDetailMapper.insertSelective(ocExchangeGoodsDetail);
					excelsuccessCount++;
					
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
	
	
	private void createExchGoodLog(ExchangegoodsStatusLogModel logModel,
			String exchange_no) {
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("exchange_no", exchange_no);
		insertDatamap.put("info", logModel.getInfo());
		insertDatamap.put("create_time", logModel.getCreateTime());
		insertDatamap.put("create_user", logModel.getCreateUser());
		insertDatamap.put("old_status", logModel.getOldStatus());
		insertDatamap.put("now_status", logModel.getNowStatus());
		try
		{
			DbUp.upTable("lc_exchangegoods").dataInsert(insertDatamap);
		} catch (Exception e)
		{
			bLogError(939301121);
		}
		
		
	}
	private List<ExchangeReport> downloadAndAnalysisFile(String fileRemoteUrl) throws Exception{
		
		String extension = fileRemoteUrl.lastIndexOf(".") == -1 ? "" : fileRemoteUrl.substring(fileRemoteUrl.lastIndexOf(".") + 1);
		java.net.URL resourceUrl = new java.net.URL(fileRemoteUrl);
		InputStream content = (InputStream) resourceUrl.getContent();
		ReadExcelUtil<ExchangeReport> readExcelUtil = new ReadExcelUtil<ExchangeReport>();
		
		return readExcelUtil.readExcel(false, null, content, new String[]{"thirdOrderCode","orderCode","skuCode","skuName","standard","currentPrice","count","exchangeGoodscount","createTime"},
				new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class}, ExchangeReport.class, extension);
	}
}
