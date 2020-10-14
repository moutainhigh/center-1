package com.cmall.groupcenter.baidupush.channel.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.cmall.groupcenter.baidupush.channel.auth.ChannelKeyPair;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsResponse;
import com.cmall.groupcenter.baidupush.channel.model.VerifyBindRequest;

public class BaiduChannelAsyncClient extends BaiduChannelClient implements
        BaiduChannelAsync {

    private static ExecutorService execurotService = Executors
            .newFixedThreadPool(2);

    public BaiduChannelAsyncClient(ChannelKeyPair pair) {
        super(pair);
    }

    @SuppressWarnings("static-access")
    public BaiduChannelAsyncClient(ChannelKeyPair pair,
            ExecutorService execurotService) {
        super(pair);
        this.execurotService = execurotService;
    }

    public Future<PushUnicastMessageResponse> pushUnicastMessageAsync(
            final PushUnicastMessageRequest request)
            throws ChannelClientException, ChannelServerException {
        return execurotService
                .submit(new Callable<PushUnicastMessageResponse>() {
                    
                    public PushUnicastMessageResponse call() throws Exception {
                        return pushUnicastMessage(request);
                    }
                });
    }

    
    public Future<Void> pushTagMessageAsync(final PushTagMessageRequest request)
            throws ChannelClientException, ChannelServerException {
        return execurotService.submit(new Callable<Void>() {
            
            public Void call() throws Exception {
                pushTagMessage(request);
                return null;
            }
        });
    }

    
    public Future<Void> pushBroadcastMessageAsync(
            final PushBroadcastMessageRequest request)
            throws ChannelClientException, ChannelServerException {
        return execurotService.submit(new Callable<Void>() {
            
            public Void call() throws Exception {
                pushBroadcastMessage(request);
                return null;
            }
        });
    }

    
    public Future<QueryBindListResponse> queryBindListAsync(
            final QueryBindListRequest request) throws ChannelClientException,
            ChannelServerException {
        return execurotService.submit(new Callable<QueryBindListResponse>() {
            
            public QueryBindListResponse call() throws Exception {
                return queryBindList(request);
            }
        });
    }

    
    public Future<Void> verifyBindAsync(final VerifyBindRequest request)
            throws ChannelClientException, ChannelServerException {
        return execurotService.submit(new Callable<Void>() {
            
            public Void call() throws Exception {
                verifyBind(request);
                return null;
            }
        });
    }

    
    public Future<QueryUserTagsResponse> queryUserTagsAsync(
            final QueryUserTagsRequest request) throws ChannelClientException,
            ChannelServerException {
        return execurotService.submit(new Callable<QueryUserTagsResponse>() {
            
            public QueryUserTagsResponse call() throws Exception {
                return queryUserTags(request);
            }
        });
    }

}
