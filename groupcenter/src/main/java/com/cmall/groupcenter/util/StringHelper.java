package com.cmall.groupcenter.util;

import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.recommend.model.ApiRecommendDetailNcPostContentResult;


/**
 * 
 *处理字串的工具类
 *
 * @author lipengfei
 * @date 2015-5-22
 * email:lipf@ichsy.com
 *
 */
public class StringHelper {

    /**
     * 
     * 半角转全角
     * 
     * @param str
     * @return
     * 
     * @author mjorcen
     * @email mjorcen@gmail.com
     * @dateTime Sep 27, 2014 2:52:31 PM
     * @version 1
     */
    public static String ToSBC(String str) {
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }
    
    /**
     * 从map中取值
     * @author lipengfei
     * @date 2015-6-2
     * @param map
     * @param key
     * @return
     */
    public static String getStringFromMap(Map map ,String key){
		String value="";
			if(map.get(key)!=null){
				value = map.get(key).toString();
			}
			return value;
	}

    /**
     * 全角转半角
     * 
     * @param str
     * @return
     * 
     * @author mjorcen
     * @email mjorcen@gmail.com
     * @dateTime Sep 27, 2014 2:52:50 PM
     * @version 1
     */
    public static String ToDBC(String str) {
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }
    
    /**
     * 将List中的map转化成bean
     * @param list
     * @return
     */
    public static List<ApiRecommendDetailNcPostContentResult> changeMap2BeanAdapter(List<Map<String, String>> list) {
    	List<ApiRecommendDetailNcPostContentResult> beanList = new LinkedList<ApiRecommendDetailNcPostContentResult>();
    	for(Map<String, String> map : list) {
    		ApiRecommendDetailNcPostContentResult res = new ApiRecommendDetailNcPostContentResult();
    		res.setType(map.get("type"));
    		res.setContent(map.get("content"));
    		if(null != map.get("height")) {
    			res.setHeight(map.get("height"));
    		}
    		if(null != map.get("width")) {
    			res.setWidth(map.get("width"));
    		}
    		beanList.add(res);
    	}
    	return beanList;
    }
    
    
    /**
     * 根据图片切割帖子内容，分割文本图片内容
     * @param content
     * @return
     */
    public static List<Map<String, String>> formatContentByImg(String content) {
//    	content = "木22222222222222222222222<br /><img alt=\"\" src=\"http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/editor/24c49/6217ad7a07654f8da10f925b4d79b7c0.jpg\" style=\"height:410px; width:750px\" /><br /><br />产品外观及配件，可能因批次不同，有微小差异，敬请原谅。 防伪查询网站：www.ingrammicro.com.cn 请认准英迈电子商贸（上海） 有限公司原装行货防伪标签，刮开涂层，登陆防伪查询网站，输入防伪数码，进行查询<br /><br /><img alt=\"\" src=\"http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/editor/24c49/cfe52bd0ea5749e8b077e89af55a54ad.jpg\" style=\"height:711px; width:750px\" /><br />源自LeBron James的灵感，Powerbeats2 Wireless旨在挑战平凡但却有爆发力的运动员对完美的不懈追求。轻盈无比、双驱动声学设计，这款重新改造的无线耳机不仅能够传递优质声音，还具有优 越的性能，足以在严酷的训练中鞭策你前进。歌王亲自提供支持，下一代性能、动力和自由展露无遗。abcd<img alt=\"\" src=\"http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/editor/24c49/6217ad7a07654f8da10f925b4d79b7c0.jpg\" style=\"height:410px; width:750px\" />abcd<img alt=\"\" src=\"http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/editor/24c49/6217ad7a07654f8da10f925b4d79b7c0.jpg\" style=\"height:410px; width:750px\" />dafa<img alt=\"\" src=\"http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/editor/24c49/6217ad7a07654f8da10f925b4d79b7c0.jpg\" style=\"height:410px; width:750px\" />aaa";
    	if(null == content || "".equals(content)) {
    		return null;
    	}
    	String contentCopy = content;
    	List<Map<String, String>> listMap = new LinkedList<Map<String, String>>();
    	int theIndex = -1;
    	while((theIndex =contentCopy.indexOf("<img")) > -1) {
    		if(theIndex != 0) {
    			//文本
    			Map<String, String> textMap = new HashMap<String, String>();
    			textMap.put("type", ApiRecommendDetailNcPostContentResult.TYPE_TEXT);
    			textMap.put("content", contentCopy.substring(0, theIndex));
    			listMap.add(textMap);
    			
    			contentCopy = contentCopy.substring(theIndex);
    			
    			//图片
    			theIndex = contentCopy.indexOf("/>");
    			//图片元素
    			String imgText = contentCopy.substring(0, theIndex+2);
    			Map<String, String> imgMap = formatImgStr(imgText);
    			imgMap.put("type", ApiRecommendDetailNcPostContentResult.TYPE_IMG);
    			listMap.add(imgMap);
    			
    			contentCopy = contentCopy.substring(theIndex+2);
    		} else {
    			theIndex = contentCopy.indexOf("/>");
    			//图片元素
    			String imgText = contentCopy.substring(0, theIndex+2);
    			Map<String, String> imgMap = formatImgStr(imgText);
    			imgMap.put("type", ApiRecommendDetailNcPostContentResult.TYPE_IMG);
    			listMap.add(imgMap);
    			contentCopy = contentCopy.substring(theIndex+2);
    		}
    	}
    	if(contentCopy.length() > 0) {
    		Map<String, String> lasttextMap = new HashMap<String, String>();
    		lasttextMap.put("type", ApiRecommendDetailNcPostContentResult.TYPE_TEXT);
    		lasttextMap.put("content", contentCopy);
    		listMap.add(lasttextMap);
    	}
    	return listMap;
    }
    
