package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.ApiTryOrderTrialInput;
import com.cmall.newscenter.model.ApplyProductTrialResult;
import com.cmall.newscenter.model.ProductGroup;
import com.cmall.newscenter.model.SaleOrder;
import com.cmall.newscenter.model.SaleProductGroup;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.model.Torder;
import com.cmall.newscenter.service.TxTryOrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 试用商品在线下单
 * @author shiyz
 * date 2014-8-26
 */
public class ApplyProductTrialApi extends RootApiForToken<ApplyProductTrialResult, ApiTryOrderTrialInput>{

	public ApplyProductTrialResult Process(ApiTryOrderTrialInput inputParam,
			MDataMap mRequestMap) {
		
		ApplyProductTrialResult result = new ApplyProductTrialResult();
		
		
		if(result.upFlagTrue()){
			
			TxTryOrderService txTryOrderService = new TxTryOrderService();	
			
			Torder  Torder = txTryOrderService.taddOrder(getUserCode(), inputParam.getProduct(), inputParam.getAddress(), inputParam.getAmount(), getManageCode(), new RootResult(),"");
			
			SaleOrder saleOrder = new SaleOrder();
			
			// 查询商品及数量
			List<ProductGroup> products=new ArrayList<ProductGroup>();
			/*返回商品*/
			List<SaleProductGroup> saleProducts = new ArrayList<SaleProductGroup>();
			
			
			if(Torder!=null){
				
				saleOrder.setCreate_time(Torder.getCreate_time());
				
				saleOrder.setId(Torder.getId());
				
				saleOrder.setOrder_id(Torder.getOrder_id());
				
				saleOrder.setState(0);
				
				saleOrder.setOrder_description(Torder.getOrder_description());
				
				saleOrder.setTotal(Torder.getTotal());
				
				products = Torder.getProducts();
				
				if(products.size()!=0){
					
					for(int i=0;i<products.size();i++){
						
						SaleProductGroup saleProduct =new SaleProductGroup();
						
						ProductGroup product = products.get(i);
						
						saleProduct.setAmout(product.getAmount());
						
						
						saleProduct.getProduct().setId(product.getProduct().getId());
						
						
						saleProduct.getProduct().setDetail_url(product.getProduct().getParam_url());
						
						
						saleProduct.getProduct().setIntro(product.getProduct().getIntro());
						
						
						
						saleProducts.add(saleProduct);
						
					}
					
					
					
				}
				}
				
			}
			
			
		
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
