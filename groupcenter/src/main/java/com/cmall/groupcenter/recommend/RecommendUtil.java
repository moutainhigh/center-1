package com.cmall.groupcenter.recommend;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmall.groupcenter.accountmarketing.util.LongShortUtil;
import com.cmall.groupcenter.util.CalendarHelper;
import com.cmall.systemcenter.message.SendMessageBase;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

public class RecommendUtil extends BaseClass{

	/**
	 * <br>fq</br>
	 * 发送推荐连接
	 * @param vipuser_mobile
	 * @param tels
	 * @param flag
	 * @return
	 */
	public Map<String, List<String>> sendLink(String vipuser_mobile, String tels ,String flag ) {
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String[] recommendTelArr = tels.split(",");
		List<String> error_list = new ArrayList<String>();
		List<String> success_list = new ArrayList<String>();
		List<String> much_list = new ArrayList<String>();

		SendMessageBase messageBase=new SendMessageBase();
		String app_code = "";
		for (int i = 0; i < recommendTelArr.length; i++) {
			String sendLink = "";
			String recommendTel = recommendTelArr[i];
			String uid = new SimpleDateFormat("MMddHHmmss").format(new Date()) + getRandom(7);
			String sBaseString="abcdefghijklmnopqrstuvwxyz0123456789";
			String convert = convertFormatNumberBack(uid, sBaseString);
			
			//校验开始---------
			//11位手机号
			 String regEx1 = "^1[0-9]{10}$"; 
			 boolean isValid = true;//校验是否陈宫
			 Pattern p1 = Pattern.compile(regEx1); 
			 
			Matcher m1 = p1.matcher(recommendTel); 
			boolean rs1 = m1.matches(); 
			 if(rs1 && !vipuser_mobile.equals(recommendTel)){//11为手机号校验成功
				 MDataMap mWhereMap = new  MDataMap();
				 
				 mWhereMap.put("login_name", recommendTel);
				 
				//判断是否已注册
				int count = DbUp.upTable("mc_login_info").dataCount(null, mWhereMap);
				
				if(count>0){
					
					isValid=false;
					
				}else{
					String today = CalendarHelper.Date2String(new Date(),"yyyy-MM-dd");
					
					Date tomorrowDate = CalendarHelper.getTomorrowDay(new Date());
					
					String tomorrow = CalendarHelper.Date2String(tomorrowDate);
					
					//检查一天是否超过了三次
					MDataMap mWhereRecommendMap = new  MDataMap();
					mWhereRecommendMap.put("recommended_mobile", recommendTel);
					mWhereRecommendMap.put("mobile", vipuser_mobile);
					mWhereRecommendMap.put("today", today);
					mWhereRecommendMap.put("tomorrow", tomorrow);
					
					
//					DbUp.upTable("gc_recommend_info").dataCount(" AND recommend_time>='"+today+"' ", mWhereRecommendMap);
					
					String sql = " SELECT zid FROM groupcenter.gc_recommend_info " +
							" WHERE recommended_mobile=:recommended_mobile " +
							" AND mobile =:mobile " +
							" AND recommend_time>=:today " +
							" AND recommend_time<:tomorrow ";
					
					List<Map<String, Object>> beenSended = DbUp.upTable("gc_recommend_info").dataSqlList(sql, mWhereRecommendMap);
					
					if(beenSended!=null && beenSended.size()>=3){
						isValid=false;
						much_list.add(recommendTel);
					}
					
				}
				
			 }else{
				 isValid=false;
			 }
			
			//校验结束---------
			
			 
			 if(isValid){
				 /*
				  * 判断发送的连接通路
				  * SI2001	刘嘉玲APP
				  * SI2005	约她
				  * SI2003	惠家有
				  * SI2007	惠美丽
				  * SI2009	家有汇
				  * SI3003     沙皮狗
				  */
				 String link = bConfig("groupcenter.app_downLoadPageUrl");
				 String linkgroup = bConfig("groupcenter.app_recommendPageUrl");
				 if(flag.equals("SI2007")) {
					 app_code = "SI2007";
					 sendLink = "点击链接 "+new LongShortUtil().getShortUrl(""+link+"/cbeauty/web/forwardDLoadPage/toDownLoadPage?id="+convert+"")+" ，下载惠美丽·韩束护肤APP手机客户端。";
					 //sendLink = "点击链接 "+link+"/cbeauty/web/forwardDLoadPage/toDownLoadPage?id="+convert+" ，下载惠美丽·韩束护肤APP手机客户端。";
				 } else if(flag.equals("SI2003")) {
					 String appDownLoadUrl = bConfig("groupcenter.app_hjyDownLoadUrl");
					 app_code = "SI2003";
					 sendLink = "点击链接 "+new LongShortUtil().getShortUrl(""+appDownLoadUrl+"/cfamily/web/forwardDLoadPage/toDownLoadPage?id="+convert+"")+" ，下载惠家有·微公社APP手机客户端。";
					 //sendLink = "点击链接 "+appDownLoadUrl+"/cfamily/web/forwardDLoadPage/toDownLoadPage?id="+convert+" ，下载惠家有·微公社APP手机客户端。";
				 } else if(flag.equals("SI2011")){
					 app_code = "SI2011";
					 sendLink = "您的好友 "+(vipuser_mobile.substring(0, 3) + "****" + vipuser_mobile.substring(7))+"送你购物返现特权，在我们的兄弟app内购物均可获得返利哦，快点注册微公社成为我们的会员吧，下载地址"+new LongShortUtil().getShortUrl(""+linkgroup+"/cgroup/web/grouppageSecond/inviteregister?superiorMobileNo="+vipuser_mobile+"&web_api_key=betagroup"); 
					 //sendLink = "点击链接 "+linkgroup+"/cgroup/web/grouppageSecond/inviteregister?superiorMobileNo="+vipuser_mobile+"&web_api_key=betagroup ，下载微公社APP手机客户端。";
				 }else if(flag.equals("SI2013")){
					 app_code = "SI2013";
					 sendLink = "你的朋友"+vipuser_mobile+"推荐你和他一起参加返利，戳"+new LongShortUtil().getShortUrl(""+linkgroup+"/cgroup/web/grouppageSecond/inviteregister?superiorMobileNo="+vipuser_mobile+"&web_api_key=betagroup")+" 加入吧 ！更多精彩尽在小时代·剧微商APP！";
					 //sendLink = "你的朋友"+vipuser_mobile+"推荐你和他一起参加返利，戳"+linkgroup+"/cgroup/web/grouppageSecond/inviteregister?superiorMobileNo="+vipuser_mobile+"&web_api_key=betagroup 加入吧 ！更多精彩尽在小时代·剧微商APP！";
					 
				 }else if(flag.equals("SI3003")){
					 app_code = "SI3003";
					 sendLink = "好基友，来剁手！扫货神器沙皮狗！瞅啥呢？进来看看吧！http://www.shapigo.com/apps/";
				 }
				 
				 
				 //此处添加短信渠道的判断 update by jlin 2015-05-18 10:36:00
//			MWebResult sendMessage = messageBase.sendMessage(recommendTel, sendLink,"");
				 MWebResult sendMessage = messageBase.sendMessage(recommendTel, sendLink,messageBase.upSendSourceByManageCode(flag));
				 
				 if(1 == sendMessage.getResultCode()) {
					 success_list.add(recommendTel);
					 //生成日志记录
					 SimpleDateFormat sysDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 DbUp.upTable("gc_recommend_info").insert("uqcode",convert,"app_code",app_code,"mobile",vipuser_mobile,
							 "recommended_mobile",recommendTel,"recommend_link",sendLink,"recommend_time",sysDateTime.format(new Date()));
					 
				 }
				 //发送失败
				 else {
					 
					 error_list.add(recommendTel);
				 }
				 
			 }else{//校验失败的
				 error_list.add(recommendTel);
			 }
			
		}
		//微公社，如果超过3次邀请，不算失败的 算成功的 start-- wangzx
		error_list.removeAll(much_list);
		success_list.addAll(much_list);
		//微公社，如果超过3次那个手机号，不算失败的 算成功的 end--
		
		map.put("success", success_list);
		map.put("error", error_list);
		return map;
	}
	
	
	/**
	 * <br>fq</br>
	 * 字符串转换
	 * @param dSource
	 * @param sParam
	 * @return
	 */
	public static String convertFormatNumberBack(String dSource, String sParam) {

		char[] cNumber = sParam.toCharArray();

		int iLength = cNumber.length;

		int iStep = 0;

		BigInteger bSource = new BigInteger(dSource);

		ArrayList<Integer> aList = new ArrayList<Integer>();

		while (bSource.divide(BigInteger.valueOf(iLength).pow(iStep))
				.compareTo(BigInteger.ONE) != -1) {

			int iNow = bSource
					.remainder(BigInteger.valueOf(iLength).pow(iStep + 1))
					.divide(BigInteger.valueOf(iLength).pow(iStep)).intValue();

			if (iNow == 0) {
				iNow = iLength;
			}

			bSource = bSource.subtract(BigInteger.valueOf(iNow).multiply(
					BigInteger.valueOf(iLength).pow(iStep)));

			aList.add(iNow);
			iStep++;

		}

		StringBuffer sBuffer = new StringBuffer();
		for (int i = aList.size() - 1; i >= 0; i--) {
			if (aList.get(i) == 0) {
				aList.set(i, iLength);
			}

			sBuffer.append(cNumber[aList.get(i) - 1]);
		}

		return sBuffer.toString();
	}
	
	/**
	 * <b>fq<br>
	 * 获取随机数,格式10000000-99999999
	 * @param number 位数
	 * @return
	 */
	public int getRandom(int number) {
		int max = 9;
		int min = 1;
		for (int i = 1; i < number; i++) {
			min = min * 10;
			max = max * 10 + 9;
		}
		return this.getRandom(min, max);
	}
	
	/**
	 * <b>fq<br>
	 * @param min
	 * @param max
	 * @return
	 */
	public int getRandom(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}
	
}
