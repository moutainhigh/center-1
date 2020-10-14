package com.cmall.groupcenter.groupapp.model;

/**
 * 融云单聊消息
 * @author wangzx
 *
 */
public class RongYunSingleChatBean {

	private String fromUserId;//发送人用户 Id。（必传）
	private String toUserId;//接收用户 Id，可以实现向多人发送消息，每次上限为 1000 人。（必传）
	private String objectName;//消息类型，参考融云消息类型表.消息标志；可自定义消息类型。（必传）
	private String content;//发送消息内容，参考融云消息类型表.示例说明；如果 objectName 为自定义消息类型，该参数可自定义格式。（必传）
	private String pushContent="";//定义显示的 Push 内容，如果 objectName 为融云内置消息类型时，则发送后用户一定会收到 Push 信息。 如果为自定义消息，则 pushContent 为自定义消息显示的 Push 内容，如果不传则用户不会收到 Push 通知。(可选)
	private String pushData="";//针对 iOS 平台为 Push 通知时附加到 payload 中，Android 客户端收到推送消息时对应字段名为 pushData。(可选)
	private int count=0;//针对 iOS 平台，Push 时用来控制未读消息显示数，只有在 toUserId 为一个用户 Id 的时候有效。(可选)
	public String getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPushContent() {
		return pushContent;
	}
	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getPushData() {
		return pushData;
	}
	public void setPushData(String pushData) {
		this.pushData = pushData;
	}



}
