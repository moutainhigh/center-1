package com.cmall.groupcenter.baidupush.channel.client;

import java.util.Map;

import com.cmall.groupcenter.baidupush.channel.auth.ChannelKeyPair;
import com.cmall.groupcenter.baidupush.channel.auth.signature.ChannelSignatureDigest;
import com.cmall.groupcenter.baidupush.channel.constants.BaiduChannelConstants;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelClientException;
import com.cmall.groupcenter.baidupush.channel.exception.ChannelServerException;
import com.cmall.groupcenter.baidupush.channel.model.ChannelRequest;
import com.cmall.groupcenter.baidupush.channel.model.DeleteAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.DeleteTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.FetchTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.FetchTagResponse;
import com.cmall.groupcenter.baidupush.channel.model.InitAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushBroadcastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushTagMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageRequest;
import com.cmall.groupcenter.baidupush.channel.model.PushUnicastMessageResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryAppIoscertResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryBindListResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryDeviceTypeRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryDeviceTypeResponse;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsRequest;
import com.cmall.groupcenter.baidupush.channel.model.QueryUserTagsResponse;
import com.cmall.groupcenter.baidupush.channel.model.SetTagRequest;
import com.cmall.groupcenter.baidupush.channel.model.UpdateAppIoscertRequest;
import com.cmall.groupcenter.baidupush.channel.model.VerifyBindRequest;
import com.cmall.groupcenter.baidupush.channel.transform.ChannelRestRequestChecker;
import com.cmall.groupcenter.baidupush.channel.transform.ChannelRestRequestMapper;
import com.cmall.groupcenter.baidupush.channel.transform.ChannelRestResponseJsonUnmapper;
import com.cmall.groupcenter.baidupush.channel.transform.utils.TransformUtilitiy;
import com.cmall.groupcenter.baidupush.core.callback.YunLogHttpCallBack;
import com.cmall.groupcenter.baidupush.core.exception.YunHttpClientException;
import com.cmall.groupcenter.baidupush.core.httpclient.YunHttpClient;
import com.cmall.groupcenter.baidupush.core.log.YunLogHandler;
import com.cmall.groupcenter.baidupush.core.model.HttpRestResponse;

public class BaiduChannelClient implements BaiduChannel {

    private String apiKey = null;

    private String secretKey = null;

    private String host = null;

    private YunLogHttpCallBack logHttpCallback = new YunLogHttpCallBack();

    private ChannelRestResponseJsonUnmapper responseJsonUnmapper = new ChannelRestResponseJsonUnmapper();

    public BaiduChannelClient(ChannelKeyPair pair) {
        this(pair, BaiduChannelConstants.CHANNEL_REST_URL);
    }

    private BaiduChannelClient(ChannelKeyPair pair, String host) {
        this.apiKey = pair.getApiKey();
        this.secretKey = pair.getSecretKey();
        this.host = host;
    }

