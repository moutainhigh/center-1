    package com.cmall.groupcenter.weixin.model;  
      
    /** 
     * 文本消息 
     *  
     * @author
     * @date
     */  
    public class TextMessage extends BaseMessage {  
        // 回复的消息内容  
        private String Content;  
      
        public String getContent() {  
            return Content;  
        }  
      
        public void setContent(String content) {  
            Content = content;  
        }  
    }  