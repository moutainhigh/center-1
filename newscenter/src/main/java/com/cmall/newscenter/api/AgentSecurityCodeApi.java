package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

import com.cmall.newscenter.model.GetSecurityCodeInput;
import com.cmall.newscenter.model.GetSecurityCodeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 验证防伪码真伪
 * 
 * @author shiyz date 2014-09-20
 * 
 */
public class AgentSecurityCodeApi extends
     RootApiForManage<GetSecurityCodeResult, GetSecurityCodeInput> {

	public GetSecurityCodeResult Process(GetSecurityCodeInput inputParam,
			MDataMap mRequestMap) {

		GetSecurityCodeResult result = new GetSecurityCodeResult();

		if (result.upFlagTrue()) {

			String securityCode = "";
			//inputParam.getSecurityCode();

			MDataMap mDataMap = new MDataMap();

			MDataMap mWhereMap = new MDataMap();

			List<String> list = new ArrayList<String>();
			// 扫描的二维码
			StringTokenizer st = new StringTokenizer(inputParam.getSecurityCode(), ",");

			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			
			List<MDataMap> listmMaps = new ArrayList<MDataMap>();
			
			int secNum = 0;

				
                if(list.size()!=0){
                	for (int i = 0; i < list.size(); i++) {
						
                		securityCode = list.get(i);
                		
                		mWhereMap.put("securityx_code", securityCode);
                		
                		int num = securityCode.indexOf("type=");
        				// 截取字符串查看是否是盒还是箱
        				String agentCode = securityCode.substring(num + 5);
                		
        				
        				if (!StringUtils.isEmpty(agentCode)
        						&& agentCode.equals("xiang")) {

        					mDataMap = DbUp.upTable("nc_agent_details").one(
        							"securityx_code", list.get(i),
        							"securityh_code", "");

        					if (mDataMap != null && !mDataMap.isEmpty()) {
        						listmMaps = DbUp
        								.upTable("nc_agent_details")
        								.queryAll(
        										"",
        										"",
        										"securityx_code=:securityx_code and securityh_code!='' ",
        										mWhereMap);
        						result.setSecrityProduct(mDataMap
        								.get("security_productname"));
        						result.setTestResult(1);
        						secNum = secNum+listmMaps.size();

        					}else {
								
        						result.setResultCode(934205172);
        						result.setResultMessage(bInfo(934205172));
        						return result;
							}

        				} else if (!StringUtils.isEmpty(agentCode)
        						&& agentCode.equals("he")) {

        					mDataMap = DbUp.upTable("nc_agent_details").one(
        							"securityh_code", list.get(i));

        					if (mDataMap != null && !mDataMap.isEmpty()) {

        						result.setSecrityProduct(mDataMap
        								.get("security_productname"));
        						result.setTestResult(1);

        						secNum= secNum+1;
        					}else {
								
        						result.setResultCode(934205172);
        						result.setResultMessage(bInfo(934205172));
        						return result;
							}
        					
        				} else {

        				}

					}
                	result.setNum(secNum);
                	
                }
				
			}

		return result;
	}

}
