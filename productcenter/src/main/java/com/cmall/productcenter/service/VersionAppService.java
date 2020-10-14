package com.cmall.productcenter.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.api.ApiVersionAppResult;
import com.cmall.systemcenter.util.AppVersionUtils;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * App版本控制  Service层
 * @author zhouguohui
 *
 */
public class VersionAppService extends BaseClass {
	/**
	 * 老版本版本控制嗲吗
	 * @param iosAndriod  手机APP型号
	 * @param versionCode 版本号
	 * @param versionApp  系统版本号
	 * @return
	 */
	public Map<String,Object> getVersionAppValuesOld(String iosAndriod,String versionCode,String versionApp){
		int  value = 0;
		int  tishiValue=0;
		Map<String,Object> map = new HashMap<String, Object>();
		ApiVersionAppResult result = new ApiVersionAppResult();
		MDataMap mapAppId=new MDataMap();
		mapAppId.inAllValues("app_id",versionCode);  //SI2003
		mapAppId.inAllValues("versin_id",versionApp); //V1....
		
		/**
		 * 首先查询APP设定的升级版本
		 */
		String sqlVersionsAapp = "select versions,minumum_versions,highest_versions,remind_counts from sc_versions_app  where app_id =:app_id  ORDER BY ZID DESC limit 1";
		Map<String,Object> mapListVersionsAapp = new HashMap<String,Object>();
		mapListVersionsAapp = DbUp.upTable("sc_versions_app").dataSqlOne(sqlVersionsAapp,mapAppId);
		String remindCounts = mapListVersionsAapp.get("remind_counts")==null?"": mapListVersionsAapp.get("remind_counts").toString();
		if("449748280001".equals(remindCounts)) {
			//首次启动提醒
			result.setRemindCounts("0");
		}else if("449748280002".equals(remindCounts)) {
			//每次启动提醒 
			result.setRemindCounts("1");
		}
		/**
		 * 查询App 版本历史信息
		 */
		String sSql = "select ifda,upgrade_select,url,upgrade_content,ios_url from sc_upgrade_app  where app_id =:app_id and  versin_id =:versin_id ORDER BY ZID DESC limit 1";
		Map<String,Object> mapList = new HashMap<String,Object>();
		mapList = DbUp.upTable("sc_upgrade_app").dataSqlOne(sSql, mapAppId);
		
		if((null==mapListVersionsAapp  || "".equals(mapListVersionsAapp))  && iosAndriod.trim().equals("1")){
			result.setUpgradeSelect("3");
			result.setAppVersion(versionApp);
			result.setAppUrl(mapList.get("ios_url").toString());
			result.setUpgradeContent(mapList.get("upgrade_content").toString());
			result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals(""))? "449746250002":mapList.get("ifda").toString());
			map.put("versionApp",result);
			return map;
		}else if((null==mapListVersionsAapp  || "".equals(mapListVersionsAapp)) && iosAndriod.trim().equals("2")){
			result.setUpgradeSelect("3");
			result.setAppVersion(versionApp);
			result.setAppUrl(mapList.get("url").toString());
			result.setUpgradeContent(mapList.get("upgrade_content").toString());
			result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
			map.put("versionApp",result);
			return map;
		}else{
			
			/**
			 * 复制App最高升级的版本号
			 */
			String  versionsAppNumber =mapListVersionsAapp.get("versions").toString();
			MDataMap mapUpgradeId=new MDataMap();
			mapUpgradeId.inAllValues("app_id",versionCode);
			mapUpgradeId.inAllValues("versin_id",versionsAppNumber);
			/**
			 * 查询App 最高版本升级地址内容以及方式
			 */
			String sSqlNew = "select ifda,upgrade_select,url,upgrade_content,ios_url from sc_upgrade_app  where app_id =:app_id and  versin_id =:versin_id ORDER BY ZID DESC limit 1";
			Map<String,Object> mapListNew = new HashMap<String,Object>();
			mapListNew = DbUp.upTable("sc_upgrade_app").dataSqlOne(sSqlNew, mapUpgradeId);
			
			
			if(iosAndriod.trim().equals("1")){
				if(result.upFlagTrue()){
					
					try {
					if(!mapListVersionsAapp.isEmpty()){
						
						/**
						 * 判断当前版本如果大于升级版本默认设定为静默
						 */
						if(compareAppVersion(versionApp,mapListVersionsAapp.get("versions").toString().trim())>=0){
							result.setUpgradeSelect("3");
							result.setAppVersion(mapListVersionsAapp.get("versions").toString());
							result.setAppUrl(mapListNew.get("ios_url").toString());
							result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals(""))  ? "449746250002":mapListNew.get("ifda").toString());
							map.put("versionApp",result);
						}else{
							
							/**
							 * 小于等于此版本的户端都要强制升级
							 */
							if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本必须更新
								 */
								result.setUpgradeSelect("1");
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								result.setAppUrl(mapListNew.get("ios_url").toString());
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								map.put("versionApp",result);
							}else if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)<0 && compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本需要判断
								 */
								tishiValue=2;
								if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
									value=Integer.parseInt("1");
								}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
									value=Integer.parseInt("2");
								}else{
									value=Integer.parseInt("3");
								}
								
								if(value-tishiValue==1){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==0){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==-1){
									result.setUpgradeSelect("1");
								}else{
									result.setUpgradeSelect("2");
								}
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								result.setAppUrl(mapListNew.get("ios_url").toString());
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								map.put("versionApp",result);
							}else{
								if(compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
									/***
									 * 当前版本需要判断
									 */
									tishiValue=3;
									if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
										value=Integer.parseInt("1");
									}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
										value=Integer.parseInt("2");
									}else{
										value=Integer.parseInt("4");
									}
									
									if(value-tishiValue==0){
										result.setUpgradeSelect("3");
									}else if(value-tishiValue==-1){
										result.setUpgradeSelect("2");
									}else if(value-tishiValue==-2){
										result.setUpgradeSelect("1");
									}else if(value-tishiValue==1){
										result.setUpgradeSelect("4");
									}else{
										result.setUpgradeSelect("3");
									}
									result.setAppVersion(mapListVersionsAapp.get("versions").toString());
									result.setAppUrl(mapListNew.get("ios_url").toString());
									result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
									map.put("versionApp",result);
								}else{
									/***
									 * 当前版本需要判断
									 */
									tishiValue=3;
									if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
										value=Integer.parseInt("1");
									}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
										value=Integer.parseInt("2");
									}else{
										value=Integer.parseInt("4");
									}
									
									if(value-tishiValue==0){
										result.setUpgradeSelect("3");
									}else if(value-tishiValue==-1){
										result.setUpgradeSelect("2");
									}else if(value-tishiValue==-2){
										result.setUpgradeSelect("1");
									}else if(value-tishiValue==1){
										result.setUpgradeSelect("4");
									}else{
										result.setUpgradeSelect("3");
									}
									result.setAppVersion(mapListVersionsAapp.get("versions").toString());
									result.setAppUrl(mapListNew.get("ios_url").toString());
									result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
									map.put("versionApp",result);
								}
							}
						}
						
					
					}
					
				} catch (Exception e) {
					result.setUpgradeSelect("0");
					result.setAppVersion("版本不存在，请核对版本信息");
					result.setAppUrl("");
					result.setUpgradeContent("");
					map.put("versionApp",result);
				}
				
			  }
				//判断手机版本的else
			}else{
				
				if(result.upFlagTrue()){
					
					try {
					
					if(!mapListVersionsAapp.isEmpty()){
						
						/**
						 * 判断当前版本如果大于升级版本默认设定为静默
						 */
						if(compareAppVersion(versionApp,mapListVersionsAapp.get("versions").toString().trim())>=0){
							result.setUpgradeSelect("3");
							result.setAppVersion(mapListVersionsAapp.get("versions").toString());
							result.setAppUrl(mapListNew.get("url").toString());
							result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							map.put("versionApp",result);
						}else{
						/**
						 * 小于等于此版本的户端都要强制升级
						 */
						if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)>=0){
							/***
							 * 判断当前版本号更新方式
							 */
							result.setUpgradeSelect("1");
							result.setAppVersion(mapListVersionsAapp.get("versions").toString());
							result.setAppUrl(mapListNew.get("url").toString());
							result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							map.put("versionApp",result);
						}else  if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)<0 && compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
							/***
							 * 当前版本需要判断
							 */
							tishiValue=2;
							if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
								value=Integer.parseInt("1");
							}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
								value=Integer.parseInt("2");
							}else{
								value=Integer.parseInt("3");
							}
							
							if(value-tishiValue==1){
								result.setUpgradeSelect("2");
							}else if(value-tishiValue==0){
								result.setUpgradeSelect("2");
							}else if(value-tishiValue==-1){
								result.setUpgradeSelect("1");
							}else{
								result.setUpgradeSelect("2");
							}
								
							result.setAppVersion(mapListVersionsAapp.get("versions").toString());
							result.setAppUrl(mapListNew.get("url").toString());
							result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							map.put("versionApp",result);
						}else{
						   if(compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本需要判断
								 */
							   tishiValue=3;
								if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
									value=Integer.parseInt("1");
								}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
									value=Integer.parseInt("2");
								}else{
									value=Integer.parseInt("4");
								}
								
								if(value-tishiValue==0){
									result.setUpgradeSelect("3");
								}else if(value-tishiValue==-1){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==-2){
									result.setUpgradeSelect("1");
								}else if(value-tishiValue==1){
									result.setUpgradeSelect("4");
								}else{
									result.setUpgradeSelect("3");
								}
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								result.setAppUrl(mapListNew.get("url").toString());
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								map.put("versionApp",result);
							}else{
								/***
								 * 当前版本需要判断
								 */
								tishiValue=3;
								if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
									value=Integer.parseInt("1");
								}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
									value=Integer.parseInt("2");
								}else{
									value=Integer.parseInt("4");
								}
								
								if(value-tishiValue==0){
									result.setUpgradeSelect("3");
								}else if(value-tishiValue==-1){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==-2){
									result.setUpgradeSelect("1");
								}else if(value-tishiValue==1){
									result.setUpgradeSelect("4");
								}else{
									result.setUpgradeSelect("3");
								}
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								result.setAppUrl(mapListNew.get("url").toString());
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								map.put("versionApp",result);
							}
						}
					  }	
					}
					
				} catch (Exception e) {
					result.setUpgradeSelect("0");
					result.setAppVersion("版本不存在，请核对版本信息");
					result.setAppUrl("");
					result.setUpgradeContent("");
					map.put("versionApp",result);
				}
				
			  }
			}
		}
		
		return map;
	}
	
	/**
	 *  新的版本控制代码
	 * @param serialNumber    流水号
	 * @param channelNumber   版本号
	 * @param iosAndriod      手机APP型号
	 * @param versionCode     版本号
	 * @param versionApp      系统版本号
	 * @return
	 */
	public Map<String,Object> getVersionAppValuesNew(String serialNumber,String channelNumber,String iosAndriod, String versionCode, String versionApp){
		int  value = 0;
		int  tishiValue=0;
		String url = "";
		Map<String,Object> map = new HashMap<String, Object>();
		ApiVersionAppResult result = new ApiVersionAppResult();
		
		MDataMap mapAppId=new MDataMap();
		mapAppId.inAllValues("app_id",versionCode); //si20..
		mapAppId.inAllValues("versin_id",versionApp);//V1.....
		
		/**
		 * 首先查询APP设定的升级版本
		 */
		String sqlVersionsAapp = "select versions,minumum_versions,highest_versions,remind_counts  from sc_versions_app  where app_id =:app_id ORDER BY ZID DESC limit 1";
		Map<String,Object> mapListVersionsAapp = DbUp.upTable("sc_versions_app").dataSqlOne(sqlVersionsAapp, mapAppId);
		String remindCounts = mapListVersionsAapp.get("remind_counts")==null?"": mapListVersionsAapp.get("remind_counts").toString();
		if("449748280001".equals(remindCounts)) {
			//首次启动提醒
			result.setRemindCounts("0");
		}else if("449748280002".equals(remindCounts)) {
			//每次启动提醒 
			result.setRemindCounts("1");
		}
		/**
		 * 查询App 版本历史信息
		 */
		String sSql = "select ifda,img_url,img_href_url,upgrade_select,url,upgrade_content,ios_url from sc_upgrade_app  where app_id =:app_id and  versin_id =:versin_id ORDER BY ZID DESC limit 1";
		Map<String,Object> mapList =  DbUp.upTable("sc_upgrade_app").dataSqlOne(sSql, mapAppId);
		
		String fileUrl=null;
		String zipUrl=null;
		String plugUrl=null;
		String mdFive=null;
		if((null!=mapListVersionsAapp  || !"".equals(mapListVersionsAapp))  &&iosAndriod.trim().equals("1")){
			String sqlFileUrl = "select down_url,zip_url from sc_versions_js_file where seller_code=:app_id and versions_app=:versin_id order by versions_code desc limit 1";
			Map<String,Object>  mapFileUrl = DbUp.upTable("sc_upgrade_app").dataSqlOne(sqlFileUrl, mapAppId);
			if(mapFileUrl!=null){
				if(mapFileUrl.get("down_url")!=null &&!mapFileUrl.get("down_url").equals("")){
					fileUrl=mapFileUrl.get("down_url").toString();
				}
				if(mapFileUrl.get("zip_url")!=null &&!mapFileUrl.get("zip_url").equals("")){
					zipUrl=mapFileUrl.get("zip_url").toString();
				}
			}
		}else if((null!=mapListVersionsAapp  || !"".equals(mapListVersionsAapp))  &&iosAndriod.trim().equals("2")){
			String sqlFileUrl = "select plug_url,md_five from sc_versions_js_file where seller_code=:app_id and versions_app=:versin_id order by versions_code desc limit 1";
			Map<String,Object>  mapFileUrl = DbUp.upTable("sc_upgrade_app").dataSqlOne(sqlFileUrl, mapAppId);
			if(mapFileUrl!=null){
				if(mapFileUrl.get("plug_url")!=null &&!mapFileUrl.get("plug_url").equals("")){
					plugUrl=mapFileUrl.get("plug_url").toString();
				}
				if(mapFileUrl.get("md_five")!=null &&!mapFileUrl.get("md_five").equals("")){
					mdFive=mapFileUrl.get("md_five").toString();
				}
			}
			
		}
		
		if((null==mapListVersionsAapp  || "".equals(mapListVersionsAapp))  && iosAndriod.trim().equals("1")){
			if(!StringUtils.isEmpty(mapList.get("ios_url")+"")){
				result.setUpgradeSelect("3");
				result.setAppVersion(versionApp);
				
				//判断渠道号
				if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
					url=mapList.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
				}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
					url=mapList.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
				}else{
					url=mapList.get("ios_url").toString();
				}
				result.setImgUrl((mapList.get("img_url")==null || mapList.get("img_url").equals(""))?"":mapList.get("img_url").toString() );
				result.setImgHrefUrl((mapList.get("img_href_url")==null || mapList.get("img_href_url").equals(""))?"":mapList.get("img_href_url").toString() );
				result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
				result.setFileUrl(fileUrl);
				result.setZipUrl(zipUrl);
				result.setAppUrl(url);
				result.setUpgradeContent(mapList.get("upgrade_content").toString());
			}else{
				result.setUpgradeSelect("3");
				result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
				result.setImgUrl((mapList.get("img_url")==null || mapList.get("img_url").equals(""))?"":mapList.get("img_url").toString() );
				result.setImgHrefUrl((mapList.get("img_href_url")==null || mapList.get("img_href_url").equals(""))?"":mapList.get("img_href_url").toString() );
				result.setFileUrl(fileUrl);
				result.setZipUrl(zipUrl);
			}
			
			map.put("versionApp",result);
			return map;
		}else if((null==mapListVersionsAapp  || "".equals(mapListVersionsAapp)) && iosAndriod.trim().equals("2")){
			if(!StringUtils.isEmpty(mapList.get("url")+"")){
				result.setUpgradeSelect("3");
				result.setAppVersion(versionApp);
				
				//判断渠道号
				if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
					url=mapList.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
				}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
					url=mapList.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
				}else{
					url=mapList.get("url").toString();
				}
				result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
				result.setAppUrl(url);
				result.setUpgradeContent(mapList.get("upgrade_content").toString());
				result.setPlugUrl(plugUrl);
				result.setMdFive(mdFive);
				
				
			}else{
				result.setUpgradeSelect("3");
				result.setPlugUrl(plugUrl);
				result.setMdFive(mdFive);
				result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
			}
			map.put("versionApp",result);
			return map;
		}else{
			
			/**
			 * 复制App最高升级的版本号
			 */
			String  versionsAppNumber =mapListVersionsAapp.get("versions").toString();
			
			MDataMap mapUpgradeId=new MDataMap();
			mapUpgradeId.inAllValues("app_id",versionCode);
			mapUpgradeId.inAllValues("versin_id",versionsAppNumber);
			
			/**
			 * 查询App 最高版本升级地址内容以及方式
			 */
			String sSqlNew = "select ifda,img_url,img_href_url,upgrade_select,url,upgrade_content,ios_url from sc_upgrade_app  where app_id =:app_id and  versin_id =:versin_id ORDER BY ZID DESC limit 1";
			Map<String,Object> mapListNew = new HashMap<String,Object>();
			mapListNew = DbUp.upTable("sc_upgrade_app").dataSqlOne(sSqlNew, mapUpgradeId);
			
			
			if(iosAndriod.trim().equals("1")){
				if(result.upFlagTrue()){
					
					try {
					if(!mapListVersionsAapp.isEmpty()){
						
						/**
						 * 判断当前版本如果大于升级版本默认设定为静默
						 */
						if(compareAppVersion(versionApp,mapListVersionsAapp.get("versions").toString().trim())>=0){
							if(!StringUtils.isEmpty(mapListNew.get("ios_url")+"")){
								result.setUpgradeSelect("3");
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("ios_url").toString();
								}
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
								result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
								result.setAppUrl(url);
								result.setFileUrl(fileUrl);
								result.setZipUrl(zipUrl);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							}else{
								result.setUpgradeSelect("3");
								result.setFileUrl(fileUrl);
								result.setZipUrl(zipUrl);
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
								result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
							}
							
							map.put("versionApp",result);
						}else{
							
							/**
							 * 小于等于此版本的户端都要强制升级
							 */
							if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本必须更新
								 */
								if(!StringUtils.isEmpty(mapListNew.get("ios_url")+"")){
								result.setUpgradeSelect("1");
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("ios_url").toString();
								}
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
								result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
								result.setAppUrl(url);
								result.setFileUrl(fileUrl);
								result.setZipUrl(zipUrl);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								}else{
								result.setUpgradeSelect("3");
								result.setFileUrl(fileUrl);
								result.setZipUrl(zipUrl);
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
								result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
								}
								map.put("versionApp",result);
							}else if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)<0 && compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本需要判断
								 */
								if(!StringUtils.isEmpty(mapListNew.get("ios_url")+"")){
								tishiValue=2;
								if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
									value=Integer.parseInt("1");
								}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
									value=Integer.parseInt("2");
								}else{
									value=Integer.parseInt("3");
								}
								
								if(value-tishiValue==1){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==0){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==-1){
									result.setUpgradeSelect("1");
								}else{
									result.setUpgradeSelect("2");
								}
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("ios_url").toString();
								}
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
								result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
								result.setAppUrl(url);
								result.setFileUrl(fileUrl);
								result.setZipUrl(zipUrl);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								}else{
									result.setUpgradeSelect("3");
									result.setFileUrl(fileUrl);
									result.setZipUrl(zipUrl);
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
									result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
									result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
								}
								map.put("versionApp",result);
							}else{
								if(compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
									/***
									 * 当前版本需要判断
									 */
									if(!StringUtils.isEmpty(mapListNew.get("ios_url")+"")){
										tishiValue=3;
										if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
											value=Integer.parseInt("1");
										}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
											value=Integer.parseInt("2");
										}else{
											value=Integer.parseInt("4");
										}
										
										if(value-tishiValue==0){
											result.setUpgradeSelect("3");
										}else if(value-tishiValue==-1){
											result.setUpgradeSelect("2");
										}else if(value-tishiValue==-2){
											result.setUpgradeSelect("1");
										}else if(value-tishiValue==1){
											result.setUpgradeSelect("4");
										}else{
											result.setUpgradeSelect("3");
										}
										result.setAppVersion(mapListVersionsAapp.get("versions").toString());
										
										//判断渠道号
										if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
											url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
										}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
											url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
										}else{
											url=mapListNew.get("ios_url").toString();
										}
										result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
										result.setImgUrl((mapListNew.get("img_url")==null || mapList.get("img_url").equals(""))?"":mapList.get("img_url").toString() );
										result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapList.get("img_href_url").equals(""))?"":mapList.get("img_href_url").toString() );
										result.setAppUrl(url);
										result.setFileUrl(fileUrl);
										result.setZipUrl(zipUrl);
										result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
									}else{
										result.setUpgradeSelect("3");
										result.setFileUrl(fileUrl);
										result.setZipUrl(zipUrl);
										result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
										result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
										result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
									}
									
									map.put("versionApp",result);
								}else{
									/***
									 * 当前版本需要判断
									 */
									if(!StringUtils.isEmpty(mapListNew.get("ios_url")+"")){
										tishiValue=3;
										if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
											value=Integer.parseInt("1");
										}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
											value=Integer.parseInt("2");
										}else{
											value=Integer.parseInt("4");
										}
										
										if(value-tishiValue==0){
											result.setUpgradeSelect("3");
										}else if(value-tishiValue==-1){
											result.setUpgradeSelect("2");
										}else if(value-tishiValue==-2){
											result.setUpgradeSelect("1");
										}else if(value-tishiValue==1){
											result.setUpgradeSelect("4");
										}else{
											result.setUpgradeSelect("3");
										}
										result.setAppVersion(mapListVersionsAapp.get("versions").toString());
										
										//判断渠道号
										if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
											url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
										}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
											url=mapListNew.get("ios_url").toString().replace(".apk","_"+channelNumber+".apk" );
										}else{
											url=mapListNew.get("ios_url").toString();
										}
										result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
										result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
										result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
										result.setAppUrl(url);
										result.setFileUrl(fileUrl);
										result.setZipUrl(zipUrl);
										result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
									}else{
										result.setUpgradeSelect("3");
										result.setFileUrl(fileUrl);
										result.setZipUrl(zipUrl);
										result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
										result.setImgUrl((mapListNew.get("img_url")==null || mapListNew.get("img_url").equals(""))?"":mapListNew.get("img_url").toString() );
										result.setImgHrefUrl((mapListNew.get("img_href_url")==null || mapListNew.get("img_href_url").equals(""))?"":mapListNew.get("img_href_url").toString() );
									}
									
									map.put("versionApp",result);
								}
							}
						}
						
					
					}
					
				} catch (Exception e) {
					result.setUpgradeSelect("0");
					result.setAppVersion("版本不存在，请核对版本信息");
					result.setAppUrl("");
					result.setUpgradeContent("");
					map.put("versionApp",result);
				}
				
			  }
				//判断手机版本的else  Andriod
			}else{
				
				if(result.upFlagTrue()){
					
					try {
					
					if(!mapListVersionsAapp.isEmpty()){
						
						/**
						 * 判断当前版本如果大于升级版本默认设定为静默
						 */
						if(compareAppVersion(versionApp,mapListVersionsAapp.get("versions").toString().trim())>=0){
							if(!StringUtils.isEmpty(mapListNew.get("url")+"")){
								result.setUpgradeSelect("3");
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("url").toString();
								}
								
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setAppUrl(url);
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							}else{
								result.setUpgradeSelect("3");
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							}
							
							map.put("versionApp",result);
						}else{
						/**
						 * 小于等于此版本的户端都要强制升级
						 */
						if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)>=0){
							/***
							 * 判断当前版本号更新方式
							 */
							if(!StringUtils.isEmpty(mapListNew.get("url")+"")){
								result.setUpgradeSelect("1");
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("url").toString();
								}
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setAppUrl(url);
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							}else{
								result.setUpgradeSelect("3");
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							}
							
							map.put("versionApp",result);
						}else  if(compareAppVersion(mapListVersionsAapp.get("minumum_versions").toString().trim(),versionApp)<0 && compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
							/***
							 * 当前版本需要判断
							 */
							if(!StringUtils.isEmpty(mapListNew.get("url")+"")){
								tishiValue=2;
								if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
									value=Integer.parseInt("1");
								}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
									value=Integer.parseInt("2");
								}else{
									value=Integer.parseInt("3");
								}
								
								if(value-tishiValue==1){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==0){
									result.setUpgradeSelect("2");
								}else if(value-tishiValue==-1){
									result.setUpgradeSelect("1");
								}else{
									result.setUpgradeSelect("2");
								}
									
								result.setAppVersion(mapListVersionsAapp.get("versions").toString());
								
								
								//判断渠道号
								if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
									url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
								}else{
									url=mapListNew.get("url").toString();
								}
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								result.setAppUrl(url);
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							}else{
								result.setUpgradeSelect("3");
								result.setPlugUrl(plugUrl);
								result.setMdFive(mdFive);
								result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							}
							
							map.put("versionApp",result);
						}else{
						   if(compareAppVersion(mapListVersionsAapp.get("highest_versions").toString().trim(),versionApp)>=0){
								/***
								 * 当前版本需要判断
								 */
							   if(!StringUtils.isEmpty(mapListNew.get("url")+"")){
								   tishiValue=3;
									if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
										value=Integer.parseInt("1");
									}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
										value=Integer.parseInt("2");
									}else{
										value=Integer.parseInt("4");
									}
									
									if(value-tishiValue==0){
										result.setUpgradeSelect("3");
									}else if(value-tishiValue==-1){
										result.setUpgradeSelect("2");
									}else if(value-tishiValue==-2){
										result.setUpgradeSelect("1");
									}else if(value-tishiValue==1){
										result.setUpgradeSelect("4");
									}else{
										result.setUpgradeSelect("3");
									}
									result.setAppVersion(mapListVersionsAapp.get("versions").toString());
									
									
									//判断渠道号
									if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
										url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
									}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
										url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
									}else{
										url=mapListNew.get("url").toString();
									}
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
									result.setAppUrl(url);
									result.setPlugUrl(plugUrl);
									result.setMdFive(mdFive);
									result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
							   }else{
								    result.setUpgradeSelect("3");
								    result.setPlugUrl(plugUrl);
								    result.setMdFive(mdFive);
								    result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
							   }
							  
								map.put("versionApp",result);
							}else{
								/***
								 * 当前版本需要判断
								 */
								if(!StringUtils.isEmpty(mapListNew.get("url")+"")){
									tishiValue=3;
									if(mapList.get("upgrade_select").toString().trim().equals("449746840001")){
										value=Integer.parseInt("1");
									}else if(mapList.get("upgrade_select").toString().trim().equals("449746840002")){
										value=Integer.parseInt("2");
									}else{
										value=Integer.parseInt("4");
									}
									
									if(value-tishiValue==0){
										result.setUpgradeSelect("3");
									}else if(value-tishiValue==-1){
										result.setUpgradeSelect("2");
									}else if(value-tishiValue==-2){
										result.setUpgradeSelect("1");
									}else if(value-tishiValue==1){
										result.setUpgradeSelect("4");
									}else{
										result.setUpgradeSelect("3");
									}
									result.setAppVersion(mapListVersionsAapp.get("versions").toString());
									

									//判断渠道号
									if(!StringUtils.isEmpty(channelNumber) && !"channel".equals(channelNumber)){
										url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
									}else if(!StringUtils.isEmpty(channelNumber) && !StringUtils.isEmpty(serialNumber) && !"channel".equals(channelNumber)){
										url=mapListNew.get("url").toString().replace(".apk","_"+channelNumber+".apk" );
									}else{
										url=mapListNew.get("url").toString();
									}
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
									result.setAppUrl(url);
									result.setPlugUrl(plugUrl);
									result.setMdFive(mdFive);
									result.setUpgradeContent(mapListNew.get("upgrade_content").toString());
								}else{
									result.setUpgradeSelect("3");
									result.setPlugUrl(plugUrl);
									result.setMdFive(mdFive);
									result.setIfda((mapListNew.get("ifda")==null || mapListNew.get("ifda").equals("")) ? "449746250002":mapListNew.get("ifda").toString());
								}
								
								map.put("versionApp",result);
							}
						}
					  }	
					}
					
				} catch (Exception e) {
					result.setUpgradeSelect("0");
					result.setAppVersion("版本不存在，请核对版本信息");
					result.setAppUrl("");
					result.setUpgradeContent("");
					//Zht write directly
					result.setResultCode(-1);
					map.put("versionApp",result);
				}
				
			  }
			}
		}
		
		
		//最终修正by liudp   idfa只与当前版本号有关  与其他无关
		if(mapList!=null)
		{
			result.setIfda((mapList.get("ifda")==null || mapList.get("ifda").equals("")) ? "449746250002":mapList.get("ifda").toString());
		}
		
		return map;
	}
	
	/**
	 * 对比版本
	 * appVersion > compareVersion   返回正数
	 * appVersion = compareVersion   返回0
	 * appVersion < compareVersion   返回负数
	 * @param appVersion
	 * @param compareVersion
	 */
	public int compareAppVersion (String appVersion ,String compareVersion) {
		appVersion = StringUtils.trimToEmpty(appVersion).toLowerCase().replaceFirst("v", "");
		compareVersion = StringUtils.trimToEmpty(compareVersion).toLowerCase().replaceFirst("v", "");
		return AppVersionUtils.compareTo(appVersion, compareVersion);
	}
}
