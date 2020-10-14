package com.cmall.groupcenter.func;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.systemcenter.api.SinglePushComment;
import com.cmall.systemcenter.model.AddSinglePushCommentInput;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


/**
 * 
 * 模块:微公社--Im用户黑名单
 * @author panwei
 *
 */
public class FuncBlackReporter extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mWebResult = new MWebResult();
		String status = mDataMap.get("zw_f_flag_black");
		String uid = mDataMap.get("zw_f_uid");
		MDataMap whereDataMap  = new MDataMap();
		
		/*系统当前时间*/
		String start_time = DateUtil.getNowTime();
		
		String end_time=DateUtil.toString((DateUtil.addDays(new Date(), 1)), DateUtil.DATE_FORMAT_DATETIME);
		
		MDataMap blackMap=DbUp.upTable("gc_report_black").one("uid",uid);
		String memberCode=blackMap.get("be_report_user");
		if("4497472000050001".equals(status)) {//当前为黑名单状态，修改为移除
				whereDataMap.put("flag_black", "4497472000050002");
				whereDataMap.put("uid", uid);
				DbUp.upTable("gc_report_black").dataUpdate(whereDataMap, null, "uid");
				//调融云接口移除黑名单
				postUNBlock(memberCode);
		} else if("4497472000050002".equals(status)) {
			
				whereDataMap.put("flag_black", "4497472000050001");
				whereDataMap.put("uid", uid);
				whereDataMap.put("black_start_time", start_time);
				whereDataMap.put("black_end_time", end_time);
				
				DbUp.upTable("gc_report_black").dataUpdate(whereDataMap,null, "uid");
				//调融云接口加入黑名单
				postBlock(memberCode);
				
				//向前台推送消息
				MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",memberCode,"manage_code","SI2011","flag_enable","1");
				String sAccountCode=memberMap.get("account_code");
				AddSinglePushCommentInput addSinglePushCommentInput=new AddSinglePushCommentInput();
				addSinglePushCommentInput.setAccountCode(sAccountCode);
				addSinglePushCommentInput.setAppCode("SI2011");
				addSinglePushCommentInput.setType("44974720000400010002");
				
				addSinglePushCommentInput.setPreSendTime(FormatHelper.upDateTime());
				addSinglePushCommentInput.setProperties("systemMessageType=2&dateTime="+System.currentTimeMillis());
				addSinglePushCommentInput.setTitle("您被禁言1天");
				addSinglePushCommentInput.setUserCode(memberCode);
			    String content="您被用户多次举报，经工作人员核实，您的情节较为严重，被禁言1天，下次将做冻结账号处理哦！有问题联系客服#"+GroupConst.GROUP_CUSTOM_SERVICE_PHONE+"#";
			    addSinglePushCommentInput.setContent(content);
				SinglePushComment.addPushComment(addSinglePushCommentInput);
		}
		return mWebResult;
	}

	private String postBlock(String memberCode) {
	   String appkey = bConfig("membercenter.rongyun_app_key"); //我自己的 appkey：mgb7ka1nb5ijg
	   String appSerct=bConfig("membercenter.rongyun_app_secret"); //我自己的appSerct:h5Xrgdg3rOx
	   String rongyunDomain=bConfig("membercenter.rongyun_domain"); //融云 域名

       String nonce=String.valueOf(new Random().nextInt());
       long millis = System.currentTimeMillis();
       //http请求头信息
       MDataMap httpHeaderDataMap = new MDataMap();
       httpHeaderDataMap.put("App-Key", appkey);
       httpHeaderDataMap.put("Nonce", nonce);
       httpHeaderDataMap.put("Timestamp",String.valueOf(millis) );
	   String signature =this.Encrypt(appSerct+nonce+millis);
	   httpHeaderDataMap.put("Signature",signature );
	   //表单信息
	   MDataMap map = new MDataMap();
	   String userId = memberCode;
	   String minute="1440";
	   map.put("userId", userId);
	   map.put("minute", minute);
	   String resultJson="";
	   try {
		   resultJson = WebClientSupport.upPost(rongyunDomain+"/user/block.json", map,httpHeaderDataMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   JsonHelper<Map<String,String>> jH = new JsonHelper<Map<String,String>>();
	   Map<String,String> rongyunJsonObj = jH.StringToObj(resultJson, new HashMap<String,String>());
	   return String.valueOf(rongyunJsonObj.get("code"));
	}
	
	private String postUNBlock(String memberCode) {
		   String appkey = bConfig("membercenter.rongyun_app_key"); //我自己的 appkey：mgb7ka1nb5ijg
		   String appSerct=bConfig("membercenter.rongyun_app_secret"); //我自己的appSerct:h5Xrgdg3rOx
		   String rongyunDomain=bConfig("membercenter.rongyun_domain"); //融云 域名

	       String nonce=String.valueOf(new Random().nextInt());
	       long millis = System.currentTimeMillis();
	       //http请求头信息
	       MDataMap httpHeaderDataMap = new MDataMap();
	       httpHeaderDataMap.put("App-Key", appkey);
	       httpHeaderDataMap.put("Nonce", nonce);
	       httpHeaderDataMap.put("Timestamp",String.valueOf(millis) );
		   String signature =this.Encrypt(appSerct+nonce+millis);
		   httpHeaderDataMap.put("Signature",signature );
		   //表单信息
		   MDataMap map = new MDataMap();
		   String userId = memberCode;
		   map.put("userId", userId);
		   String resultJson="";
		   try {
			   resultJson = WebClientSupport.upPost(rongyunDomain+"/user/unblock.json", map,httpHeaderDataMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   JsonHelper<Map<String,String>> jH = new JsonHelper<Map<String,String>>();
		   Map<String,String> rongyunJsonObj = jH.StringToObj(resultJson, new HashMap<String,String>());
		   return String.valueOf(rongyunJsonObj.get("code"));
		}

	/**
	 * sha-1 加密
	 * @param strSrc
	 * @return
	 */
	public String Encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
	
		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = Hex.encodeHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			//System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

}
