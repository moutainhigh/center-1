package com.cmall.groupcenter.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 *初始化LD系统虚拟商品迁移至本系统商户下json为空值 
 */
public class JobForProductJson extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		List<MDataMap> li = DbUp.upTable("pc_product_problem").queryAll("", "", "", new MDataMap());
		if(li!=null&&!li.isEmpty()){
			for (int i = 0; i < li.size(); i++) {
				String product_code = li.get(i).get("product_code");
				if(StringUtils.isNotBlank(product_code)){
					DbUp.upTable("pc_productflow").insert("flow_code",WebHelper.upCode("PF"),"product_code",product_code,
							"product_json",new ProductService().upProductInfoJson(product_code),
							"flow_status","22","create_time",DateUtil.getSysDateTimeString(),"update_time",DateUtil.getSysDateTimeString(),
							"creator","system_problem","updator","system_problem"
							);
					DbUp.upTable("sc_flow_main").insert("flow_code",WebHelper.upCode("SF"),"flow_type","449717230011",
							"creator","system_problem","updator","system_problem",
							"create_time",DateUtil.getSysDateTimeString(),"update_time",DateUtil.getSysDateTimeString(),
							"outer_code",product_code,"flow_title",product_code,
							"flow_url","page_preview_v_pc_productDetailInfo?zw_f_product_code="+product_code+"_1",
							"flow_remark","补全LD虚拟商品迁移至本商户系统所缺少的审批流程数据","flow_isend","1","current_status","4497153900060008");
					DbUp.upTable("pc_product_problem").delete("uid",li.get(i).get("uid"));
				}
			}
		}
	}
}
