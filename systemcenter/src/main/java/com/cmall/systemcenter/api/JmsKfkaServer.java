package com.cmall.systemcenter.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.enumer.KafkaNameEnumer;
import com.cmall.systemcenter.util.KafkaUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapzero.root.RootJmsListenser;

/***
 * 从jms里面往kafka里面放数据
 * @author zhouguohui
 *
 */
public class JmsKfkaServer extends RootJmsListenser{

	public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
		boolean retb = true;
		if(StringUtils.isNotEmpty(sMessage)){
			try{
				KafkaUtil.KafkaProperties().send(KafkaUtil.SetMessage(KafkaNameEnumer.OnProductOrder, null, sMessage));
			}catch(Exception e){
				retb=false;
				e.printStackTrace();
			}
			
		}
		
		return retb;
	}


}
