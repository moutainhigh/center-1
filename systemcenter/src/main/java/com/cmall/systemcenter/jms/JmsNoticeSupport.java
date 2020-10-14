package com.cmall.systemcenter.jms;

import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.baseface.IBaseInstance;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.websupport.MessageSupport;
import com.srnpr.zapzero.enumer.EJmsMessageType;
import com.srnpr.zapzero.face.IJmsListener;
import com.srnpr.zapzero.support.JmsSupport;

public class JmsNoticeSupport extends BaseClass implements IBaseInstance {

	public final static JmsNoticeSupport INSTANCE = new JmsNoticeSupport();

	/**
	 * 
	 * 发送消息通知 （主题模型）
	 * 
	 * @param jmsNameEnumer
	 *            消息类型
	 * @param sMsg
	 *            消息内容
	 * @param mPropMap
	 *            Map对象扩展
	 * @return
	 */
	public boolean sendToplic(JmsNameEnumer jmsNameEnumer, String sMsg,
			MDataMap mPropMap) {

		JmsSupport.getInstance().sendMessage(jmsNameEnumer.toString(), sMsg,
				mPropMap, EJmsMessageType.Toplic);

		return true;

	}

	/**
	 * 发送消息通知（队列模型）
	 * 
	 * @param jmsNameEnumer
	 * @param sMsg
	 * @param mPropMap
	 * @return
	 */
	public boolean sendQueue(JmsNameEnumer jmsNameEnumer, String sMsg,
			MDataMap mPropMap) {

		JmsSupport.getInstance().sendMessage(jmsNameEnumer.toString(), sMsg,
				mPropMap, EJmsMessageType.Queue);

		return true;

	}

	/**
	 * 添加一个队列订阅
	 * 
	 * @param jmsNameEnumer
	 * @param listener
	 * @return
	 */
	public boolean onReveiveQueue(JmsNameEnumer jmsNameEnumer,
			IJmsListener listener) {

		JmsSupport.getInstance().addTopicLisense(jmsNameEnumer.toString(),
				jmsNameEnumer.toString(), EJmsMessageType.Queue, listener);

		return true;

	}

	/**
	 * 添加一个主题订阅
	 * 
	 * @param jmsNameEnumer
	 * @param listener
	 * @return
	 */
	public boolean onReveiveToplic(JmsNameEnumer jmsNameEnumer,
			IJmsListener listener) {

		JmsSupport.getInstance().addTopicLisense(jmsNameEnumer.toString(),
				jmsNameEnumer.toString(), EJmsMessageType.Toplic, listener);

		return true;

	}

}
