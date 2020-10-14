package com.cmall.ordercenter.webfunc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.ordercenter.util.MoneyUtil;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webexport.RootExport;

/**
 * @description: 订单发货单批量导出为zip压缩包 
 *
 * @author Yangcl
 * @date 2017年3月30日 下午2:19:40 
 * @version 1.0.0
 */
public class OrderShipBillExportRar extends RootExport {
	

	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		MDataMap mReqMap = convertRequest(request);
		String orderCodes = mReqMap.get("order_code_list");
		String [] arr = orderCodes.split(",");  //  order code 数组
		List<String> list = new ArrayList<String>(); 
		for(int i = 0 ; i < arr.length ; i ++){
			String orderCode = arr[i];
			list.add(this.initFile(request, response, orderCode));
		}
		this.exportZip(response , list); 
	}
	
	/**
	 * @description: 准备生成zip压缩包，并返回给前端，同时删除服务器目录下的word文档。
	 *
	 * @author Yangcl
	 * @date 2017年3月30日 下午7:23:40 
	 * @version 1.0.0.1
	 */
	private void exportZip(HttpServletResponse res ,  List<String> list){  //  , OutputStream zipOutputStream
		ZipOutputStream out = null;
		FileInputStream fis = null;
		try {
			String exportZipName = FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
			res.setContentType("application/binary;charset=UTF-8");
			res.setHeader("Content-disposition", "attachment; filename=" + exportZipName + ".zip");// 组装附件名称和格式
			out = new ZipOutputStream(res.getOutputStream());  // 创建zip输出流
			byte[] buffer = new byte[1024];
			for(String s : list)	{
				File file = new File(s);
				if(file.exists()){
					fis = new FileInputStream(file); 
					s = s.split("zip")[1];
					s = s.substring(1, s.length());
					out.putNextEntry(new ZipEntry(s));
					int len = 0 ;
					// 读取文件的内容,打包到zip文件    
					while ((len = fis.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
				}
	            out.flush();
	            out.closeEntry();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(out != null){
					out.close();
					fis.close();  
				}
				
				// 删除服务器目录下的word文档
				for(String s : list)	{
					File file = new File(s);
					if(file.exists()){
						System.out.println(file.getName() );
						boolean flag = file.delete(); 
						int count = 1;
						while(!flag){
							flag = file.delete();
							count ++; 
							if(count > 100000){   // 上线删除次数，解决文件无法删除问题
								System.out.println(file.getName() + " 删除失败");  
								break;
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); 
			}
			 
		}
	}
	
	
	/**
	 * @description: 实例化批量中的每一个word文档    
	 *
	 * @author Yangcl
	 * @date 2017年3月30日 下午5:05:05 
	 * @version 1.0.0.1
	 */
	private String initFile(HttpServletRequest request , HttpServletResponse response , String orderCode){
		Order order = new OrderService().getOrder( orderCode ); 
		Document document = null;
		OutputStream outputStream = null;
		String url = "";
		try {
			String exportName = order.getOrderCode()+"-" + FormatHelper.upDateTime(new Date(), "yyyy-MM-dd-HH-mm-ss");
			String path = "resources" + File.separator + "zip" + File.separator + exportName + ".doc";
			url = request.getSession().getServletContext().getRealPath("/") + File.separator + path; 
			outputStream = new FileOutputStream(url); 
			
			document = new Document(PageSize.A4);
			RtfWriter2.getInstance(document, outputStream);
			document.open();
			Image headImage = createImg("jyhProductDetail.jpg");
			headImage.scalePercent(86f, 88f);
			BaseFont bfChinese = BaseFont.createFont();
			Table billTable = new Table(1, 7);			
			billTable.setBorder(0);
			billTable.setPadding(0);
			billTable.setSpacing(0);
			billTable.setWidth(100);
			Cell cell1 = new Cell(headImage);
			billTable.addCell(cell1);
			cell1.setBorder(0);
			cell1.setHorizontalAlignment(Cell.ALIGN_CENTER);
			billTable.endHeaders();
			Font font02 = new Font(bfChinese, 12f, Font.BOLD);
			Chunk chunk02 = new Chunk("\n\n        订购信息", font02);
			Cell cell2 = new Cell(chunk02);
			cell2.setBorder(0);
			billTable.addCell(cell2);
			Cell cell3 = new Cell(createBookInfo(order));
			cell3.setBorder(0);
			billTable.addCell(cell3);
			Chunk chunk04 = new Chunk("\n\n        商品信息", font02);
			Cell cell4 = new Cell(chunk04);
			cell4.setBorder(0);
			billTable.addCell(cell4);
			Cell cell5 = new Cell(createProductInfo(order));
			cell5.setBorder(0);
			billTable.addCell(cell5);
			Cell cell6 = new Cell(createFocus(bfChinese));
			cell6.setBorder(0);
			billTable.addCell(cell6);
			Image bottomImage = createImg("jyhkfphone.jpg");
			bottomImage.scalePercent(86f, 88f);
			Cell bottomCell = new Cell(bottomImage);
			bottomCell.setBorder(0);
			billTable.addCell(bottomCell);
			document.add(billTable);
		}catch(Exception e){
			e.printStackTrace();
			bLogError(0, e.getMessage());
		}finally{
			close(document);
			close(outputStream);
		}
		
		return url;  
	}
 
	
	public void close(Document document){
		if(document != null){
			document.close();
		}
	}
	
	public void close(OutputStream outputStream){
		if(outputStream != null){
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				bLogError(0, e.getMessage());
			}
		}
	}
	
	public Table createBookInfo(Order order) throws BadElementException{
		Table bookTable = new Table(5, 2);
		OrderAddress orderAddress = order.getAddress();
		bookTable.setWidth(100f);
		Cell cell01 = new Cell("订单号");
		cell01.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell02 = new Cell("客户姓名");
		cell02.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell03 = new Cell("联系电话");
		cell03.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell04 = new Cell("收货地址");
		cell04.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell05 = new Cell("订单备注");
		cell05.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell11 = new Cell(orderAddress.getOrderCode());
		cell11.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell12 = new Cell(orderAddress.getReceivePerson());
		cell12.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell13 = new Cell(orderAddress.getMobilephone());
		cell13.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell14 = new Cell(getAreaName(orderAddress.getAreaCode())+orderAddress.getAddress());
		cell14.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell15 = new Cell(orderAddress.getRemark());
		cell15.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		bookTable.addCell(cell01);
		bookTable.addCell(cell02);
		bookTable.addCell(cell03);
		bookTable.addCell(cell04);
		bookTable.addCell(cell05);
		bookTable.addCell(cell11);
		bookTable.addCell(cell12);
		bookTable.addCell(cell13);
		bookTable.addCell(cell14);
		bookTable.addCell(cell15);
		return bookTable;
	}
	
	
	public Table createProductInfo(Order order) throws BadElementException{
		Table bookTable = new Table(4);
		bookTable.setWidth(100f);
		Cell cell01 = new Cell("商品编号");
		cell01.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell02 = new Cell("商品名称");
		cell02.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		cell02.setColspan(2);
		Cell cell03 = new Cell("数量");
		cell03.setVerticalAlignment(Cell.ALIGN_MIDDLE);
//		Cell cell04 = new Cell("价格");
//		cell04.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		bookTable.addCell(cell01);
		bookTable.addCell(cell02);
		bookTable.addCell(cell03);
		//bookTable.addCell(cell04);
		int count = 0;
		List<OrderDetail> detailList = order.getProductList();
		
		for (OrderDetail orderDetail : detailList) {
			if(StringUtils.equals(orderDetail.getGiftFlag(), "1")){
				Cell cellProduct1 = new Cell(orderDetail.getProductCode());
				Cell cellProduct2 = new Cell(orderDetail.getSkuName());
				Cell cellProduct3 = new Cell(Integer.toString(orderDetail.getSkuNum()));
//				Cell cellProduct4 = new Cell(orderDetail.getSkuPrice().toString());
				
				cellProduct2.setColspan(2);
				
				count = count+orderDetail.getSkuNum();
				bookTable.addCell(cellProduct1);
				bookTable.addCell(cellProduct2);
				bookTable.addCell(cellProduct3);
				//bookTable.addCell(cellProduct4); // 发货单隐藏商品实际的价格
			}
		}
		
		Cell cell21 = new Cell("赠品");
		cell21.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell22 = new Cell(convertGiftName(order));
		cell22.setColspan(3);
		cell22.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		bookTable.addCell(cell21);
		bookTable.addCell(cell22);
		Cell cell31 = new Cell("合计数量");
		cell31.setRowspan(2);
		cell31.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell32 = new Cell(Integer.toString(count));
		cell32.setRowspan(2);
		cell32.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell33 = new Cell("应收货款（大写）\n        （小写）");
		cell33.setRowspan(2);
		cell33.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		Cell cell34 = new Cell(MoneyUtil.toChinese(order.getDueMoney().toString()));		
		cell34.setVerticalAlignment(Cell.ALIGN_MIDDLE);		
		Cell cell44 = new Cell(order.getDueMoney().toString());
		cell44.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		bookTable.addCell(cell31);
		bookTable.addCell(cell32);
		bookTable.addCell(cell33);
		bookTable.addCell(cell34);
		bookTable.addCell(cell44);
		
		return bookTable;
	}
	
	
	public Image createImg(String fileName) throws BadElementException, MalformedURLException, IOException{
		String baseDir = new TopDir().upServerletPath("resources/images/");
		Image headImage = Image.getInstance(baseDir+fileName);
		headImage.setAlignment(Image.ALIGN_CENTER);
		headImage.setAlignment(Image.UNDERLYING);
		headImage.setBorder(0);
		headImage.setAbsolutePosition(0, 0);
		
		return headImage;
	}
	
	public Table createFocus(BaseFont bfChinese) throws Exception{
		Table focusTable = new Table(4, 8);
		focusTable.setBorder(0);
		focusTable.setPadding(0);
		focusTable.setSpacing(0);
		focusTable.setWidth(100f);
		Font font01 = new Font(bfChinese, 13f, Font.BOLD);
		Font font02 = new Font(bfChinese, 9f, Font.BOLD);
		Chunk chunk1 = new Chunk("     惠家有关注方式：", font01);
		
		Cell cell1 = new Cell(chunk1);
		cell1.setBorder(0);
		cell1.setColspan(4);
		focusTable.addCell(cell1);
		Chunk chunk2 = new Chunk("        1. 扫描右方二维码下载安装惠家有app，海量超值商品任您选。", font02);
		Cell cell2 = new Cell(chunk2);
		cell2.setBorder(0);
		cell2.setColspan(3);
		focusTable.addCell(cell2);
		Image qrcodeImage = createImg("jyhqrcode.png");
		qrcodeImage.scalePercent(45f, 45f);
		Cell qrcodeCell = new Cell(qrcodeImage);
		qrcodeCell.setBorder(0);
		qrcodeCell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
		qrcodeCell.setRowspan(4);
		focusTable.addCell(qrcodeCell);
		Chunk chunk3 = new Chunk("        2. 登录惠家有官方网站：http://www.huijiayou.cn，在首页右下方扫描惠家有二维码,\n            首次下载可获666元大礼包，来就送！", font02);
		Cell cell3 = new Cell(chunk3);
		cell3.setBorder(0);
		cell3.setColspan(3);
		focusTable.addCell(cell3);
		Chunk chunk4 = new Chunk("        3. 登录微信公众号搜索 “惠家有” 添加关注即可。", font02);
		Cell cell4 = new Cell(chunk4);
		cell4.setBorder(0);
		cell4.setColspan(3);
		focusTable.addCell(cell4);
		Cell cell5 = new Cell();
		cell5.setBorder(0);
		cell5.setColspan(3);
		focusTable.addCell(cell5);
		Cell cell6 = new Cell();
		cell6.setBorder(0);
		cell6.setColspan(4);
		focusTable.addCell(cell6);
		
		return focusTable;
	}
	
	public String convertGiftName(Order order){
		List<OrderDetail> detailList = order.getProductList();
		String giftName = "";
		for (OrderDetail orderDetail : detailList) {
			/*赠品*/
			if(StringUtils.equals(orderDetail.getGiftFlag(), "0")){
				giftName = giftName+"、"+orderDetail.getSkuName();
			}
		}
		if(giftName.length() > 1){
			giftName = giftName.substring(1);
		}
		
		return giftName;
	}
	
	public String getAreaName(String code){
		String areaName = "";
		if(code.length() == 6){
			areaName = areaName + queryAreaName(code.substring(0, 2)+"0000");
			areaName = areaName + queryAreaName(code.substring(0, 4)+"00");
			areaName = areaName + queryAreaName(code);
		}else if(code.length() == 9){
			areaName = areaName + queryAreaName(code.substring(0, 2)+"0000");
			areaName = areaName + queryAreaName(code.substring(0, 4)+"00");
			areaName = areaName + queryAreaName(code.substring(0, 6));
			areaName = areaName + queryAreaName(code);
		}
		
		return areaName;
	}
	
	
	public String queryAreaName(String areaCode){
		MDataMap mDataMap = DbUp.upTable("sc_tmp").one("code",areaCode,"show_yn","Y");
		String areaName = "";
		if(mDataMap != null){
			areaName = mDataMap.get("name");
		}
		return areaName;
	}
}








































