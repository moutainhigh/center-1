package com.cmall.ordercenter.service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cmall.ordercenter.model.GetOrderFreightInput;
import com.cmall.ordercenter.model.GetOrderFreightResult;
import com.cmall.ordercenter.model.ProductFrenghtModel;
import com.cmall.ordercenter.model.PtInfo;
import com.cmall.ordercenter.model.StoreFreight;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 规则：根据运费标准计算规则
 * 注意
 * 1： 默认运费和指定区域的运费是多条记录，利用sql，合并成一个。有指定区域的用指定区域，否则用默认的。
 * 2：同一店铺的商品用的模板相同，计算运费前，数量合并
 * 
 * 订单运费
 * @author huoqiangshou
 *
 */
public class GetOrderFreightService extends BaseClass {
	
	/**
	 * 商品表
	 */
	private String PRODUCT_INFO="pc_productinfo";
	
	/**
	 * 运费模板
	 */
	private String FREIGHT_TPL="uc_freight_tpl";
	
	
	/**
	 * 指定区域不可售
	 */
	private String SPE_AREA_NOT_SALE = "不可售";
	/**
	 * 1:查询商品
	 * 2:查询运费模板
	 * 3:计算商品运费
	 * 4:店家各个店铺的运费
	 * @return 返回运费结果
	 */
	public GetOrderFreightResult getOrderFreight(GetOrderFreightInput of){
		GetOrderFreightResult result = new GetOrderFreightResult();
		result.setResultCode(1);
		result.setResultMessage("成功！");
		
		List<String> productCodes = new ArrayList<String>();
		//各商品的数量
		Map<String,Integer> productAmountMap = new HashMap<String,Integer>();
		for(PtInfo info:of.getPtInfos()){
			productCodes.add("'"+info.getProductCode()+"'");
			productAmountMap.put(info.getProductCode(), info.getAccount());
		}
		//查询所有商品
		List<MDataMap> productList=  DbUp.upTable(PRODUCT_INFO).query("seller_code sellerCode,transport_template transportTemplate,product_code productCode,product_weight productWeight,product_volume productVolume", "", "  product_code in (  "+StringUtils.join(productCodes, ",")+")", null, 0, 0);
		
		ProductFrenghtModel prm=null,tmpPrm=null; 
		//店铺编码: 店铺code+商品code+模板code  为key的 对象 
		Map<String,ProductFrenghtModel> sellerMap = new HashMap<String, ProductFrenghtModel>();
		//模板编码
		Set<String> tmplateCodes = new HashSet<String>();
		for(MDataMap map:productList){
			try {
				//old 运费模板
//				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
//				tmplateCodes.add("'"+prm.getTransportTemplate()+"'");
//				String tmpKey = prm.getSellerCode()+":"+prm.getProductCode()+":"+prm.getTransportTemplate();
//				//设置数量
//				if(null!=productAmountMap.get(prm.getProductCode())){
//					prm.setAmount(productAmountMap.get(prm.getProductCode()));
//				}else{
//					prm.setAmount(0);
//				}
				
				//new 
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				tmplateCodes.add("'"+prm.getTransportTemplate()+"'");
				//key ： 店铺号+模版号  数量叠加
				String tmpKey = prm.getSellerCode()+":"+prm.getTransportTemplate();
				//设置数量
				Integer ct = productAmountMap.get(prm.getProductCode());
				if(null!=ct){ //
					if(null != sellerMap.get(tmpKey)){  //同一个店铺 模板 的商品数量叠加
						tmpPrm = sellerMap.get(tmpKey);
						prm.setAmount(ct+tmpPrm.getAmount());
					}else{
						prm.setAmount(productAmountMap.get(prm.getProductCode()));
					}
				}else{  //没有的设置-0
					prm.setAmount(0);
				}
				
				sellerMap.put(tmpKey, prm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				result.setResultCode(1);
				result.setResultMessage(bInfo(939301063));
				return result;
			}
			
		}
		//模板uid
		//用的的模板
		List<MDataMap> tplList=  DbUp.upTable(FREIGHT_TPL).query("uid,store_id sellerCode,isDisable tplDisable,is_free isFree,valuation_type valuationType", "", "  uid in (  "+StringUtils.join(tmplateCodes, ",")+")", null, 0, 0);
		for(MDataMap map:tplList){
			try {
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				for(ProductFrenghtModel pp:getModelFromMapBykey(sellerMap,map.get("uid"))){
					copyBeansPropertyNotNull(prm, pp);
//					sellerMap.put(map.get("uid"), pp);
				}
			} catch (Exception e) {
				result.setResultCode(1);
				result.setResultMessage(bInfo(939301063));
				return result;
			}
			
		}
		
		
		
		//运费模板明细  
//		List<MDataMap> tplDetailList=  DbUp.upTable(PRODUCT_INFO).query("", "", "  uid in (  "+StringUtils.join(tmplateCodes, ",")  +") and areaCode ='"+of.getAreaCode()+"'", null, 0, 0);
	
		List<Map<String, Object>> tplDetailList = DbUp.upTable("uc_freight_tpl_detail").upTemplate().queryForList(doFreightDetailSql(of.getAreaCode(),StringUtils.join(tmplateCodes, ",") ), new MapSqlParameterSource(null));
		
		for(Map map:tplDetailList){
			try {
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				//覆盖对象的运费信息
				for(ProductFrenghtModel pp:getModelFromMapBykey(sellerMap,(String)map.get("uid"))){
					copyBeansPropertyNotNull(prm, pp);
//					sellerMap.put((String)map.get("uid"), pp);
				}
			} catch (Exception e) {
				result.setResultCode(1);
				result.setResultMessage(bInfo(939301063));
				return result;
			}
		}
		//循环计算每个商品的运费，有bug 同一个商家卖出两件衣服  运费一份就够   
		
		Map<String,StoreFreight> sfMap = new HashMap<String,StoreFreight>();
		List<String> noSaleList = new ArrayList<String>();
		for(String key:sellerMap.keySet()){
			prm = sellerMap.get(key);
			String tmpMoney="";
			StoreFreight sf=null;
			if(sfMap.keySet().contains(prm.getSellerCode())){
				if(ProductFrenghtModel.TPL_DISABLE_Y.equals(prm.getTplDisable())){  //禁用
					continue;
				}else{
					sf = sfMap.get(prm.getSellerCode());
					tmpMoney = getProductMoney(prm);
					if(!SPE_AREA_NOT_SALE.equals(tmpMoney)){
						sf.setFreight(DecimalByDouble(Double.valueOf(sf.getFreight())).add(DecimalByDouble(Double.valueOf(tmpMoney))).toString());  
						sfMap.put(prm.getSellerCode(),sf);
					}else{
						noSaleList.add(prm.getProductCode());
					}
				}
			}else{
				sf = new StoreFreight();
				sf.setStroeCode(prm.getSellerCode());
				tmpMoney = getProductMoney(prm);
				if(!SPE_AREA_NOT_SALE.equals(tmpMoney)){
					sf.setFreight(tmpMoney);
					sfMap.put(prm.getSellerCode(),sf);
				}else{
					noSaleList.add(prm.getProductCode());
				}
			}
		}
		
		
		result.setList(new ArrayList<StoreFreight>(sfMap.values()));
		result.setNoSaleList(noSaleList);
		
		return result;
	}
	
	
	/**
	 * 返回商品的运费
	 * @return
	 */
	private String getProductMoney(ProductFrenghtModel prm){
		String tmpMoney="";
		//模板类型 1: 0为免运费  2:数字为运费金额 3:运费模板
		String transCode = prm.getTransportTemplate();
		//对商品单独设置 运费
//		if("0".equals(transCode)){
//			tmpMoney = "0";
//		}else{
//			try {
//				//转换Double操作主要为了判断运费模版是否是数字，如果不是则进入catch处理
//				Double.valueOf(transCode);
//				tmpMoney = transCode;
//			}catch (NumberFormatException e) {
//				tmpMoney = getFreightMoney(prm);
//			}
//		}
		if (StringUtils.isEmpty(transCode)) {
			tmpMoney = "0";
		}else if (transCode.length() == 32) {
			tmpMoney = getFreightMoney(prm);
		}else{
			tmpMoney = transCode;
		}
		
		return tmpMoney;
	}
	
	
	/**
	 * 计算商品的运费金额 
	 * @return 运费
	 */
	public String getFreightMoney(ProductFrenghtModel fm){
		String money="";
		if(ProductFrenghtModel.TPL_DISABLE_Y.equals(fm.getTplDisable())){  //禁用  为了友好，显示为不可售
			return SPE_AREA_NOT_SALE;
		}else if(ProductFrenghtModel.IS_FREE_Y.equals(fm.getIsFree())){  //卖家承担运费
			money = "0";
		}else{                                //需要计算运费
			if(ProductFrenghtModel.VALUATION_TYPE_P.equals(fm.getValuationType())){  //件数
				money = getMoneyByValutionTypeP(fm);
			}else if(ProductFrenghtModel.VALUATION_TYPE_W.equals(fm.getValuationType())){  //重量
				money = getMoneyByValutionTypeW(fm);
			}else{  //体积
				money = getMoneyByValutionTypeV(fm);
			}
		}
		return money.toString();
	}
	
	/**
	 * 按件数 计算运费
	 * @return
	 */
	private String getMoneyByValutionTypeP(ProductFrenghtModel fm){
		String money="";
		BigDecimal bdMoneyF,bdMoneyS,bdMoneyT;
		if(ProductFrenghtModel.DETAIL_ENABLE_Y.equals(fm.getSpeDetailEnable())){  //指定区域是否可售 是
			if(fm.getAmount()>fm.getSpeExpressStart()){ // 大于基础单位
				// 公式=首费用+（总件数-首件数）×（续费用/续件数）
				bdMoneyF = DecimalByDouble(fm.getSpeExpressPostage());
				bdMoneyS = DecimalByDouble(fm.getAmount()).subtract(DecimalByDouble(fm.getSpeExpressStart()));
				
				if(0.0==fm.getSpeExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getSpeExpressPostagePlus()).divide(DecimalByDouble(fm.getSpeExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				
				
				money = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT)).toString();
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getSpeExpressPostage()).toString();
			}
		}else if(ProductFrenghtModel.DETAIL_ENABLE_N.equals(fm.getSpeDetailEnable())){
			money = SPE_AREA_NOT_SALE;
		}else {   //指定区域不可售 
			if(fm.getAmount()>fm.getExpressStart()){ // 大于基础单位
				// 公式=首费用+（总件数-首件数）×（续费用/续件数）
				bdMoneyF = DecimalByDouble(fm.getExpressPostage());
				bdMoneyS = DecimalByDouble(fm.getAmount()).subtract(DecimalByDouble(fm.getExpressStart()));
				if(0.0==fm.getExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getExpressPostagePlus()).divide(DecimalByDouble(fm.getExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				money = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT)).toString();
				
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getExpressPostage()).toString();
			}
		}
		return money;
	}
	
	
	
	/**
	 * 按重量 计算运费
	 * @return
	 */
	private String getMoneyByValutionTypeW(ProductFrenghtModel fm){
		String money="";
		BigDecimal bdMoneyF,bdMoneyS,bdMoneyT,bdMoneyFour;
		double basicWeight = fm.getProductWeight()*fm.getAmount();
		if(ProductFrenghtModel.DETAIL_ENABLE_Y.equals(fm.getSpeDetailEnable())){  //指定区域是否可售 是
			if(basicWeight>fm.getSpeExpressStart()){ // 大于基础单位  公式=首费用+（总重量-首重量）×（续费用/续重量）
				// 首费用
				bdMoneyF = DecimalByDouble(fm.getSpeExpressPostage());
				//总重量-首重量
				bdMoneyS = DecimalByDouble(fm.getAmount()).multiply(DecimalByDouble(fm.getProductWeight())).subtract(DecimalByDouble(fm.getSpeExpressStart()));
				//需费用/续重量
				if(0.0==fm.getSpeExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getSpeExpressPostagePlus()).divide(DecimalByDouble(fm.getSpeExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				bdMoneyFour = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT));
				money = bdMoneyFour.toString();
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getSpeExpressPostage()).toString();
			}
		}else if(ProductFrenghtModel.DETAIL_ENABLE_N.equals(fm.getSpeDetailEnable())){
			money = SPE_AREA_NOT_SALE;
		}else{   //指定区域不可售 
			if(basicWeight>fm.getExpressStart()){ // 大于基础单位  公式=首费用+（总重量-首重量）×（续费用/续重量）
				// 首费用
				bdMoneyF = DecimalByDouble(fm.getExpressPostage());
				//总重量-首重量
				bdMoneyS = DecimalByDouble(fm.getAmount()).multiply(DecimalByDouble(fm.getProductWeight())).subtract(DecimalByDouble(fm.getExpressStart()));
				//需费用/续重量
				if(0.0==fm.getExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getExpressPostagePlus()).divide(DecimalByDouble(fm.getExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				bdMoneyFour = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT));
				money = bdMoneyFour.toString();
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getExpressPostage()).toString();
			}
		}
		return money;
	}
	
	
	
	/**
	 * 按体积 计算运费
	 * @return
	 */
	private String getMoneyByValutionTypeV(ProductFrenghtModel fm){
		String money="";
		double basicV = fm.getProductVolume()*fm.getAmount();
		BigDecimal bdMoneyF,bdMoneyS,bdMoneyT,bdMoneyFour;
		if(ProductFrenghtModel.DETAIL_ENABLE_Y.equals(fm.getSpeDetailEnable())){  //指定区域是否可售 是
			if(basicV>fm.getSpeExpressStart()){ // 大于基础单位  公式=首费用+（总体积-首体积）×（续费用/续体积）
				// 首费用
				bdMoneyF = DecimalByDouble(fm.getSpeExpressPostage());
				//总重量-首重量
				bdMoneyS = DecimalByDouble(fm.getAmount()).multiply(DecimalByDouble(fm.getProductVolume())).subtract(DecimalByDouble(fm.getSpeExpressStart()));
				//需费用/续重量
				if(0.0==fm.getSpeExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getSpeExpressPostagePlus()).divide(DecimalByDouble(fm.getSpeExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				bdMoneyFour = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT));
				money = bdMoneyFour.toString();
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getSpeExpressPostage()).toString();
			}
		}else if(ProductFrenghtModel.DETAIL_ENABLE_N.equals(fm.getSpeDetailEnable())){
			money = SPE_AREA_NOT_SALE;
		}else{   //指定区域不可售 
			if(basicV>fm.getExpressStart()){ // 公式=首费用+（总体积-首体积）×（续费用/续体积
				// 首费用
				bdMoneyF = DecimalByDouble(fm.getExpressPostage());
				//总重量-首重量
				bdMoneyS = DecimalByDouble(fm.getAmount()).multiply(DecimalByDouble(fm.getProductVolume())).subtract(DecimalByDouble(fm.getExpressStart()));
				//需费用/续重量
				if(0.0==fm.getExpressPlus()){  //除数为0
					bdMoneyT = DecimalByDouble(0.0);
				}else{
					bdMoneyT = DecimalByDouble(fm.getExpressPostagePlus()).divide(DecimalByDouble(fm.getExpressPlus()),BigDecimal.ROUND_FLOOR);
				}
				
				bdMoneyFour = bdMoneyF.add(bdMoneyS.multiply(bdMoneyT));
				money = bdMoneyFour.toString();
			}else{
				//公式=首费用
				money = DecimalByDouble(fm.getExpressPostage()).toString();
			}
		}
		return money;
	}
	
	
	/**
	 * @return
	 */
	private BigDecimal DecimalByDouble(double d){
		return BigDecimal.valueOf(d);
	}
	
	/**
	 * 从map中，取出key包含partKey的 对象
	 * @param sellerMap
	 * @param partKey
	 * @return
	 */
	private List<ProductFrenghtModel> getModelFromMapBykey(Map<String,ProductFrenghtModel> sellerMap ,String partKey){
		
		List<ProductFrenghtModel> pfmList= new ArrayList<ProductFrenghtModel>();
		String key="";
		for(Iterator<String> itor=sellerMap.keySet().iterator();itor.hasNext();){
			key = itor.next();
			if(key.indexOf(partKey)!=-1){
				pfmList.add(sellerMap.get(key));
			}
			
		}
		return pfmList;
	}
	
	
	/**
	 * 复制sourceObj到targetObj不为空的属性,只复制字符类型的，
	 * 
	 * @param sourceObj
	 * @param targetObj
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private void copyBeansPropertyNotNull(Object sourceObj,Object targetObj) throws Exception{
		Method[] methods = sourceObj.getClass().getDeclaredMethods();
		
		String mName="";
		Object mValue=null;
		Method targetMethod = null;
		for(Method m:methods){
			mName = m.getName();
			if(mName.startsWith("get")){
				mValue = m.invoke(sourceObj, null);
				if(null!=m.getReturnType()&&null!=mValue){
					if(int.class==m.getReturnType()&&0==(Integer)mValue){
						continue;
					}
					if(double.class==m.getReturnType()&&0.0==(Double)mValue){
						continue;
					}
					targetMethod = targetObj.getClass().getMethod("set"+mName.substring(3, mName.length()), m.getReturnType());
					targetMethod.invoke(targetObj, mValue);
				}
				
			}
		}
	}
	
	
	 /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException
     *             如果分析类属性失败
     * @throws IllegalAccessException
     *             如果实例化 JavaBean 失败
     * @throws InstantiationException
     *             如果实例化 JavaBean 失败
     * @throws InvocationTargetException
     *             如果调用属性的 setter 方法失败
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
     */
    public static Object convertMap(Class type, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Object obj = type.newInstance(); // 创建 JavaBean 对象
        List<Field> list = Arrays.asList(type.getDeclaredFields());
        
        String fieldName="";
        Method m = null;
        for(Field field:list){
        	fieldName = field.getName();
        	if(map.keySet().contains(fieldName)){
        		//得到set 方法
				m = type.getMethod("set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length()), field.getType());
				if(null==map.get(fieldName)){
					continue;
				}
				if(double.class==field.getType()){ 
					m.invoke(obj,Double.valueOf(map.get(fieldName).toString().trim()) );
				}else if(String.class==field.getType()){
					m.invoke(obj, (String)map.get(fieldName));
				}else if(int.class==field.getType()){
					m.invoke(obj, (Integer)map.get(fieldName));
				}else if(Boolean.class==field.getType()){
					m.invoke(obj, (Boolean)map.get(fieldName));
				}else if(Date.class==field.getType()){
					m.invoke(obj, (Date)map.get(fieldName));
				}else if(float.class==field.getType()){
					m.invoke(obj, (Float)map.get(fieldName));
				}
        	}
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
	
	/**
	 * 商品表和运费模板关联sql。一个商品最多可以有两条运费记录一个默认，一个指定区域的
	 * 两条记录合并为一个，这个方法产生一个from后的信息，需要拼接两次 一个global（默认），
	 * 一次特定区域
	 * @return
	 */
	public String querySql(){
		StringBuffer sbf = new StringBuffer();
		sbf.append("SELECT");
		sbf.append("	product.seller_code,product.product_code,");
		sbf.append("	tmplate.tplDisable,tmplate.is_free,tmplate.valuation_type,");
		sbf.append("			tmplate.area,tmplate.area_Code,tmplate.detailEnable,");
		sbf.append("			tmplate.express_Start,tmplate.express_Postage,tmplate.express_Plus,tmplate.express_Postage_plus");
		sbf.append("	");
		sbf.append("	 from (");
		sbf.append("		SELECT");
		sbf.append("			pc.seller_code,pc.transport_template,pc.product_code");
		sbf.append("		FROM");
		sbf.append("			pc_productinfo pc");
		sbf.append("		) ");
		sbf.append("			product");
		sbf.append(" 	INNER JOIN");
		sbf.append("	(");
		sbf.append("		SELECT");
		sbf.append("			tpl.zid,tpl.store_id,tpl.uid,tpl.isDisable tplDisable,tpl.is_free,tpl.valuation_type,");
		sbf.append("			detail.area,detail.area_Code,detail.isEnable detailEnable,");
		sbf.append("			detail.express_Start,detail.express_Postage,detail.express_Plus,detail.express_Postage_plus");
		sbf.append("		FROM");
		sbf.append("			uc_freight_tpl tpl");
		sbf.append("				INNER JOIN");
		sbf.append("				uc_freight_tpl_detail detail");
		sbf.append("				ON");
		sbf.append("				tpl.uid= detail.tpl_uid)");
		sbf.append("		 tmplate ");
		sbf.append("		");
		sbf.append("	on product.seller_code= tmplate.store_id and product.transport_template=tmplate.uid  and  ");
		return sbf.toString();
	}
	
	
	/**
	 * 查询模板明细
	 * @param areaCode
	 * @return
	 */
	public String doFreightDetailSql(String areaCode,String tplUids){
		String querySql = "SELECT"
				+"      a.tpl_uid uid,"
				+"      a.area,"
				+"      a.area_code areaCode,"
				+"      a.isEnable detailEnable,"
				+"      a.express_Start expressStart,"
				+"      a.express_Postage expressPostage,"
				+"      a.express_Plus expressPlus,"
				+"      a.express_Postage_Plus expressPostagePlus,"
				+"      b.tpl_uid speUid,"
				+"      b.area speArea,"
				+"      b.area_code speAreaCode,"
				+"      b.isEnable speDetailEnable,"
				+"      b.express_Start speExpressStart,"
				+"      b.express_Postage speExpressPostage,"
				+"      b.express_Plus speExpressPlus,"
				+"      b.express_Postage_Plus speExpressPostagePlus "
				+"      FROM"
				+"      uc_freight_tpl_detail a  left join  uc_freight_tpl_detail b on  a.tpl_uid=b.tpl_uid   and b.area_code like '%"+areaCode+"%'  "
				+"      where a.tpl_uid in ( "+tplUids+" ) and a.area_code='global'";

		
		return querySql;

	}
	
	public static void main(String[] args){
		
		Map<String,String> map22 = new HashMap<String, String>();
		map22.put("uid",  "2be0a127cbab4f28b2634335e2f94dcb");
		List<Map<String, Object>> map2 = DbUp.upTable("uc_freight_tpl_detail").upTemplate().queryForList("select * from uc_freight_tpl where uid=:uid ", new MapSqlParameterSource(map22));
		//System.out.println(map2);
		
	}
	
	
	//创建试图   运费模板子关联  一次查询global 一次特定区域
	//修改说明 查询出global和其他区域的关联然后在过滤
//	CREATE
//	view usercenter.v_uc_freight_detail as
//SELECT
//	a.tpl_uid uid,
//	a.area,
//	a.area_code areaCode,
//	a.isEnable detailEnable,
//	a.express_Start expressStart,
//	a.express_Postage expressPostage,
//	a.express_Plus expressPlus,
//	a.express_Postage_Plus expressPostagePlus,
//	b.tpl_uid speUid,
//	b.area speArea,
//	b.area_code speAreaCode,
//	b.isEnable speDetailEnable,
//	b.express_Start speExpressStart,
//	b.express_Postage speExpressPostage,
//	b.express_Plus speExpressPlus,
//	b.express_Postage_Plus speExpressPostagePlus
//FROM
//	usercenter.uc_freight_tpl_detail a
//		LEFT JOIN
//		usercenter.uc_freight_tpl_detail b
//		ON
//		a.tpl_uid=b.tpl_uid and  a.uid <>b.uid
//WHERE
//	a.area_code='global' ;
	
	/**
	 * 获取商品运费
	 * @param productCodes		商品信息List，包含商品编码与商品数量
	 * @param areaCode			目的地区域代码
	 * @return
	 * @author ligj
	 * 2015/01/22 15:58
	 */
	public Map<String,BigDecimal> getProductFreight(List<PtInfo> ptInfos, String areaCode){
		
		if (StringUtils.isNotEmpty(areaCode)) {
			//将后四位替换为0000，代表省级单位
			areaCode = (areaCode.substring(0, areaCode.length()-4)+"0000");
		}
		
		Map<String,BigDecimal> resultMap = new HashMap<String,BigDecimal>();
		
		List<String> productCodes = new ArrayList<String>();
		//各商品的数量
		Map<String,Integer> productAmountMap = new HashMap<String,Integer>();
		for(PtInfo info:ptInfos){
			resultMap.put(info.getProductCode(), new BigDecimal(0)); 	//保证返回的结果里包含所有的商品
			
			productCodes.add("'"+info.getProductCode()+"'");
			productAmountMap.put(info.getProductCode(), info.getAccount());
		}
		//查询所有商品
		List<MDataMap> productList=  DbUp.upTable(PRODUCT_INFO).query("seller_code sellerCode,transport_template transportTemplate,product_code productCode,product_weight productWeight,product_volume productVolume", "", "  product_code in (  "+StringUtils.join(productCodes, ",")+")", null, 0, 0);
		
		ProductFrenghtModel prm=null,tmpPrm=null; 
		//店铺编码: 店铺code+商品code+模板code  为key的 对象 
		Map<String,ProductFrenghtModel> sellerMap = new HashMap<String, ProductFrenghtModel>();
		//模板编码
		Set<String> tmplateCodes = new HashSet<String>();
		for(MDataMap map:productList){
			try {
				//old 运费模板
//				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
//				tmplateCodes.add("'"+prm.getTransportTemplate()+"'");
//				String tmpKey = prm.getSellerCode()+":"+prm.getProductCode()+":"+prm.getTransportTemplate();
//				//设置数量
//				if(null!=productAmountMap.get(prm.getProductCode())){
//					prm.setAmount(productAmountMap.get(prm.getProductCode()));
//				}else{
//					prm.setAmount(0);
//				}
				
				//new 
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				tmplateCodes.add("'"+prm.getTransportTemplate()+"'");
				//key ： 店铺号+模版号  数量叠加
				String tmpKey = prm.getSellerCode()+":"+prm.getTransportTemplate();
				//设置数量
				Integer ct = productAmountMap.get(prm.getProductCode());
				if(null!=ct){ //
					if(null != sellerMap.get(tmpKey)){  //同一个店铺 模板 的商品数量叠加
						tmpPrm = sellerMap.get(tmpKey);
						prm.setAmount(ct+tmpPrm.getAmount());
					}else{
						prm.setAmount(productAmountMap.get(prm.getProductCode()));
					}
				}else{  //没有的设置-0
					prm.setAmount(0);
				}
				
				sellerMap.put(tmpKey, prm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return resultMap;
			}
			
		}
		//模板uid
		//用的的模板
		List<MDataMap> tplList=  DbUp.upTable(FREIGHT_TPL).query("uid,store_id sellerCode,isDisable tplDisable,is_free isFree,valuation_type valuationType", "", "  uid in (  "+StringUtils.join(tmplateCodes, ",")+")", null, 0, 0);
		for(MDataMap map:tplList){
			try {
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				for(ProductFrenghtModel pp:getModelFromMapBykey(sellerMap,map.get("uid"))){
					copyBeansPropertyNotNull(prm, pp);
//					sellerMap.put(map.get("uid"), pp);
				}
			} catch (Exception e) {
				return resultMap;
			}
			
		}
		
		
		
		//运费模板明细  
//		List<MDataMap> tplDetailList=  DbUp.upTable(PRODUCT_INFO).query("", "", "  uid in (  "+StringUtils.join(tmplateCodes, ",")  +") and areaCode ='"+of.getAreaCode()+"'", null, 0, 0);
	
		List<Map<String, Object>> tplDetailList = DbUp.upTable("uc_freight_tpl_detail").upTemplate().queryForList(doFreightDetailSql(areaCode,StringUtils.join(tmplateCodes, ",") ), new MapSqlParameterSource(null));
		
		for(Map map:tplDetailList){
			try {
				prm = (ProductFrenghtModel)convertMap(ProductFrenghtModel.class, map);
				//覆盖对象的运费信息
				for(ProductFrenghtModel pp:getModelFromMapBykey(sellerMap,(String)map.get("uid"))){
					copyBeansPropertyNotNull(prm, pp);
//					sellerMap.put((String)map.get("uid"), pp);
				}
			} catch (Exception e) {
				return resultMap;
			}
		}
		Map<String,StoreFreight> sfMap = new HashMap<String,StoreFreight>();
//		List<String> noSaleList = new ArrayList<String>();
		for(String key:sellerMap.keySet()){
			prm = sellerMap.get(key);
			String tmpMoney="";
			StoreFreight sf=null;
			if(sfMap.keySet().contains(prm.getSellerCode())){
				if(ProductFrenghtModel.TPL_DISABLE_Y.equals(prm.getTplDisable())){  //禁用
					continue;
				}else{
					sf = sfMap.get(prm.getSellerCode());
					tmpMoney = getProductMoney(prm);
					if(!SPE_AREA_NOT_SALE.equals(tmpMoney)){
//						sf.setFreight(DecimalByDouble(Double.valueOf(sf.getFreight())).add(DecimalByDouble(Double.valueOf(tmpMoney))).toString());  
						sfMap.put(prm.getSellerCode(),sf);
						resultMap.put(prm.getProductCode(),new BigDecimal(Double.valueOf(tmpMoney)));
					}else{
//						noSaleList.add(prm.getProductCode());
//						resultMap.put(prm.getProductCode(), new BigDecimal(-1));
					}
				}
			}else{
				sf = new StoreFreight();
				sf.setStroeCode(prm.getSellerCode());
				tmpMoney = getProductMoney(prm);
				if(!SPE_AREA_NOT_SALE.equals(tmpMoney)){
//					sf.setFreight(tmpMoney);
					sfMap.put(prm.getSellerCode(),sf);
					resultMap.put(prm.getProductCode(),new BigDecimal(Double.valueOf(tmpMoney)));
					
				}else{
//					noSaleList.add(prm.getProductCode());
//					resultMap.put(prm.getProductCode(), new BigDecimal(-1));
				}
			}
		}
		
//		result.setList(new ArrayList<StoreFreight>(sfMap.values()));
//		result.setNoSaleList(noSaleList);
		
		return resultMap;
	}
	
	
}
