package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商品审批业务实现
 * 
 * @author pang_jhui
 *
 */
public class ProductFlowService extends BaseClass {

	/**
	 * 根据角色编码获取用户列表
	 * 
	 * @param roleCode
	 *            角色编码
	 * @return List<MDataMap> 用户信息列表
	 */
	public List<MDataMap> getUserInfoList(String roleCode) {

		List<MDataMap> userRoleRelList = DbUp.upTable("za_userrole").queryAll("user_code", "", "",
				new MDataMap("role_code", roleCode));

		List<String> values = new ArrayList<String>();

		for (MDataMap mDataMap : userRoleRelList) {

			values.add(mDataMap.get("user_code"));

		}

		String sFieldValue = StringUtils.join(values, ",");

		if (StringUtils.isNotEmpty(sFieldValue)) {
			// 增加flag_enable='1'条件。已经冻结的账户不显示
			return DbUp.upTable("za_userinfo").queryInSafe("user_code,real_name", "", "flag_enable='1'", null, -1, -1,
					"user_code", sFieldValue);

		} else {

			return null;

		}

	}

	/**
	 * 
	 * 方法: getFlowErrorMsg <br>
	 * 描述: 获取审核未通过的商品的驳回信息 <br>
	 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
	 * 时间: 2016年7月8日 下午4:56:38
	 * 
	 * @return
	 */
	public String getFlowErrorMsg(String productCode) {
		String msg = "";
		MDataMap map = new MDataMap();
		map.put("product_code", productCode);
		Map<String, Object> result = DbUp.upTable("v_sc_flow_history_pa").dataSqlOne(
				"select flow_remark from v_sc_flow_history_pa where product_code = :product_code and current_status in('4497172300160006','4497172300160007','4497172300160008','4497172300160009','4497172300160017')order by create_time desc",
				map);
		if (result != null && result.get("flow_remark") != null && !"".equals(result.get("flow_remark").toString())) {
			msg = result.get("flow_remark").toString();
		}
		return msg;
	}
	
	public static void main(String[] args) {
		String productCode = "8016411611";
		String msg = new ProductFlowService().getFlowErrorMsg(productCode);
		System.out.println(msg);
	}

}
