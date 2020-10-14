package com.cmall.productcenter.common;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.CallableStatementCreator;

import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcProductpic;
import com.cmall.productcenter.model.PcProductproperty;
import com.cmall.productcenter.model.PcSkupic;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basehelper.JsonHelper;

public class ProductCallableStatement implements CallableStatementCreator {

	PcProductinfo product = null;

	
	/*
	 * 0 添加，1 修改
	 */
	private int flag =0;
	
	public ProductCallableStatement(PcProductinfo product,int flag) {
		this.product=product;
		this.flag = flag;
	}

	public CallableStatement createCallableStatement(Connection con)
			throws SQLException {
		final String callProcedureSql = "{call proc_add_product(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
				" ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)}";
		CallableStatement cstmt = con.prepareCall(callProcedureSql);
		cstmt.registerOutParameter(1, Types.VARCHAR);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		
		cstmt.setString(3, this.product.getProductCode());
		cstmt.setString(4, this.product.getProdutName());
		cstmt.setString(5, this.product.getSellerCode() == null ?"":this.product.getSellerCode());
		cstmt.setString(6, this.product.getBrandCode());
		cstmt.setBigDecimal(7, this.product.getProductWeight());
		cstmt.setInt(8, this.product.getFlagSale());
		cstmt.setString(9, this.product.getCategory() == null ?"":this.product.getCategory().getCategoryCode());
		
		if(this.product.getDescription()!=null)
			cstmt.setString(10, this.product.getDescription().getDescriptionInfo());
		else
			cstmt.setString(10,"");
		
		StringBuffer picUrlStr = new StringBuffer();
		
		if(this.product.getPcPicList()!=null)
		{
			for (int i = 0; i < this.product.getPcPicList().size(); i++) {
				PcProductpic od = this.product.getPcPicList().get(i);
				od.setProductCode(product.getProductCode());
				picUrlStr.append(od.getPicUrl());

				if (i != this.product.getPcPicList().size() - 1)
					picUrlStr.append(SkuCommon.FirstSplitStr);
			}
		}
		cstmt.setString(11, picUrlStr.toString());
		StringBuffer productPropertyStr = new StringBuffer();

		if(this.product.getPcProductpropertyList()!=null)
		{
			for (int i = 0; i < this.product.getPcProductpropertyList().size(); i++) {
				PcProductproperty od = this.product.getPcProductpropertyList().get(i);
				od.setProductCode(product.getProductCode());
				productPropertyStr.append(od.getPropertyKeycode() + SkuCommon.SecondSplitStr + od.getPropertyCode() + SkuCommon.SecondSplitStr + od.getPropertyKey() + SkuCommon.SecondSplitStr + od.getPropertyValue()+ SkuCommon.SecondSplitStr + od.getPropertyType());
				if (i != this.product.getPcProductpropertyList().size() - 1)
					productPropertyStr.append(SkuCommon.FirstSplitStr);
			}
		}
		
		
		
		cstmt.setString(12, productPropertyStr.toString());
		StringBuffer skuStr = new StringBuffer();
		
		if(this.product.getProductSkuInfoList()!=null)
		{
			for (int i = 0; i < this.product.getProductSkuInfoList().size(); i++) {
				ProductSkuInfo od = this.product.getProductSkuInfoList().get(i);
				od.setProductCode(product.getProductCode());
				skuStr.append(
						od.getSkuCode() + SkuCommon.SecondSplitStr 
						+ od.getSellPrice() + SkuCommon.SecondSplitStr 
						+ od.getMarketPrice() + SkuCommon.SecondSplitStr 
						+ od.getStockNum() + SkuCommon.SecondSplitStr 
						+ od.getSkuKey()+ SkuCommon.SecondSplitStr 
						+ od.getSkuValue()+ SkuCommon.SecondSplitStr 
						+ od.getSkuPicUrl()+ SkuCommon.SecondSplitStr 
						+ od.getSkuName()+ SkuCommon.SecondSplitStr 
						+ od.getSellProductcode() + SkuCommon.SecondSplitStr
						+ od.getSecurityStockNum() + SkuCommon.SecondSplitStr
						+ od.getSkuAdv().replace(SkuCommon.SecondSplitStr, "").replace(SkuCommon.FirstSplitStr, ""));
				
				
				if (i != this.product.getProductSkuInfoList().size() - 1)
					skuStr.append(SkuCommon.FirstSplitStr);
			}
		}
		
		
		cstmt.setString(13, skuStr.toString());
		
		
		cstmt.setString(14, SkuCommon.FirstSplitStr);
		cstmt.setString(15, SkuCommon.SecondSplitStr);
		cstmt.setInt(16, this.flag);
		if(product.getDescription()!=null)
			cstmt.setString(17, product.getDescription().getKeyword());
		else
			cstmt.setString(17, "");
		
		if(product.getPcProdcutflow() != null){
			cstmt.setString(18, product.getPcProdcutflow().getFlowStatus());
			cstmt.setString(19, product.getPcProdcutflow().getFlowCode());
		}else{
			cstmt.setString(18, "");
			cstmt.setString(19, "");
		}
		
		//if(flag == 0){
			JsonHelper<PcProductinfo> pHelper=new JsonHelper<PcProductinfo>();
			cstmt.setString(20, pHelper.ObjToString(product));
		//}else{
			//cstmt.setString(20, "");
		//}
		

		if(product.getPcProdcutflow() != null){
			cstmt.setString(21, product.getPcProdcutflow().getUpdator());
		}else{
			cstmt.setString(21, "");
		}
		
	
		cstmt.setString(22, String.valueOf(product.getMarketPrice()));
		cstmt.setString(23, String.valueOf(product.getMinSellPrice()));
		cstmt.setString(24, String.valueOf(product.getMaxSellPrice()));
		cstmt.setString(25, String.valueOf(product.getProductVolume()));
		cstmt.setString(26, product.getTransportTemplate());
		cstmt.setString(27, product.getSellProductcode());
		cstmt.setString(28, product.getMainPicUrl());
		cstmt.setString(29, product.getLabels() == null ?"":product.getLabels());
		cstmt.setInt(30, product.getFlagPayway());
		cstmt.setString(31, product.getProductVolumeItem());
		cstmt.setString(32, product.getProductStatus());
		
		return cstmt;
	}

}
