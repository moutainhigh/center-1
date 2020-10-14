package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.kjt.RsyncGetKjtProductChannelInventoryById;
import com.cmall.groupcenter.kjt.model.ChannelProductPageModel;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 向跨境通发送数据
 * product_ids商品ID，多个商品ID用英文逗号（,）分隔，最多20个商品ID
 * saleChannelSysNo 渠道编号
 * @author zmm
 *
 */
public class JobForKtjChannelInventory extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		// TODO Auto-generated method stub
		//List<Map<String, Object>> list = DbUp.upTable("pc_productinfo").dataSqlList("SELECT DISTINCT product_code_old from pc_productinfo where product_status='4497153900060002' and  (seller_code=:seller_code1 or  seller_code=:seller_code3 or  seller_code=:seller_code4) ", new MDataMap("seller_code3",MemberConst.MANAGE_CODE_HOMEHAS,"seller_code1",MemberConst.MANAGE_CODE_APP,"seller_code4",MemberConst.MANAGE_CODE_HPOOL));
		String product_id="";
		String product_ids="";
		String newproduct_id="";
		int totalpage;
		String infosql="select product_code_old from pc_productinfo where seller_code='SI2003' and small_seller_code='SF03KJT' ";
		List<Map<String, Object>> list=DbUp.upTable("pc_productinfo").dataSqlList(infosql, null);
		ChannelProductPageModel cpp = new ChannelProductPageModel(list,20);
		totalpage=cpp.getTotalPages();
		if(list!=null){
			for (int i = 0; i < totalpage; i++) {
				String lockCode = WebHelper.addLock(1, "JobForKtjChannelInventory156554");//跨境通接口请求时间限制为500ms，现在锁1秒
				if (StringUtils.isNotBlank(lockCode)) {
					StringBuffer sb =new StringBuffer();
					int startNum = (i * 20);
					int endNum = (i + 1) * 20;
					for (int j = startNum; j < endNum; j++) {
						if(j >= list.size())
							break;
						product_id = (String)list.get(j).get("product_code_old");
						newproduct_id = product_id.replaceAll("'", "");
						sb.append(newproduct_id).append(",");
					}
					product_ids=sb.toString().substring(0, sb.toString().length()-1);
					RsyncGetKjtProductChannelInventoryById rsyncChannel=new RsyncGetKjtProductChannelInventoryById();
					rsyncChannel.upRsyncRequest().setProductIDs(product_ids);
					rsyncChannel.upRsyncRequest().setSaleChannelSysNo(bConfig("groupcenter.rsync_kjt_SaleChannelSysNo"));
					rsyncChannel.doRsync();
				}else{
					i--;
				}
			}
		}
	}

}
