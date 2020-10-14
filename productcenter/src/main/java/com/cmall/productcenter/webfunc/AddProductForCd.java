package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 沙皮狗添加商品
 * @author ligj
 *
 */
public class AddProductForCd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		try {

			if (mResult.upFlagTrue()) {

				ProductService pService = new ProductService();

				MDataMap mSubDataMap = mDataMap
						.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

				PcProductinfo pcProductinfo = new PcProductinfo();
				pcProductinfo = new JsonHelper<PcProductinfo>().StringToObj(
						mSubDataMap.get("json"), pcProductinfo);
				
				pcProductinfo.setSellerCode(AppConst.MANAGE_CODE_CDOG);
				pcProductinfo.setSmallSellerCode(AppConst.MANAGE_CODE_CDOG);
				
				//沙皮狗商品都设置为虚拟商品
				pcProductinfo.setValidate_flag("Y");
				//设为不可售
				pcProductinfo.setFlagSale(0);
				//扩展信息
				  {
					  //一下代码控制如TDS1仓库
					  pcProductinfo.getPcProductinfoExt().setPrchType("20");
					  pcProductinfo.getPcProductinfoExt().setDlrId(pcProductinfo.getSmallSellerCode());
					  pcProductinfo.getPcProductinfoExt().setDlrNm(pcProductinfo.getSmallSellerCode());
					  pcProductinfo.getPcProductinfoExt().setOaSiteNo(AppConst.CDOG_STORE_CODE);
					  pcProductinfo.getPcProductinfoExt().setValidateFlag("Y");
				  }
				
				
				StringBuffer error = new StringBuffer();
				pService.AddProduct(pcProductinfo, error);
				
				if (StringUtils.isEmpty(error.toString())) {
					mResult.setResultMessage(bInfo(951001003));

				} else {
					mResult.inErrorMessage(951001004, error.toString());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(951001005);
		}

		return mResult;

	}

}
