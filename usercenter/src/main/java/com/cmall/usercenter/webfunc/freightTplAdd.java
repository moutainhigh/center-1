package com.cmall.usercenter.webfunc;

import java.io.IOException;
import java.util.List;





import com.cmall.usercenter.model.FreightTpl;
import com.cmall.usercenter.model.FreightTplDetail;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加运费模板
 * @author huoqiangshou
 *
 */
public class freightTplAdd extends RootFunc{

	
	private static String TABLE_TPL="uc_freight_tpl"; //运费模板
	
	private static String TABLE_TPL_DETAL="uc_freight_tpl_detail"; //运费模板明细
	
	public MWebResult funcDo(String sOperateUid, MDataMap dataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		
		String data = dataMap.get("data");
		ObjectMapper mapper = new ObjectMapper(); 
		
		//mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			FreightTpl u = mapper.readValue(data, FreightTpl.class);
			
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("store_id",UserFactory.INSTANCE.create().getManageCode());
			mDataMap.put("tpl_name", u.getTplName());
			
			mDataMap.put("province", u.getProvince());
			mDataMap.put("city", u.getCity());
			mDataMap.put("area", u.getArea());
			mDataMap.put("consignment_time", u.getConsignmentTime());
			mDataMap.put("is_free", u.getIsFree());
			mDataMap.put("valuation_type", u.getValuationType());
			
			
			mDataMap.put("freight_mode", "449746210002");//快递
			mDataMap.put("isDisable", u.getIsDisable());
			if("449746250001".equals(u.getIsDisable())){  //禁用的话 记录时间
				mDataMap.put("disableDate", FormatHelper.upDateTime());
			}
			mDataMap.put("createDate", FormatHelper.upDateTime());
			String tplId = DbUp.upTable(TABLE_TPL).dataInsert(mDataMap);
			
			if(u.getExpress().size()>0){
				doSaveDetail(tplId,u.getExpress());
			}
			
		}
			
		 catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		mResult.setResultCode(1);
		mResult.setResultType("116018010");
		String mCode = UserFactory.INSTANCE.create().getManageCode();
		if("SI3003".equals(mCode)){
			mResult.setResultObject("zapjs.f.tourl('page_chart_v_uc_freight_tpl_cdog')"); 
		}else{
			mResult.setResultObject("zapjs.f.tourl('page_chart_v_uc_freight_tpl')"); 
		}
		return mResult;
	}
	
	/**
	 * 保存运费明细
	 * @param tpl 
	 * @param details
	 * @return
	 */
	public boolean doSaveDetail(String tplId,List<FreightTplDetail> details){
		
		for(FreightTplDetail detail:details){
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("tpl_uid", tplId);
			mDataMap.put("tpl_type_id", detail.getTplTypeId());
			mDataMap.put("isEnable", detail.getIsEnable());
			mDataMap.put("area", detail.getArea());
			mDataMap.put("area_Code", detail.getAreaCode());
			mDataMap.put("express_Start", detail.getExpressStart());
			mDataMap.put("express_Postage",detail.getExpressPostage());
			mDataMap.put("express_Plus", detail.getExpressPlus());
			mDataMap.put("express_Postage_plus", detail.getExpressPostageplus());
			mDataMap.put("sequence", String.valueOf(detail.getSequence()));
			DbUp.upTable(TABLE_TPL_DETAL).dataInsert(mDataMap);
		}
		return true;
	}
	
	

}
