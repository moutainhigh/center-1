package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.Boutique;
import com.cmall.productcenter.model.BoutiqueInput;
import com.cmall.productcenter.model.BoutiqueResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiBoutique extends RootApi<BoutiqueResult,BoutiqueInput>{

	public BoutiqueResult Process(BoutiqueInput inputParam, MDataMap mRequestMap) {
		BoutiqueResult result = new BoutiqueResult();
		List<String> code = inputParam.getBoutique_code();
		List<Boutique> lisBoutiques = new ArrayList<Boutique>();
		Boutique boutique  = new Boutique();
		String nowTime = FormatHelper.upDateTime();
		String sql = "";
		if(code.isEmpty())
		{
			sql = "select * from oc_boutique_market where  start_time < '"+ nowTime  +"' and end_time >'" +nowTime +"' order by start_time desc" ;
			List<Map<String, Object>> list = null;
			try {
				list = DbUp.upTable("oc_boutique_market").dataSqlList(sql, new MDataMap());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				result.setResultCode(941901037);
				result.setResultMessage(bInfo(941901037));
				return result;
			}
			for(Map<String, Object> mp1 :list)
			{
				boutique = new SerializeSupport<Boutique>().serialize(new MDataMap(mp1),new Boutique());
				lisBoutiques.add(boutique);
			}
			result.setList(lisBoutiques);
			
		}
		else
		{
			 sql = "select * from oc_boutique_market where "
					+" start_time < '"+ nowTime  +"' and end_time >'" +nowTime +"'" 
					+ " and boutique_code in " + inputParam.getBoutique_code().toString().replace("[", "(").replace("]", ")")
					+ "order by start_time desc";
			List<Map<String, Object>> list = null;
			try {
				list = DbUp.upTable("oc_boutique_market").dataSqlList(sql, new MDataMap());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				result.setResultCode(941901037);
				result.setResultMessage(bInfo(941901037));
				return result;
			}
			for(Map<String, Object> mp1 :list)
			{
				boutique = new SerializeSupport<Boutique>().serialize(new MDataMap(mp1),new Boutique());
				lisBoutiques.add(boutique);
			}
			result.setList(lisBoutiques);
		}
		return result;
	}
}
