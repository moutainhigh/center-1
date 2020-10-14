package com.cmall.groupcenter.baidupush.core.callback;

import com.cmall.groupcenter.baidupush.core.event.YunHttpEvent;
import com.cmall.groupcenter.baidupush.core.log.NullYunLogHandler;
import com.cmall.groupcenter.baidupush.core.log.YunLogEvent;
import com.cmall.groupcenter.baidupush.core.log.YunLogHandler;

public class YunLogHttpCallBack implements YunHttpObserver {

    private YunLogHandler logHandler = new NullYunLogHandler();

    public void onHandle(YunHttpEvent event) {

        int level = YunLogEvent.DEBUG;
        if (event.getHttpStatusCode() == 200) {
            level = YunLogEvent.NOTICE;
        } else {
            level = YunLogEvent.WARNING;
        }
        String message = String.format("URL:[%s]\n" + "params:[%s]\n"
                + "HttpStatusCode:[%d]\n" + "Response:[%s]\n", event.getUrl(),
                event.getParams(), event.getHttpStatusCode(),
                event.getResponse());

        YunLogEvent logEvent = new YunLogEvent(level, message);
        if (logHandler != null) {
            synchronized (this) {
                logHandler.onHandle(logEvent);
            }
        }
    }

    public void setHandler(YunLogHandler handler) {
        this.logHandler = handler;
    }

}
