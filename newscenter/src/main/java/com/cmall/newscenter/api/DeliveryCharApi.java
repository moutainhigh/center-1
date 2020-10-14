package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.AgentEntityClass;
import com.cmall.newscenter.model.DeliveryCharInput;
import com.cmall.newscenter.model.DeliveryCharResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 查看发货信息
 * 
 * @author shiyz date 2016-03-18
 * 
 */
public class DeliveryCharApi extends
		RootApiForToken<DeliveryCharResult, DeliveryCharInput> {

	public DeliveryCharResult Process(DeliveryCharInput inputParam,
			MDataMap mRequestMap) {

		DeliveryCharResult result = new DeliveryCharResult();

		if (result.upFlagTrue()) {

			MDataMap agentmap = new MDataMap();

			agentmap.put("delivery_name", inputParam.getSearch());

			agentmap.put("phone_number", inputParam.getSearch());

			agentmap.put("wx_number", inputParam.getSearch());

			List<AgentEntityClass> agentEntity = new ArrayList<AgentEntityClass>();

			MPageData mPageData = new MPageData();

			if (inputParam.getSearch() != null && !inputParam
					.getSearch().isEmpty()) {
				mPageData = DataPaging
						.upPageData(
								"nc_delivery",
								"",
								"delivery_name=:delivery_name or"
										+ " phone_number=:phone_number or wx_number=:wx_number",
								"-delivery_time", agentmap,
								inputParam.getPaging());
			} else {

				mPageData = DataPaging.upPageData("nc_delivery", "", "",
						"-delivery_time", new MDataMap(), inputParam.getPaging());
			}

			for (MDataMap mDataMap : mPageData.getListData()) {

				AgentEntityClass agent = new AgentEntityClass();

				agent.setAddress(new MemberUtil().addressName(mDataMap
						.get("receipt_address")));

				agent.setAgent_level(new MemberUtil().Agent_grade(mDataMap
						.get("delivery_grade")));

				agent.setAgent_name(mDataMap.get("delivery_name"));

				agent.setAgent_phone(mDataMap.get("phone_number"));

				agent.setAgent_wchat(mDataMap.get("wx_number"));

				agent.setExpress_company(mDataMap.get("express_company"));

				agent.setExpress_number(mDataMap.get("express_number"));

				agent.setProduct_name(new MemberUtil().product_name(mDataMap
						.get("delivery_no")));

				agent.setProduct_num(Integer.valueOf(mDataMap
						.get("delivery_number")));

				agentEntity.add(agent);

			}

			result.setAgentEntity(agentEntity);
			result.setPaged(mPageData.getPageResults());
		}

		return result;
	}

}
