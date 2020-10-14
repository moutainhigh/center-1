package com.cmall.groupcenter.account.api;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.cmall.groupcenter.account.model.ApiForGetVideoInput;
import com.cmall.groupcenter.account.model.ApiForGetVideoResult;
import com.cmall.groupcenter.util.HttpUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;


/**
 *商户培训视频接口获取
 *@author zhangbo
 */
public class ApiForGetVideo  extends RootApi<ApiForGetVideoResult, ApiForGetVideoInput>{

	static AtomicInteger idx = new AtomicInteger();
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Override
	public ApiForGetVideoResult Process(ApiForGetVideoInput inputParam, MDataMap mRequestMap) {
		ApiForGetVideoResult result = new ApiForGetVideoResult();
		Date now = new Date();
		String videoid = inputParam.getVideoid();
		String operate =inputParam.getOperate();
		try {

				        String params2 = "userid="+"7408202D39A123FF";
				        String params3 = "videoid="+videoid;
				        String totalStr = params2+"&"+params3;
				        String params1 = "time="+System.currentTimeMillis();
				        String md5Parmas = MD5(totalStr+"&"+params1+"&salt="+"2PTwX2QNHSghSm8UoagS32s5bQG7I1hR");
				        String videoHost = "http://union.bokecc.com/api/mobile";
				        String allurl = videoHost.concat("?").concat(totalStr).concat("&").concat(params1).concat("&").concat("hash=").concat(md5Parmas);
				        String temResult = HttpUtil.post(allurl, "{}", "UTF-8");
						String videoLink = this.xmlElements(temResult);
						result.setVideoLink(videoLink);
						result.setResultCode(1);


		} catch (Exception e) {
			result.setResultCode(0);
			e.printStackTrace();
		}
		return result;
	}
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
	public static boolean httpDownload(String httpUrl, String saveFile) {
        // 1.下载网络文件
        int byteRead;
        URL url;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }
 
        try {
            //2.获取链接
            URLConnection conn = url.openConnection();
            //3.输入流
            InputStream inStream = conn.getInputStream();
            //3.写入文件
            FileOutputStream fs = new FileOutputStream(saveFile);
 
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        }

	private String xmlElements(String xmlDoc) {
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        String linkvalue = "";
        try {
            //通过输入源构造一个Document
            Document doc = sb.build(source);
            //取的根元素
            Element root = doc.getRootElement();
//            System.out.println(root.getName());//输出根元素的名称（测试）
            //得到根元素所有子元素的集合
            List jiedian = root.getChildren();
            //获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            Element et = null;            
            for(int i=0;i<jiedian.size();i++){
                et = (Element) jiedian.get(i);//循环依次得到子元素
                if(linkvalue.equals("")){
                	linkvalue = et.getText();
                }
                if(et.getAttribute("quality").getValue().equals("20")){
                	linkvalue = et.getText();
                }
            }          
        } catch (JDOMException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return linkvalue;
    }
	
	/**
	 * 解码
	 * @param param eg: resultcode=0&resultmessage=操作成功&successreceivers=&failedreceivers=&mac=c6c9297bf57a77fd4ff0955864c2ba70
	 * @return
	 */
	private MDataMap decode(String param) {
		
		if(null == param || "".equals(param)) {
			return null;
		}
		
		try {//解码
			param = URLDecoder.decode(param, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		MDataMap resultMap = new MDataMap();
		String[] resultArr = param.split("&");
	    for(String strSplit:resultArr){
	          String[] arrSplitEqual=null;         
	          arrSplitEqual= strSplit.split("=");
	         
	          //解析出键值
	          if(arrSplitEqual.length>1){
	              //正确解析
	        	  resultMap.put(arrSplitEqual[0], arrSplitEqual[1]);
	          }else{
	              if(arrSplitEqual[0]!=""){
		              //只有参数没有值，不加入
	            	  resultMap.put(arrSplitEqual[0], "");       
	              }
	          }
	    }
	    return resultMap;
	}
	
	private String createSignature(Map<String, Object> param) {
		List<String> dataList = new ArrayList<String>();
		Set<Map.Entry<String, Object>> entryList = param.entrySet();
		for (Map.Entry<String, Object> entry : entryList) {
			if (entry.getValue() != null && !entry.getValue().toString().trim().isEmpty()) {
				dataList.add(entry.getValue().toString().trim());
			}
		}
		//Collections.sort(dataList);

		String text = StringUtils.join(dataList, "");

		text = DigestUtils.md5Hex(text);
		return text;
	}
}
