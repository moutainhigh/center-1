package com.cmall.newscenter.beauty.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cmall.newscenter.beauty.model.AddCosmeticBagResult;
import com.cmall.newscenter.beauty.model.AddCosmeticBagInput;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—保存与修改化妆包中的妆品Api（传入妆品编码为空的话是新增   不为空是修改）
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class AddCosmeticBagApi extends RootApiForToken<AddCosmeticBagResult, AddCosmeticBagInput> {

	public AddCosmeticBagResult Process(AddCosmeticBagInput inputParam,MDataMap mRequestMap) {

		AddCosmeticBagResult result = new AddCosmeticBagResult();

		// 设置相关信息
		if (result.upFlagTrue()) {
			
			if(inputParam.getCosmetic_code().equals("")){
				
				MDataMap mDataMap = new MDataMap();
				
				String img = "";
				
				String status = "";
				
				//根据失效时间来判断化妆品状态  （当前时间大于失效时间=已过期  否则=即将过期）
				if(!inputParam.getDisabled_time().equals("")){
					
					int days= 0;
					try {
						//计算失效日期距离今天有多少天
						days = AddCosmeticBagApi.time(inputParam.getDisabled_time());
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					int flag = DateUtil.getSysDateString().compareTo(inputParam.getDisabled_time());
					
					if(flag>0){
						
						status = "449747120002";              //已过期
						
					}else{
						//失效日期小于等于半年  为即将过期  否则没状态
						if(days<=180){
							
							status = "449747120001";              //即将过期
						}
					}
					
				}
				
				List<String> photos  = inputParam.getPhoto();
				
				if(photos!=null && photos.size()!=0){
					
					for (int i = 0; i < photos.size(); i++) {
						
						img = img + photos.get(i) +",";
					}
					
				}
				
				if(!img.equals("")){
					
					img = img.substring(0, img.length()-1);
				}
				
				mDataMap.inAllValues("cosmetic_code", WebHelper.upCode("AS"),"member_code",getUserCode(),"cosmetic_name",inputParam.getCosmetic_name(),"cosmetic_price",inputParam.getCosmetic_price(),"disabled_time",inputParam.getDisabled_time(),"count",inputParam.getCount(),"iswarn",inputParam.getIswarn(),"warn_time",inputParam.getWarn_time(),"photo",img,"status",status,"update_time",DateUtil.getSysDateTimeString(),"remark",inputParam.getRemark(),"unit",inputParam.getUnit());
				
				DbUp.upTable("nc_cosmetic_bag").dataInsert(mDataMap);
				
			}else{
				
				String cosmetic_code = inputParam.getCosmetic_code();
				
				MDataMap dataMap = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code",cosmetic_code);
				
				if(dataMap!=null){
					
					String img = "";
					
					String status = "";
					
					//根据失效时间来判断化妆品状态  （当前时间大于失效时间=已过期  否则=即将过期）
					if(!inputParam.getDisabled_time().equals("")){
						
						int days= 0;
						try {
							//计算失效日期距离今天有多少天
							days = AddCosmeticBagApi.time(inputParam.getDisabled_time());
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						int flag = DateUtil.getSysDateString().compareTo(inputParam.getDisabled_time());
						
						if(flag>0){
							
							status = "449747120002";              //已过期
							
						}else{
							
							//失效日期小于等于半年  为即将过期  否则没状态
							if(days<=180){
								
								status = "449747120001";              //即将过期
							}
						}
						
					}
					
					List<String> photos  = inputParam.getPhoto();
					
					if(photos!=null && photos.size()!=0){
						
						for (int i = 0; i < photos.size(); i++) {
							
							img = img + photos.get(i) +",";
						}
						
					}
					
					if(!img.equals("")){
						
						img = img.substring(0, img.length()-1);
					}
					
					dataMap.inAllValues("member_code",getUserCode(),"cosmetic_name",inputParam.getCosmetic_name(),"cosmetic_price",inputParam.getCosmetic_price(),"disabled_time",inputParam.getDisabled_time(),"count",inputParam.getCount(),"iswarn",inputParam.getIswarn(),"warn_time",inputParam.getWarn_time(),"photo",img,"status",status,"update_time",DateUtil.getSysDateTimeString(),"remark",inputParam.getRemark(),"unit",inputParam.getUnit());
					
					DbUp.upTable("nc_cosmetic_bag").update(dataMap);
				}
			}

		}
		return result;
	}
	
	    //计算某一天距离今天有多少天
		public static int time(String disabled_time) throws ParseException {
			
			Date date = new Date();
			long a = date.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long b = sdf.parse(disabled_time).getTime();
			int success = (int) ((b-a)/(1000*60*60*24));  //1000毫秒*60分钟*60秒*24小时 = 天
			return success;
			
		}

}

