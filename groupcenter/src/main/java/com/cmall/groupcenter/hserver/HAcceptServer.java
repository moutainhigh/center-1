package com.cmall.groupcenter.hserver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.hserver.model.HServerResponse;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 接收LD推送信息的处理控制类
 * @author jlin
 *
 */
public class HAcceptServer extends BaseClass {

	public HServerResponse doAccept(String request) {

		String code = WebHelper.upCode("LCAL");
		DbUp.upTable("lc_accept_log").dataInsert(new MDataMap("code", code, "request_data", request, "request_time", DateUtil.getSysDateTimeString()));

		// 获取接口编号
		String moi_code_cd = (String) JSON.parseObject(request).get("moi_code_cd");

		HServerResponse response =new HServerResponse(false,moi_code_cd);
		MDataMap funcInfo = DbUp.upTable("fh_accept_func").one("moi_code_cd", moi_code_cd);
		if(funcInfo==null||funcInfo.isEmpty()){
			String process_time=DateUtil.getSysDateTimeString();
			DbUp.upTable("lc_accept_log").dataUpdate(new MDataMap("code", code, "moi_code_cd", moi_code_cd, "response_data", JSON.toJSONString(response),"response_time", process_time,"process_time",process_time,"process_data","未发现有效的接收处理类"),"","code");
			return response;
		}
		try {
			Class<?> cClass = ClassUtils.getClass(funcInfo.get("accept_func"));
			if (cClass != null) {
				Type[] superclassType = cClass.getGenericInterfaces();
				@SuppressWarnings("unchecked")
				IAcceptFunc<IHServerRequest> acceptFunc = (IAcceptFunc<IHServerRequest>) cClass.newInstance();
				
				JsonHelper<IHServerRequest> jsonHelper = new JsonHelper<IHServerRequest>();
				
				IHServerRequest requestObj=(IHServerRequest)Class.forName(StringUtils.remove((((ParameterizedType)superclassType[0]).getActualTypeArguments()[0]).toString(), "class ")).newInstance();
				requestObj=jsonHelper.StringToObjExp(request, requestObj);
				
				AcceptResult acceptResult = acceptFunc.doProcess(requestObj);
				if(acceptResult.upFlagTrue()){
					response.setSuccess(true);
					String process_time=DateUtil.getSysDateTimeString();
					//DbUp.upTable("lc_accept_log").dataUpdate(new MDataMap("code", code, "moi_code_cd", moi_code_cd, "response_data", JSON.toJSONString(response),"response_time", process_time,"process_time",process_time),"","code");
					DbUp.upTable("lc_accept_log").dataUpdate(new MDataMap("code", code, "moi_code_cd", moi_code_cd, "response_data", JSON.toJSONString(response),"response_time", process_time,"process_time",process_time,"process_data",acceptResult.getResultMessage(),"process_num",String.valueOf(acceptResult.getProcessNum()),"success_num",String.valueOf(acceptResult.getSuccessNum())),"","code");
				}else{
					String process_time=DateUtil.getSysDateTimeString();
					DbUp.upTable("lc_accept_log").dataUpdate(new MDataMap("code", code, "moi_code_cd", moi_code_cd, "response_data", JSON.toJSONString(response),"response_time", process_time,"process_time",process_time,"process_data",acceptResult.getResultMessage(),"process_num",String.valueOf(acceptResult.getProcessNum()),"success_num",String.valueOf(acceptResult.getSuccessNum())),"","code");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			String process_time=DateUtil.getSysDateTimeString();
			DbUp.upTable("lc_accept_log").dataUpdate(new MDataMap("code", code, "moi_code_cd", moi_code_cd, "response_data", JSON.toJSONString(response),"response_time", process_time,"error_expection",ExceptionUtils.getStackTrace(e),"process_time",process_time,"process_data","未发现有效的接收处理类"),"","code");
			return response;
		}
		return response;
	}
}
