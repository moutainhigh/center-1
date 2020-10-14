package com.cmall.systemcenter.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * 解析xml
 * @author wz
 *
 */
public class AnalysisXmlUtil {

    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return Map
     */
    public static Map readStringXmlOut(String xml) {
        Map map = new HashMap();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            //System.out.println(doc.getRootElement());

            Element rootElt = doc.getRootElement(); // 获取根节点

            //System.out.println("根节点：" + rootElt.getNodeTypeName()); // 拿到根节点的名称
            
           // System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称

            Iterator iter = rootElt.elementIterator(); // 获取根节点下的子节点head

            // 遍历head节点

            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                
                map.put(recordEle.getName(), recordEle.getText());

            }
            return map;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static void main(String[] args) {
		String aa = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx5003e049845e69c1]]></appid><mch_id><![CDATA[1246046302]]></mch_id><nonce_str><![CDATA[OY3hcrhC1Oa7OWRc]]></nonce_str><sign><![CDATA[89853294E02BCE43EC0BA68B4DFEB7B0]]></sign><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx20150717162652f415e2891b0128279297]]></prepay_id><trade_type><![CDATA[APP]]></trade_type></xml>";
		AnalysisXmlUtil.readStringXmlOut(aa);
	}

}
