package com.cmall.productcenter.webfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.UcSellercategoryProductRelation;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProductForCm extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				ProductService pService = new ProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				PcProductinfo pcProductinfo = new PcProductinfo();
				pcProductinfo = new JsonHelper<PcProductinfo>().StringToObj(mSubDataMap.get("json"), pcProductinfo);
				String sc = UserFactory.INSTANCE.create().getManageCode();
				if(sc==null||"".equals(sc)){
					sc = pcProductinfo.getSellerCode();
				}
				if(sc!=null&&!"".equals(sc)&&UserFactory.INSTANCE.create()!=null)
				{
					if(mSubDataMap.get("c_sellercategory")!=null&&!"".equals(mSubDataMap.get("c_sellercategory"))){
						List<UcSellercategoryProductRelation> usprList = new ArrayList<UcSellercategoryProductRelation>();
						String c_sellercategory[] = mSubDataMap.get("c_sellercategory").split(","); 
						for(int i=0;i<c_sellercategory.length;i++){
							UcSellercategoryProductRelation relation = new UcSellercategoryProductRelation();
							relation.setCategoryCode(c_sellercategory[i]);
							relation.setSellerCode(sc);
							usprList.add(relation);
						}
						pcProductinfo.setUsprList(usprList);
					}
					StringBuffer error = new StringBuffer();
					pService.AddProduct(pcProductinfo, error);
					if (StringUtils.isEmpty(error.toString())) {
						mResult.setResultMessage(bInfo(909101002));
					} else {
						mResult.inErrorMessage(909101003, error.toString());
					}
				}else{
					mResult.inErrorMessage(909101004);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909101004);
		}
		return mResult;
	}
}
