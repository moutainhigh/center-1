package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.service.ProductSkuPriceService;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商品价格变更审批
 * 
 * @author pang_jhui
 * @author 修改人:LHY 2016-4-6
 * 
 */
public class ProductSkuPriceFlowFuncDsf1 extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				List<LinkedHashMap<String, Object>> pcFlows = new ArrayList<LinkedHashMap<String, Object>>();
				pcFlows = new JsonHelper<List<LinkedHashMap<String, Object>>>().StringToObjExp(mSubDataMap.get("json"), pcFlows);
				String doType = mSubDataMap.get("do_type");
				List<MDataMap> list = initDataMap(pcFlows,doType);
				//判读当前的审批流程是否存在。
				if(list != null && !list.isEmpty()){
					/*商品编号*/
					ScFlowMain sfm = new FlowService().getApprovalFlowByOurterCode(list.get(0).get("product_code"), "449717230014");
					if(sfm != null){
						mResult.inErrorMessage(941901136);
						return mResult;
					}else{
						new ProductSkuPriceService().createProductSkuPriceFlow(list, mSubDataMap, Constants.FLOW_STATUS_SKUPRICE_YY);
					}
				}
			}
		} catch (Exception e) {
			mResult.setResultCode(-1);
			mResult.setResultMessage(e.getMessage());
		}
		return mResult;
	}

	/**
	 * 转换数据集合
	 * 变更后成本价必须小于变更后销售价
	 * @param pcFlows
	 *            审批信息
	 * @return
	 */
	public List<MDataMap> initDataMap(List<LinkedHashMap<String, Object>> pcFlows,String doType) throws Exception {
		List<MDataMap> maps = new ArrayList<MDataMap>();
		for (Map<String, Object> dataMap : pcFlows) {
			MDataMap mDataMap = new MDataMap(dataMap);
			BigDecimal costPrice = BigDecimal.valueOf(Double.parseDouble(mDataMap.get("cost_price")));
			BigDecimal sellPrice = BigDecimal.valueOf(Double.parseDouble(mDataMap.get("sell_price")));
			if(costPrice.compareTo(sellPrice)>=0) {
				throw new Exception("变更后成本价必须小于变更后销售价");
			}
			//如果成本价调高,从 sc_event_item_product sc_event_info表 查询调价时间区间此商品是否有 活动 ,如果 有活动不允许调整 
			BigDecimal costPriceOld = BigDecimal.valueOf(Double.parseDouble(mDataMap.get("cost_price_old")));
			if(costPriceOld.compareTo(costPrice)<0) {
				String startTime = mDataMap.get("start_time")==null?"":mDataMap.get("start_time").toString();
				String endTime =  mDataMap.get("end_time")==null?"":mDataMap.get("end_time").toString();
				String productCode =  mDataMap.get("product_code").toString();
				String skuCode =  mDataMap.get("sku_code").toString();
				String sql ="select * from "+ 
						"(select b.* from sc_event_item_product a left join sc_event_info b on a.event_code = b.event_code  "
						+ "where a.flag_enable = 1 and b.event_status = '4497472700020002' and a.product_code =  '" + productCode + "' and"
						+ " a.sku_code =  '"+skuCode+"') aa ";
				if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
					sql += "where (aa.begin_time < '"+startTime+"' and aa.end_time > '"+startTime+"') "
						+ "or (aa.begin_time > '"+startTime+"' and aa.begin_time < '"+endTime+"')";
				}
				List<Map<String, Object>> dataSqlList = DbUp.upTable("sc_event_item_product").dataSqlList(sql, null);
				if(null!=dataSqlList&&dataSqlList.size()>0) {
					throw new Exception("该商品正在参加促销活动，不能上调成本价，如有疑问请联系商开！");
				}
			}
			
			maps.add(mDataMap);
		}
		if(!doType.equals("1")){
			checkChangeTime(pcFlows);
		}		
		return maps;
	}
	
	private void checkChangeTime(List<LinkedHashMap<String, Object>> pcFlows) throws Exception {
		for (Map<String, Object> dataMap : pcFlows) {
			String start_time = String.valueOf(dataMap.get("start_time"));
			String end_time = String.valueOf(dataMap.get("end_time"));
			BigDecimal costPrice = BigDecimal.valueOf(Double.parseDouble(dataMap.get("cost_price").toString()));
			BigDecimal costPriceOld = BigDecimal.valueOf(Double.parseDouble(dataMap.get("cost_price_old").toString()));
			Date startTime = DateUtil.toDate(start_time);
			Date endTime = DateUtil.toDate(end_time);
			Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
			if(startTime.compareTo(nowTime)<0) {
				throw new Exception("开始日期必须大于等于当前日期");
			}
			if(endTime.compareTo(nowTime)<0) {
				throw new Exception("结束日期必须大于等于当前日期");
			}
			if(endTime.compareTo(startTime)<0) {
				throw new Exception("开始日期必须小于或等于结束日期");
			}
			if(DateUtil.daysBetween(new Timestamp(startTime.getTime()), new Timestamp(endTime.getTime()))>365){
				throw new Exception("调价时间不能超过一年");
			}
			if(costPriceOld.compareTo(costPrice)<0) {
				throw new Exception("活动成本价不能高于档案成本价");
			}
		}
	}
}