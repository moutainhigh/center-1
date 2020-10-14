package com.cmall.usercenter.template;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.cmall.systemcenter.service.SystemCheck;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.MapHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.UserSupport;

public class ApiGetShopTemplate extends
		RootApi<MWebResult, ApiGetShopTemplateInput> implements IFlowFunc {

	public MWebResult Process(ApiGetShopTemplateInput inputParam,
			MDataMap mRequestMap) {

		MWebResult mResult = new MWebResult();

		UserSupport userSupport = new UserSupport();

		if (userSupport.checkLogin()) {

			String sManageCode = userSupport.getUserInfo().getManageCode();

			MDataMap mDataMap = DbUp.upTable("uc_shop_template").one(
					"seller_code", sManageCode, "uid", inputParam.getUid());

			if (MapHelper.isNotEmpty(mDataMap)) {

				if (inputParam.getCallType() == 1) {

					mResult.setResultObject(mDataMap.get("template_autosave"));
				} else if (inputParam.getCallType() == 2) {

					mDataMap.put("template_autosave", inputParam.getContent());
					mDataMap.put("template_preview", inputParam.getPreview());
					mDataMap.put("update_time", FormatHelper.upDateTime());
					DbUp.upTable("uc_shop_template").dataUpdate(mDataMap,
							"template_autosave,template_preview", "uid");

				}
				// 如果是3 则保存并申请审批
				else if (inputParam.getCallType() == 3) {
					
					if(mResult.upFlagTrue())
					{
						SystemCheck systemCheck=new SystemCheck();
						MWebResult mCheckLinkResult=systemCheck.checkLink(inputParam.getPreview());
						if(!mCheckLinkResult.upFlagTrue())
						{
							mResult.setResultCode(mCheckLinkResult.getResultCode());
							mResult.setResultMessage(mCheckLinkResult.getResultMessage());
						}
						
						
						
					}
					
					
					
					if(mResult.upFlagTrue())
					{
					//校验当前是否有未完成的审批
					FlowService flowService = new FlowService();
					
					if(flowService.isExistSP(mDataMap.get("uid"))){
						
						mResult.setResultCode(959701029);
						mResult.setResultMessage(bInfo(959701029));
						
					}else{
						mDataMap.put("template_autosave", inputParam.getContent());
						mDataMap.put("template_preview", inputParam.getPreview());

						mDataMap.put("update_time", FormatHelper.upDateTime());

						DbUp.upTable("uc_shop_template").dataUpdate(mDataMap,
								"template_autosave,template_preview,update_time",
								"uid");

						

						ScFlowMain flow = new ScFlowMain();
						flow.setCurrentStatus("4497172300050001");
						flow.setFlowRemark(userSupport.getUserInfo()
								.getManageCode());
						flow.setCreator(userSupport.getUserInfo().getLoginName());
						flow.setFlowUrl(bConfig("systemcenter.weburl_cshop")
								+ "/manage/preview?uid=" + mDataMap.get("uid"));
						flow.setFlowType("449717230005");
						flow.setFlowTitle(bInfo(954901020, userSupport
								.getUserInfo().getManageCode(), mDataMap
								.get("template_name")));
						flow.setOuterCode(mDataMap.get("uid"));

						flowService.CreateFlow(flow);
					}
					}
				}

			}

		}

		return mResult;

	}

	private void changeStatus(String sUid) {

		MDataMap mDataMap = DbUp.upTable("uc_shop_template").one("uid", sUid);

		PriviewTemplate pTemplate = new PriviewTemplate();

		String sContent = pTemplate.upPriview(mDataMap.get("template_preview"),
				bConfig("usercenter.template_resources"));

		mDataMap.put("template_content", sContent);

		// 定义顶部结束元素
		String sHeader = "<div class=\"ctheme_shop_top\"></div>";

		if (StringUtils.contains(sContent, sHeader)) {
			sHeader = StringUtils.substringBefore(sContent, sHeader)
					+ "</div></div></div>";
		} else {
			sHeader = "";
		}

		mDataMap.put("template_header", sHeader);

		// 更新所有其他同类型模板不可用
		{
			MDataMap mUpdateAllNotUse = new MDataMap();
			mUpdateAllNotUse.put("template_type_did",
					mDataMap.get("template_type_did"));
			mUpdateAllNotUse.put("seller_code", mDataMap.get("seller_code"));
			mUpdateAllNotUse.put("flag_enable", "0");

			DbUp.upTable("uc_shop_template").dataUpdate(mUpdateAllNotUse,
					"flag_enable", "seller_code,template_type_did");

		}

		mDataMap.put("flag_enable", "1");
		DbUp.upTable("uc_shop_template").dataUpdate(mDataMap,
				"template_content,template_header,flag_enable", "uid");

	}

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		// return null;
		RootResult result = new RootResult();

		if (fromStatus.equals("4497172300050001")
				&& toStatus.equals("4497172300050002")) {

			changeStatus(outCode);
			UserSupport userSupport = new UserSupport();
			
			//通知前端生成静态页面
			ProductJmsSupport pjs = new ProductJmsSupport();
			
			
			MDataMap md = DbUp.upTable("uc_shop_template").one("uid",outCode);
			
			if(md!=null){
				//{"type":"shop.index","data":"sellerCode"}
				String jsonData="{\"type\":\"shop.index\",\"data\":\""+md.get("seller_code")+"\"}";
				pjs.OnChangeSku(jsonData);
			}
			
				

		}

		return result;

	}

}