    /**
     * 切割图片字符串
     * <img alt="" src="http://qhbeta-cfiles.qhw.srnpr.com/cfiles/6217ad7a0765.jpg" style="height:410px; width:750px" />
     * @param imgStr
     * @return
     */
    public static Map<String, String> formatImgStr(String imgText) {
    	Map<String, String> imgMap = new HashMap<String, String>();
    	String[] imgParams = imgText.replace("; ", ";").split(" ");
		for(String pa : imgParams) {
			if(pa.startsWith("src")) { //图片地址
				String src = pa.split("=")[1];
				imgMap.put("content", src.substring(1, src.length()-1));
			} else if(pa.startsWith("style")) {
				String style = pa.split("=")[1];
				style = style.substring(1, style.length()-1);
				String[] heiAndWid = style.split(";");
				for(String hw : heiAndWid) {
					String[] hws = hw.split(":");
					imgMap.put(hws[0].trim(), hws[1].trim());
				}
			}
		}
		return imgMap;
    }
    
    public static void main1(String[] args) {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            String str = "８１４乡道阿斯蒂芬１２３／．１２，４１２看２家１快２看了就２；看了２叫看来＋看来家１２考虑就２３；了３接口２了２会２，．水电费苦辣时间的２　　１２５１２３１２３１２１２０９－０２１～！＠＃＄％＾＆＊（）＿";
            
            String result = ToDBC(str);
            
        //    System.out.println(ToSBC(result));
        }
       // System.out.println(System.currentTimeMillis() - l);
    }
    public static void main(String[] args) {
    	List<Map<String, String>> list = formatContentByImg("");
    	List<ApiRecommendDetailNcPostContentResult> beanList = changeMap2BeanAdapter(list);
    	for(ApiRecommendDetailNcPostContentResult r : beanList) {
    	//	System.out.println(r.getType() + "   " + r.getContent() + "  " + r.getHeight() + " " + r.getWidth());
    	}
    }
	
    
    /**
     * 按照需要的格式分割
     * @author lipengfei
     * @date 2015-6-4
     * @param string
     * @param interceptor
     * @param value
     */
    public static void formatString(StringBuffer string, String interceptor,
			String value) {
		if (StringUtils.isNotBlank(value)) {
			string.append(interceptor + value);
		}
	}
	
    /**
     * 将list的数据变成分割的字符串
     * @author lipengfei
     * @date 2015-6-4
     * @param list
     * @param interceptor
     * @return
     */
	public static String formatString(List<Object> list,String interceptor){
				
				StringBuffer temp = new StringBuffer();
				
				for (Object object : list) {
					temp.append(object+""+interceptor);
				}
				
				String value = temp.toString();
				value = value.substring(0, value.length()-1);
				
				return value;
	}
	
	/**
	 * 删除文本中的表情符号
	 * @param text
	 * @return
	 */
	public static String deleteEmoji(String text){
		if(text == null) return "";
		
		CharsetEncoder encoder = java.nio.charset.Charset.forName("UTF-8").newEncoder();
		StringBuilder build = new StringBuilder();
		for(int i = 0,j = text.length(); i < j; i++){
			if(encoder.canEncode(text.charAt(i))){
				build.append(text.charAt(i));
			}
		}
		return build.toString();
	}

	
}
