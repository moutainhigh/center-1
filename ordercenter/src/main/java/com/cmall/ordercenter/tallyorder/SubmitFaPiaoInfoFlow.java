package com.cmall.ordercenter.tallyorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
public class SubmitFaPiaoInfoFlow extends RootFunc{

	/**
	 * 发票信息流提交维护
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String state = mSubMap.get("state");
		if("44975003001".equals(state)) {
			//商管提交
			String uid=mSubMap.get("uid");
			String is_issue = mSubMap.get("is_issue");
			String document_type = mSubMap.get("document_type");
			String document_nature = mSubMap.get("document_nature")==null?"":mSubMap.get("document_nature");
			if("0".equals(is_issue)) {
				DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"is_issue",is_issue,"document_type",document_type,"document_nature",document_nature,"submit_flag","1","document_state","0","update_time",DateUtil.getSysDateTimeString()),"is_issue,document_type,document_nature,submit_flag,document_state,update_time", "uid");
				
			}
			else {
				if("zz".equals(document_type)) {
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"is_issue",is_issue,"document_type",document_type,"document_nature",document_nature,"submit_flow","44975003002","submit_flag","1","document_state","0","update_time",DateUtil.getSysDateTimeString(),"waybill_num_state","2"),"submit_flag,document_state,is_issue,document_type,document_nature,submit_flow,update_time,waybill_num_state", "uid");
				}
				else {
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"is_issue",is_issue,"document_type",document_type,"document_nature",document_nature,"submit_flow","44975003002","submit_flag","1","document_state","0","update_time",DateUtil.getSysDateTimeString()),"submit_flag,document_state,is_issue,document_type,document_nature,submit_flow,update_time", "uid");
				}
				
			}
			addLogs(uid,"提交通过");
			
		}
		else if("44975003003".equals(state)) {
			//商管维护运单号,并提交完成开发票流转			
			String uid=mSubMap.get("uid");
			String waybill_num = mSubMap.get("waybill_num");
			DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"waybill_num",waybill_num,"submit_flow","44975003004","update_time",DateUtil.getSysDateTimeString(),"waybill_num_state","1","document_state","1"), "waybill_num,submit_flow,update_time,waybill_num_state,document_state", "uid");
			addLogs(uid,"维护运单号通过");
		}
		else if("44975003002".equals(state)) {
			//财务提交开发票
			String uid=mSubMap.get("uid");
			String document_type=mSubMap.get("document_type");
			if("zz".equals(document_type)) {
				DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"submit_flow","44975003003","update_time",DateUtil.getSysDateTimeString(),"waybill_num_state","2"), "submit_flow,update_time,waybill_num_state", "uid");
			}
			else if("dz".equals(document_type)) {
				DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"submit_flow","44975003004","update_time",DateUtil.getSysDateTimeString(),"document_state","1"), "submit_flow,update_time,document_state", "uid");
			}
			
			//商户的发票维护信息入库不变
			Map<String,Object>  relMap = DbUp.upTable("uc_seller_invoice_info").dataSqlOne("select a.* from usercenter.uc_seller_invoice_info a, ordercenter.oc_documents_info b where  a.small_seller_code=b.small_seller_code and b.uid=:uid", new MDataMap("uid",uid));
			if(relMap != null) {
				MDataMap paramMap =new MDataMap();
				for (String key : relMap.keySet()) {
					paramMap.put(key, relMap.get(key).toString());
				}
				paramMap.remove("zid");
				paramMap.put("uid",uid);
				DbUp.upTable("uc_seller_invoice_info_over").dataInsert(paramMap);
			}
			
			addLogs(uid,"开发票通过");
		}
		else if("44975003001_batch".equals(state)) {
			//商管批量提交
			String uids=mSubMap.get("uid");
			String[] uidArray = uids.split(",");
			String[] is_issues = mSubMap.get("is_issue").split(",");
			String[] document_types = mSubMap.get("document_type").split(",");
			String[] document_natures = mSubMap.get("document_nature").split(",");
			for (int i=0;i<uidArray.length;i++) {
				String uid = uidArray[i];
				String is_issue = is_issues[i];
				String document_type = document_types[i];
				String document_nature = "";
				if(document_natures!=null&&document_natures.length>0) {document_nature=document_natures[i];}
				if("0".equals(is_issue)) {
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"is_issue",is_issue,"document_type",document_type,"document_nature",document_nature,"submit_flag","1","document_state","0","update_time",DateUtil.getSysDateTimeString()),"is_issue,document_type,document_nature,submit_flag,document_state,update_time", "uid");
				}
				else {
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",uid,"is_issue",is_issue,"document_type",document_type,"document_nature",document_nature,"submit_flow","44975003002","submit_flag","1","document_state","0","update_time",DateUtil.getSysDateTimeString()),"submit_flag,document_state,is_issue,document_type,document_nature,submit_flow,update_time", "uid");
				}
				
				addLogs(uid,"提交通过");
			}
		}
		else if("44975003002_batch".equals(state)) {
			String small_seller_code = mSubMap.get("small_seller_code");
			String small_seller_name = mSubMap.get("small_seller_name");
			String uc_seller_type = mSubMap.get("uc_seller_type");
			String document_nature = mSubMap.get("document_nature");
			String document_type = mSubMap.get("document_type");
			String bill_time_flag = mSubMap.get("bill_time_flag");
			String fee_type = mSubMap.get("fee_type");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			
			if(StringUtils.isBlank(small_seller_code)&&StringUtils.isBlank(small_seller_name)&&StringUtils.isBlank(uc_seller_type)&&
					StringUtils.isBlank(document_nature)&&StringUtils.isBlank(document_type)&&StringUtils.isBlank(bill_time_flag)) {
				list =  DbUp.upTable("oc_documents_info").dataSqlList("select * from oc_documents_info where fee_type=:fee_type and bill_time_flag=:bill_time_flag",new MDataMap("fee_type",fee_type,"bill_time_flag","1"));
			}
			else {
				list =  DbUp.upTable("oc_documents_info").dataSqlList("select * from oc_documents_info where small_seller_code=:small_seller_code and small_seller_name=:small_seller_name and uc_seller_type=:uc_seller_type "
						+ "and document_nature=:document_nature and document_type=:document_type and bill_time_flag and fee_type=:fee_type and bill_time_flag=:bill_time_flag", new MDataMap("small_seller_code",small_seller_code,"small_seller_name",small_seller_name,
								"uc_seller_type",uc_seller_type,"document_nature",document_nature,"document_type",document_type,"bill_time_flag",bill_time_flag,"fee_type",fee_type,"bill_time_flag","1"));
			}
			for (Map<String, Object> map : list) {
				if("44975003002".equals(map.get("submit_flow"))&&"dz".equals(map.get("document_type"))&&"1".equals(map.get("bill_time_flag"))) {
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",map.get("uid").toString(),"submit_flow","44975003004","update_time",DateUtil.getSysDateTimeString(),"document_state","1"), "submit_flow,update_time,document_state", "uid");
					
					//发票状态变更，添加日志操作
					MDataMap logMap = new MDataMap();
					logMap.put("uid",UUID.randomUUID().toString().replace("-", ""));
					logMap.put("document_code", map.get("document_code").toString());
					logMap.put("operating_time", FormatHelper.upDateTime());
					logMap.put("operator",UserFactory.INSTANCE.create().getRealName());
					logMap.put("small_seller_type",map.get("uc_seller_type").toString());
					logMap.put("remark","开发票通过");
					DbUp.upTable("lc_fapiao_log").dataInsert(logMap);
				}
				if("44975003002".equals(map.get("submit_flow"))&&"zz".equals(map.get("document_type"))&&"1".equals(map.get("bill_time_flag"))) {
					//纸质发票返回商管进行运单号维护节点
					DbUp.upTable("oc_documents_info").dataUpdate(new MDataMap("uid",map.get("uid").toString(),"submit_flow","44975003003","update_time",DateUtil.getSysDateTimeString(),"waybill_num_state","2"), "submit_flow,update_time,waybill_num_state", "uid");
					
					MDataMap logMap = new MDataMap();
					logMap.put("uid",UUID.randomUUID().toString().replace("-", ""));
					logMap.put("document_code", map.get("document_code").toString());
					logMap.put("operating_time", FormatHelper.upDateTime());
					logMap.put("operator",UserFactory.INSTANCE.create().getRealName());
					logMap.put("small_seller_type",map.get("uc_seller_type").toString());
					logMap.put("remark","开发票通过");
					DbUp.upTable("lc_fapiao_log").dataInsert(logMap);
				}
				
				//商户的发票维护信息入库不变
				Map<String,Object>  relMap = DbUp.upTable("uc_seller_invoice_info").dataSqlOne("select a.* from usercenter.uc_seller_invoice_info a, ordercenter.oc_documents_info b where  a.small_seller_code=b.small_seller_code and b.uid=:uid", new MDataMap("uid",map.get("uid").toString()));
				if(relMap != null) {
					MDataMap paramMap =new MDataMap();
					for (String key : relMap.keySet()) {
						paramMap.put(key, relMap.get(key).toString());
					}
					paramMap.remove("zid");
					paramMap.put("uid",map.get("uid").toString());
					DbUp.upTable("uc_seller_invoice_info_over").dataInsert(paramMap);
				}
			}
		}
		
		mResult.setResultMessage("操作成功");
		return mResult;
	}

	private void addLogs(String uid,String remark) {
		Map<String, Object> map = DbUp.upTable("oc_documents_info").dataSqlOne("select * from oc_documents_info where uid=:uid", new MDataMap("uid",uid));
		MDataMap logMap = new MDataMap();
		logMap.put("uid",UUID.randomUUID().toString().replace("-", ""));
		logMap.put("document_code", map.get("document_code").toString());
		logMap.put("operating_time", FormatHelper.upDateTime());
		logMap.put("operator",UserFactory.INSTANCE.create().getRealName());
		logMap.put("small_seller_type",map.get("uc_seller_type").toString());
		logMap.put("remark",remark);
		
		DbUp.upTable("lc_fapiao_log").dataInsert(logMap);
		
	}
	
	

}
