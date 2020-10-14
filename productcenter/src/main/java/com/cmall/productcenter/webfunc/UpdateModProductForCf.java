package com.cmall.productcenter.webfunc;


import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 *惠家友后台修改商品
 *
 *@author jack
 *@version 1.0 
 * 
 */
public class UpdateModProductForCf extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				String startDate = mDataMap.get("start_date");
				String endDate = mDataMap.get("end_date");
				
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pp = new PcProductinfo();
				pp = new JsonHelper<PcProductinfo>().StringToObj(mSubDataMap.get("json"), pp);
				
				// 商品保障必须勾选
				if(StringUtils.isBlank(mSubDataMap.get("authority_logo"))){
					mResult.inErrorMessage(941901145);
					return mResult; 
				}
				
				// 支持/不支持7日无理由退货不能同时勾选
				if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday"))
						&& mSubDataMap.get("authority_logo").contains(bConfig("productcenter.authority_logo_sevenday_no"))){
					String msg1 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday")).get("logo_content");
					String msg2 = DbUp.upTable("pc_authority_logo").one("uid",bConfig("productcenter.authority_logo_sevenday_no")).get("logo_content");
					mResult.inErrorMessage(941901146,msg1,msg2);
					return mResult; 
				}
				
				// 仅支持在线支付必须设置时间
				// 逻辑调整，只要勾选了就永久有效
				if("449747110002".equals(pp.getOnlinepayFlag())){
					//if(DateUtils.parseDate(pp.getOnlinepayStart(), "yyyy-MM-dd HH:mm:ss") == null
					//		|| DateUtils.parseDate(pp.getOnlinepayEnd(), "yyyy-MM-dd HH:mm:ss") == null){
					//	mResult.setResultCode(0);
					//	mResult.setResultMessage("请设置仅支持在线支付的生效时间");
					//	return mResult;
					//}
				}else{
					pp.setOnlinepayFlag("449747110001");
					pp.setOnlinepayStart("");
					pp.setOnlinepayEnd("");
				}
				
				for(int i = 0 ; i < pp.getPcProductpropertyList().size() ; i ++){
					if(StringUtils.contains(pp.getPcProductpropertyList().get(i).getPropertyKey(), "内联赠品")){
						if(!this.compareDate(startDate, endDate) && StringUtils.isNotBlank(pp.getPcProductpropertyList().get(i).getPropertyValue())){
							mResult.setResultMessage("错误的时间范围");
							return mResult;
						}
						pp.getPcProductpropertyList().get(i).setType(1);
						pp.getPcProductpropertyList().get(i).setStartDate(startDate);
						pp.getPcProductpropertyList().get(i).setEndDate(endDate);
						break;
					}
				}
				
				StringBuffer error = new StringBuffer();
				String sc = pp.getSellerCode();//商品所属店铺编号
				MUserInfo uc = UserFactory.INSTANCE.create();//当前用户所属店铺编号
				if(uc==null){
					mResult.inErrorMessage(941901065, bInfo(941901064));
				}else if(sc!=null&&!"".equals(sc)){
					/**
					 * 如果不是LD商品，查询是否有法务待审批流程，如果存在提示正在审批中
					 */
					if(!StringUtils.equals("SI2003", pp.getSmallSellerCode())){
						MDataMap flowIsExists = DbUp.upTable("sc_flow_main").one("outer_code",pp.getProductCode(),"flow_type","449717230016","flow_isend","0","current_status","4497172300160011");
						if(flowIsExists != null){
							mResult.setResultCode(-1);
							mResult.setResultMessage("商品修改正在审批中");
							return mResult;
						}
					}
					PcProductinfo pro = pService.getProduct(pp.getProductCode());
					pp.getProductSkuInfoList().clear();
					pp.setProductSkuInfoList(pro.getProductSkuInfoList());
					pService.updateProduct(pp, error);
					//判断是否修改图片，如果修改图片插入商品更新表
					String picEditFlag = mSubDataMap.get("picEdit_flag");
					if("1".equals(picEditFlag)) {
						String uid = mSubDataMap.get("uid");
						MDataMap proMap = DbUp.upTable("pc_productinfo").one("uid",uid);
						MDataMap updateMap = new MDataMap();
						updateMap.put("product_code",proMap.get("product_code"));
						updateMap.put("updatepic_time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
						DbUp.upTable("lc_productpic_update").dataInsert(updateMap);
					}
					
					if (StringUtils.isEmpty(error.toString())) {
						
						// 更新商品保障标识
						DbUp.upTable("pc_product_authority_logo").delete("product_code",pp.getProductCode());
						if(StringUtils.isNotBlank(mSubDataMap.get("authority_logo"))){
							String[] vs = mSubDataMap.get("authority_logo").split(",");
							for(String v : vs){
								if(StringUtils.isBlank(v)) continue;
								MDataMap data = new MDataMap("product_code",pp.getProductCode(),"authority_logo_uid",v,"create_time",FormatHelper.upDateTime());
								DbUp.upTable("pc_product_authority_logo").dataInsert(data);
							}
						}
						
						if(!StringUtils.equals("SI2003", pp.getSmallSellerCode())){
							/**
							 * =================修改商品后提交到商品审批流程，到达节点法务待审批 start==============
							 */
							// 加入审批的流程
							ScFlowMain flow = new ScFlowMain();
							flow.setCreator(uc.getUserCode());
							flow.setCurrentStatus("4497172300160011");
							String title = "修改商品"+pp.getProductCode()+"信息，待法务审批";
							flow.setFlowTitle(pp.getProductCode());
							// flow.setFlowType("449717230011");
							// 修改添加商品跳转节点 2016-06-24 zhy
							flow.setFlowType("449717230016");
							String preViewUrl = bConfig("productcenter.PreviewCheckProductUrl") + pp.getProductCode() + "_1";
							flow.setFlowUrl(preViewUrl);
							flow.setCreator(uc.getUserCode());
							flow.setOuterCode(pp.getProductCode());
							flow.setFlowRemark(title);
							FlowService flowService = new FlowService();
							flowService.CreateFlow(flow);
							/**
							 * ================= end ==============
							 */
						}
						mResult.setResultMessage(bInfo(941901097));
					} else {
						mResult.inErrorMessage(941901098, error.toString());
					}
				}else{
					mResult.inErrorMessage(941901099);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(941901099);
		}
		return mResult;
	}
	
	private boolean compareDate(String a , String b){
		if(StringUtils.isBlank(a) || StringUtils.isBlank(b)){ 
			return false;
		}
		return a.compareTo(b) < 0;
	}
}
