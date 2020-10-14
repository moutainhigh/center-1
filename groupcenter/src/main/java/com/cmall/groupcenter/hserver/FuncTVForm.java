package com.cmall.groupcenter.hserver;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.cmall.groupcenter.hserver.model.FuncTVFormRequest;
import com.cmall.groupcenter.hserver.model.TVForm;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.helper.KvHelper;

/**
 * 节目单推送处理逻辑
 * 
 * @author zhaojunling
 */
public class FuncTVForm implements IAcceptFunc<FuncTVFormRequest> {

	
	@Override
	public AcceptResult doProcess(FuncTVFormRequest request) {

		AcceptResult acceptResult = new AcceptResult();
		
		List<TVForm> results = request.getResults();
		
		acceptResult.setProcessNum(results.size());
		MDataMap tvForm;
		List<Map<String,Object>> mapList;
		if (results != null) {
			
			// 加锁，避免家有那边并发调用造成重复数据插入
			String lockCode = null;
			// 把内容关联到锁定的KEY，只对相同内容的推送加锁互斥
			String lockKey = "FuncTVForm-"+md5(request);
			try {
				lockCode = KvHelper.lockCodes(30, lockKey);
				
				// 未取到锁定内容则标识重复请求，忽略
				if(StringUtils.isBlank(lockCode)){
					return acceptResult;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 加锁异常，如redis访问失败的情况不能影响后续逻辑处理
			}
			
			try{
				for (TVForm form : results) {
					
					if(form == null ){
						continue;
					}
					
					//tvForm = DbUp.upTable("pc_tv").oneWhere("", "", "", "form_id",form.getFORM_ID(), "form_seq", form.getFORM_SEQ());
					mapList = DbUp.upTable("pc_tv").upTemplate().queryForList("select * from pc_tv where form_id = :form_id and form_seq =:form_seq limit 1", new MDataMap("form_id",form.getFORM_ID(),"form_seq",form.getFORM_SEQ()));
					
					tvForm = null;
					if(!mapList.isEmpty()){
						tvForm = new MDataMap(mapList.get(0));
					}
					
					// 删除操作
					if(form.getCHANGE_CD() != null && form.getCHANGE_CD().equals("D")){ // 删除
						if(StringUtils.isBlank(form.getFORM_SEQ())){
							// 没有form_seq则是节目单删除，需要删除节目单数据
							DbUp.upTable("pc_tv").delete("form_id", form.getFORM_ID());
						}else if(tvForm != null){
							// 节目单商品明细删除，只需删除节目单下某个商品数据
							DbUp.upTable("pc_tv").delete("zid", tvForm.get("zid"));
						}
						continue;
					}
					
					MDataMap formObj = new MDataMap();
					formObj.put("form_id", form.getFORM_ID());
					formObj.put("title_nm", form.getTITLE_NM());
					formObj.put("form_fr_date", formatDate(form.getFORM_FR_DATE()));
					formObj.put("form_end_date", formatDate(form.getFORM_END_DATE()));
					formObj.put("form_cd", form.getFORM_CD());
					formObj.put("form_seq", form.getFORM_SEQ());
					formObj.put("so_id", form.getSO_ID());
					formObj.put("good_id", form.getGOOD_ID());
					formObj.put("form_good_mis", form.getFORM_GOOD_MIS());
					formObj.put("update_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					
					if(StringUtils.isBlank(form.getFORM_SEQ())){
						// 节目单基本信息修改,不修改商品编号和播出时长
						// 节目单基本信息不考虑新增
						DbUp.upTable("pc_tv").dataUpdate(formObj, "title_nm,form_fr_date,form_end_date,form_cd,so_id,update_time", "form_id");
					}else{
						// 节目单明细修改,新增或者更新操作
						if(tvForm != null){
							// 有记录则修改
							formObj.put("zid", tvForm.get("zid"));
							formObj.put("uid", tvForm.get("uid"));
							DbUp.upTable("pc_tv").update(formObj);
						}else{
							// 无记录则添加一条
							formObj.put("create_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
							DbUp.upTable("pc_tv").dataInsert(formObj);
						}
					}
					
					acceptResult.setSuccessNum(acceptResult.getSuccessNum()+1);
				}
			} finally {
				if(StringUtils.isNotBlank(lockCode)){
					KvHelper.unLockCodes(lockCode, lockKey);
				}
			}

		}

		return acceptResult;
	}
	
	private String formatDate(String date){
		try {
			return DateFormatUtils.format(DateUtils.parseDate(date, "yyyy/MM/dd HH:mm"), "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private String md5(FuncTVFormRequest request){
		JsonHelper<IHServerRequest> jsonHelper = new JsonHelper<IHServerRequest>();
		return DigestUtils.md5Hex(jsonHelper.ObjToString(request));
	}
}
