package com.cmall.productcenter.webfunc;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModMulSkuStocknfo extends RootFunc {

	/** 
	* @Description:商户批量修改库存
	* @param @param sOperateUid
	* @param @param mDataMap
	* @param @return
	* @author 张海生
	* @date 2015-11-24 下午12:56:59
	* @throws 
	*/
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MUserInfo userInfo = UserFactory.INSTANCE.create();
//		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || !userInfo.getManageCode().startsWith("SF03")){
		/**
		 * 修改商户判断条件 2016-12-02 zhy
		 */
		String seller_type = WebHelper.getSellerType(userInfo.getManageCode());
		if(null == userInfo || StringUtils.isEmpty(userInfo.getManageCode()) || StringUtils.isBlank(seller_type)){
			mResult.inErrorMessage(941901061, bInfo(941901064));
			return mResult;
		}
		Iterator<String> rtKey = mAddMaps.keySet().iterator();
		int skuFlag = 0;
		while(rtKey.hasNext()){
			String myKey = rtKey.next();
			String kenEnd = myKey.substring(myKey.indexOf("_")+1);
			if(myKey.startsWith("skuNum")){
				Iterator<String> rtKey3 = mAddMaps.keySet().iterator();
				String option2 = "";
				while(rtKey3.hasNext()){
					String myKey1 = rtKey3.next();
					String kenEnd1 = myKey1.substring(myKey1.indexOf("_")+1);
					if(kenEnd.equals(kenEnd1)){
						if(myKey1.startsWith("option")){
							option2 = mAddMaps.get(myKey1);
						}
					}
				}
				String stockNum = mAddMaps.get(myKey);
				if("1".equals(option2)){	//减少库存
					PlusSupportStock st = new PlusSupportStock();
					int remainStock = st.upAllStock(kenEnd);
					if(Integer.parseInt(stockNum) > remainStock){
						mResult.setResultCode(941901130);
						mResult.setResultMessage(bInfo(941901130, kenEnd));
						return mResult;
					}
				}
				if(StringUtils.isEmpty(stockNum)){
					continue;
				}
				skuFlag ++;
			}
		}
		if(skuFlag == 0){
			mResult.inErrorMessage(941901126);
			return mResult;
		}
		String userCode = userInfo.getUserCode();
		String productCode = mAddMaps.get("product_code");
		String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl")+productCode+"_1";
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		String createTime = DateUtil.getSysDateTimeString();
		String flowCode = "";
		try {
			//加入审批的流程
			ScFlowMain flow = new ScFlowMain();
			flow.setCurrentStatus("4497172300120001");
			String title = bInfo(941901127, productCode);
			flow.setFlowTitle(productCode);
			flow.setFlowType("449717230012");
			flow.setFlowUrl(preViewUrl);
			flow.setCreator(userCode);
			flow.setOuterCode(productCode);
			flow.setFlowRemark(title);
			//创建的审批流程
			RootResult ret = (new FlowService()).CreateFlow(flow);
			if(ret.getResultCode() == 1){
				flowCode = ret.getResultMessage();
			}else{
				mResult.inErrorMessage(ret.getResultCode());
				return mResult;
			}
		} catch (Exception e) {
		}
		Iterator<String> rtKey2 = mAddMaps.keySet().iterator();
		MDataMap sp = new MDataMap();
		sp.put("flow_code", flowCode);
		while(rtKey2.hasNext()){
			String myKey = rtKey2.next();
			if(myKey.startsWith("skuNum")){
				String stockNum = mAddMaps.get(myKey);
				if(StringUtils.isEmpty(stockNum)){
					continue;
				}
				String kenEnd = myKey.substring(myKey.indexOf("_")+1);
				String option = "";
				String skuuid = "";
				Iterator<String> rtKey1 = mAddMaps.keySet().iterator();
				while(rtKey1.hasNext()){
					String myKey1 = rtKey1.next();
					String kenEnd1 = myKey1.substring(myKey1.indexOf("_")+1);
					if(kenEnd.equals(kenEnd1)){
						if(myKey1.startsWith("option")){
							option = mAddMaps.get(myKey1);
						}
						if(myKey1.startsWith("skuuid")){
							skuuid = mAddMaps.get(myKey1);
						}
					}
				}
				sp.put("product_code", productCode);
				sp.put("sku_uid", skuuid);
				sp.put("sku_code", kenEnd);
				if("0".equals(option)){	//增加库存
					sp.put("operate_type", "1");
				}else if("1".equals(option)){	//减少库存
					//减少库存
					sp.put("operate_type", "2");
				}
				sp.put("change_num", stockNum);
				sp.put("deal_status", "4497471600230001");//待审批
				sp.put("create_time", createTime);
				sp.put("create_user", create_user);
				sp.put("update_ime", createTime);
				sp.put("update_user", create_user);
				try {
					DbUp.upTable("sc_skunum_change").dataInsert(sp);//插入到商品sku库存变化记录表
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return mResult;
	}

}