    public QueryBindListResponse queryBindList(QueryBindListRequest request)
            throws ChannelClientException, ChannelServerException {
        // TODO Auto-generated method stub
        HttpRestResponse resp = process("query_bindlist", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new QueryBindListResponse());
    }

    
    public PushUnicastMessageResponse pushUnicastMessage(
            PushUnicastMessageRequest request) throws ChannelClientException,
            ChannelServerException {

        HttpRestResponse resp = process("push_msg", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new PushUnicastMessageResponse());

    }

    
    public PushTagMessageResponse pushTagMessage(PushTagMessageRequest request)
            throws ChannelClientException, ChannelServerException {
        // TODO Auto-generated method stub
        HttpRestResponse resp = process("push_msg", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new PushTagMessageResponse());
    }

    
    public PushBroadcastMessageResponse pushBroadcastMessage(
            PushBroadcastMessageRequest request) throws ChannelClientException,
            ChannelServerException {
        // TODO Auto-generated method stub
        HttpRestResponse resp = process("push_msg", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new PushBroadcastMessageResponse());
    }

    
    public void verifyBind(VerifyBindRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("verify_bind", request);
        responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse());
    }

    
    public FetchMessageResponse fetchMessage(FetchMessageRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("fetch_msg", request);
        return new ChannelRestResponseJsonUnmapper().unmarshall(
                resp.getHttpStatusCode(), resp.getJsonResponse(),
                new FetchMessageResponse());
    }

    
    public void setTag(SetTagRequest request) throws ChannelClientException,
            ChannelServerException {
        HttpRestResponse resp = process("set_tag", request);
        new ChannelRestResponseJsonUnmapper().unmarshall(
                resp.getHttpStatusCode(), resp.getJsonResponse());
    }

    
    public void deleteTag(DeleteTagRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("delete_tag", request);
        new ChannelRestResponseJsonUnmapper().unmarshall(
                resp.getHttpStatusCode(), resp.getJsonResponse());
    }

    
    public FetchTagResponse fetchTag(FetchTagRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("fetch_tag", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new FetchTagResponse());
    }

    
    public QueryUserTagsResponse queryUserTags(QueryUserTagsRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("query_user_tags", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new QueryUserTagsResponse());
    }

    
    public void initAppIoscert(InitAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("init_app_ioscert", request);
        responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse());
    }

    
    public void updateAppIoscert(UpdateAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("update_app_ioscert", request);
        responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse());
    }

    
    public void deleteAppIoscert(DeleteAppIoscertRequest request)
            throws ChannelClientException, ChannelServerException {
        HttpRestResponse resp = process("delete_app_ioscert", request);
        responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse());
    }

    
    public QueryAppIoscertResponse queryAppIoscert(
            QueryAppIoscertRequest request) throws ChannelClientException,
            ChannelServerException {
        HttpRestResponse resp = process("query_app_ioscert", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new QueryAppIoscertResponse());
    }

    
    public QueryDeviceTypeResponse queryDeviceType(
            QueryDeviceTypeRequest request) throws ChannelClientException,
            ChannelServerException {
        HttpRestResponse resp = process("query_device_type", request);
        return responseJsonUnmapper.unmarshall(resp.getHttpStatusCode(),
                resp.getJsonResponse(), new QueryDeviceTypeResponse());
    }

    public void setChannelLogHandler(YunLogHandler logHandler) {
        logHttpCallback.setHandler(logHandler);
    }

    // -----------------------------------------------------------

    private HttpRestResponse process(String method, ChannelRequest request)
            throws ChannelClientException, ChannelServerException {

        ChannelRestRequestChecker checker = new ChannelRestRequestChecker();
        checker.validate(request);

        ChannelRestRequestMapper mapper = new ChannelRestRequestMapper();
        Map<String, String> params = mapper.marshall(request);
        params.put("method", method);
        params.put("apikey", apiKey);

        String surl = obtainIntegrityUrl(request);

        ChannelSignatureDigest digest = new ChannelSignatureDigest();
        String sign = digest.digest("POST", surl, secretKey, params);
        params.put("sign", sign);

        YunHttpClient client = new YunHttpClient();
        client.addHttpCallback(logHttpCallback);

        try {
            return client.doExecutePostRequestResponse(surl, params);
        } catch (YunHttpClientException e) {
            throw new ChannelClientException(e.getMessage());
        }
    }

    private String obtainIntegrityUrl(ChannelRequest request) {
        String resurl = host;
        if (host.startsWith("http://") || host.startsWith("https://")) {
        } else {
            resurl = "http://" + host;
        }
        if (resurl.endsWith("/")) {
            resurl = resurl + "rest/2.0/channel/";
        } else {
            resurl = resurl + "/rest/2.0/channel/";
        }
        String resourceId = TransformUtilitiy.extractResourceId(request,
                BaiduChannelConstants.CHANNEL_DEFAULT_RESOURCE_ID);
        return resurl + resourceId;
    }

}
