package com.cmall.newscenter.webfunc;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.PcProductdescription;
import com.cmall.productcenter.model.PcProductproperty;

public class ProductDetails {

	/**
	 * 图文详情
	 * 
	 * @param descriptionInfo
	 * @return
	 */
	public static String getProductUrl(PcProductdescription pd) {

		StringBuffer sBuffer = new StringBuffer();

		// if(pd!=null||!"".equals(descriptionInfo)){

		sBuffer.append("<html>");

		sBuffer.append("<head>");

		sBuffer.append("<meta charset=" + "utf-8" + ">");

		sBuffer.append("<meta name=" + "viewport" + " content="
				+ "width=device-width, initial-scale=1.0, user-scalable=no"
				+ ">");

		sBuffer.append("<style>");

		// sBuffer.append("body{font-family:Helvetica,arial,sans-serif;font-size:14px;line-height:1.6;padding-top:10px;padding-bottom:10px;background-color:#fff;padding:20px}body > :first-child{margin-top:0!important}body > :last-child{margin-bottom:0!important}a{color:#4183C4}a.absent{color:#c00}a.anchor{display:block;padding-left:30px;margin-left:-30px;cursor:pointer;position:absolute;top:0;left:0;bottom:0}h1,h2,h3,h4,h5,h6{margin:20px 0 10px;padding:0;font-weight:700;-webkit-font-smoothing:antialiased;cursor:text;position:relative;word-break:break-word}h1:hover a.anchor,h2:hover a.anchor,h3:hover a.anchor,h4:hover a.anchor,h5:hover a.anchor,h6:hover a.anchor{background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA09pVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoMTMuMCAyMDEyMDMwNS5tLjQxNSAyMDEyLzAzLzA1OjIxOjAwOjAwKSAgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OUM2NjlDQjI4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OUM2NjlDQjM4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5QzY2OUNCMDg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5QzY2OUNCMTg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PsQhXeAAAABfSURBVHjaYvz//z8DJYCRUgMYQAbAMBQIAvEqkBQWXI6sHqwHiwG70TTBxGaiWwjCTGgOUgJiF1J8wMRAIUA34B4Q76HUBelAfJYSA0CuMIEaRP8wGIkGMA54bgQIMACAmkXJi0hKJQAAAABJRU5ErkJggg==) no-repeat 10px center;text-decoration:none}h1 tt,h1 code{font-size:inherit}h2 tt,h2 code{font-size:inherit}h3 tt,h3 code{font-size:inherit}h4 tt,h4 code{font-size:inherit}h5 tt,h5 code{font-size:inherit}h6 tt,h6 code{font-size:inherit}h1{font-size:28px;color:#000}h2{font-size:24px;color:#000}h3{font-size:18px}h4{font-size:16px}h5{font-size:14px}h6{color:#777;font-size:14px}p,blockquote,ul,ol,dl,li,table,pre{margin:15px 0}hr{background:transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAECAYAAACtBE5DAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OENDRjNBN0E2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OENDRjNBN0I2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4Q0NGM0E3ODY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4Q0NGM0E3OTY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PqqezsUAAAAfSURBVHjaYmRABcYwBiM2QSA4y4hNEKYDQxAEAAIMAHNGAzhkPOlYAAAAAElFTkSuQmCC) repeat-x 0 0;border:0 none;color:#ccc;height:4px;padding:0}body > h2:first-child{margin-top:0;padding-top:0}body > h1:first-child{margin-top:0;padding-top:0}body > h1:first-child + h2{margin-top:0;padding-top:0}body > h3:first-child,body > h4:first-child,body > h5:first-child,body > h6:first-child{margin-top:0;padding-top:0}a:first-child h1,a:first-child h2,a:first-child h3,a:first-child h4,a:first-child h5,a:first-child h6{margin-top:0;padding-top:0}h1 p,h2 p,h3 p,h4 p,h5 p,h6 p{margin-top:0}li p.first{display:inline-block}li{margin:0}ul,ol{padding-left:30px}ul :first-child,ol :first-child{margin-top:0}dl{padding:0}dl dt{font-size:14px;font-weight:700;font-style:italic;padding:0;margin:15px 0 5px}dl dt:first-child{padding:0}dl dt > :first-child{margin-top:0}dl dt > :last-child{margin-bottom:0}dl dd{margin:0 0 15px;padding:0 15px}dl dd > :first-child{margin-top:0}dl dd > :last-child{margin-bottom:0}blockquote{border-left:4px solid #ddd;padding:0 15px;color:#777}blockquote > :first-child{margin-top:0}blockquote > :last-child{margin-bottom:0}table{padding:0;border-collapse:collapse;table-layout:fixed}table tr{border-top:1px solid #ccc;background-color:#fff;margin:0;padding:0}table tr:nth-child(2n){background-color:#f8f8f8}table tr th{font-weight:700;border:1px solid #ccc;text-align:left;margin:0;white-space:nowarp;word-break:break-all；;word-break:keep-all;padding:6px 13px}table tr td{border:1px solid #ccc;text-align:left;margin:0;white-space:nowarp;word-break:break-all；;word-break:keep-all;padding:6px 13px}table tr th :first-child,table tr td :first-child{margin-top:0}table tr th :last-child,table tr td :last-child{margin-bottom:0;width:200px}img{max-width:100%}code,tt{margin:0 2px;padding:0 5px;white-space:nowrap;border:1px solid #eaeaea;background-color:#f8f8f8;border-radius:3px}pre code{margin:0;padding:0;white-space:pre;border:none;background:transparent}pre{background-color:#f8f8f8;border:1px solid #ccc;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px;border-radius:3px}pre code,pre tt{background-color:transparent;border:none}@media screen and (min-width: 914px){body{width:854px;margin:0 auto}}");

		sBuffer.append(".img-responsive {  width: 100%; height: auto; }");
		sBuffer.append("</style>");

		sBuffer.append("</head>");

		sBuffer.append("<body><div>");

		String[] sPicList = StringUtils.split(pd.getDescriptionPic(), "|");

		for (String sPic : sPicList) {

			if (StringUtils.isNotEmpty(sPic)) {
				sBuffer.append("<img class=\"img-responsive\" src=\"" + sPic
						+ "\" />");
			}
		}

		// sBuffer.append(""+descriptionInfo+"");

		sBuffer.append("</div></body>");

		sBuffer.append("</html>");
		// }

		return sBuffer.toString();

	}

	/**
	 * 产品参数
	 * 
	 * @return
	 */
	public static String getDetailUrl(String produtName, String categoryName,
			BigDecimal productVolume,
			List<PcProductproperty> pcProductpropertyList) {

		StringBuffer sBuffer = new StringBuffer();

		sBuffer.append("<html>");

		sBuffer.append("<head>");

		sBuffer.append("<meta charset=" + "utf-8" + ">");

		sBuffer.append("<meta name=" + "viewport" + " content="
				+ "width=device-width, initial-scale=1.0, user-scalable=no"
				+ ">");

		sBuffer.append("<style>");

	//	sBuffer.append("body{font-family:Helvetica,arial,sans-serif;font-size:14px;line-height:1.6;padding-top:10px;padding-bottom:10px;background-color:#fff;padding:20px}body > :first-child{margin-top:0!important}body > :last-child{margin-bottom:0!important}a{color:#4183C4}a.absent{color:#c00}a.anchor{display:block;padding-left:30px;margin-left:-30px;cursor:pointer;position:absolute;top:0;left:0;bottom:0}h1,h2,h3,h4,h5,h6{margin:20px 0 10px;padding:0;font-weight:700;-webkit-font-smoothing:antialiased;cursor:text;position:relative;word-break:break-word}h1:hover a.anchor,h2:hover a.anchor,h3:hover a.anchor,h4:hover a.anchor,h5:hover a.anchor,h6:hover a.anchor{background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA09pVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoMTMuMCAyMDEyMDMwNS5tLjQxNSAyMDEyLzAzLzA1OjIxOjAwOjAwKSAgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OUM2NjlDQjI4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OUM2NjlDQjM4ODBGMTFFMTg1ODlEODNERDJBRjUwQTQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5QzY2OUNCMDg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5QzY2OUNCMTg4MEYxMUUxODU4OUQ4M0REMkFGNTBBNCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PsQhXeAAAABfSURBVHjaYvz//z8DJYCRUgMYQAbAMBQIAvEqkBQWXI6sHqwHiwG70TTBxGaiWwjCTGgOUgJiF1J8wMRAIUA34B4Q76HUBelAfJYSA0CuMIEaRP8wGIkGMA54bgQIMACAmkXJi0hKJQAAAABJRU5ErkJggg==) no-repeat 10px center;text-decoration:none}h1 tt,h1 code{font-size:inherit}h2 tt,h2 code{font-size:inherit}h3 tt,h3 code{font-size:inherit}h4 tt,h4 code{font-size:inherit}h5 tt,h5 code{font-size:inherit}h6 tt,h6 code{font-size:inherit}h1{font-size:28px;color:#000}h2{font-size:24px;color:#000}h3{font-size:18px}h4{font-size:16px}h5{font-size:14px}h6{color:#777;font-size:14px}p,blockquote,ul,ol,dl,li,table,pre{margin:15px 0}hr{background:transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAAECAYAAACtBE5DAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OENDRjNBN0E2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OENDRjNBN0I2NTZBMTFFMEI3QjRBODM4NzJDMjlGNDgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo4Q0NGM0E3ODY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo4Q0NGM0E3OTY1NkExMUUwQjdCNEE4Mzg3MkMyOUY0OCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PqqezsUAAAAfSURBVHjaYmRABcYwBiM2QSA4y4hNEKYDQxAEAAIMAHNGAzhkPOlYAAAAAElFTkSuQmCC) repeat-x 0 0;border:0 none;color:#ccc;height:4px;padding:0}body > h2:first-child{margin-top:0;padding-top:0}body > h1:first-child{margin-top:0;padding-top:0}body > h1:first-child + h2{margin-top:0;padding-top:0}body > h3:first-child,body > h4:first-child,body > h5:first-child,body > h6:first-child{margin-top:0;padding-top:0}a:first-child h1,a:first-child h2,a:first-child h3,a:first-child h4,a:first-child h5,a:first-child h6{margin-top:0;padding-top:0}h1 p,h2 p,h3 p,h4 p,h5 p,h6 p{margin-top:0}li p.first{display:inline-block}li{margin:0}ul,ol{padding-left:30px}ul :first-child,ol :first-child{margin-top:0}dl{padding:0}dl dt{font-size:14px;font-weight:700;font-style:italic;padding:0;margin:15px 0 5px}dl dt:first-child{padding:0}dl dt > :first-child{margin-top:0}dl dt > :last-child{margin-bottom:0}dl dd{margin:0 0 15px;padding:0 15px}dl dd > :first-child{margin-top:0}dl dd > :last-child{margin-bottom:0}blockquote{border-left:4px solid #ddd;padding:0 15px;color:#777}blockquote > :first-child{margin-top:0}blockquote > :last-child{margin-bottom:0}table{padding:0;border-collapse:collapse;table-layout:fixed}table tr{border-top:1px solid #ccc;background-color:#fff;margin:0;padding:0}table tr:nth-child(2n){background-color:#f8f8f8}table tr th{font-weight:700;border:1px solid #ccc;text-align:left;margin:0;white-space:nowarp;word-break:break-all；;word-break:keep-all;padding:6px 13px}table tr td{border:1px solid #ccc;text-align:left;margin:0;white-space:nowarp;word-break:break-all；;word-break:keep-all;padding:6px 13px}table tr th :first-child,table tr td :first-child{margin-top:0}table tr th :last-child,table tr td :last-child{margin-bottom:0;width:200px}img{max-width:100%}code,tt{margin:0 2px;padding:0 5px;white-space:nowrap;border:1px solid #eaeaea;background-color:#f8f8f8;border-radius:3px}pre code{margin:0;padding:0;white-space:pre;border:none;background:transparent}pre{background-color:#f8f8f8;border:1px solid #ccc;font-size:13px;line-height:19px;overflow:auto;padding:6px 10px;border-radius:3px}pre code,pre tt{background-color:transparent;border:none}@media screen and (min-width: 914px){body{width:854px;margin:0 auto}}");
		sBuffer.append("body{font-family:Helvetica,arial,sans-serif;font-size:1.2rem;line-height:2;padding-top:10px;padding-bottom:10px;}");

		
		sBuffer.append("</style>");

		sBuffer.append("</head>");

		sBuffer.append("<body>");

		if (produtName != null || !"".equals(produtName)) {

			sBuffer.append("【商品名称】");

			sBuffer.append("" + produtName + "");

		}

		if (categoryName != null || !"".equals(categoryName)) {

			sBuffer.append("<br>");

			sBuffer.append("【分类】");

			sBuffer.append("" + categoryName + "");

		}

		if (pcProductpropertyList.size() != 0) {

			for (int i = 0; i < pcProductpropertyList.size(); i++) {

				PcProductproperty productproperty = pcProductpropertyList
						.get(i);

				if (productproperty.getPropertyKey() != null
						|| !"".equals(productproperty.getPropertyKey())
						|| productproperty.getPropertyValue() != null
						|| !"".equals(productproperty.getPropertyValue())) {

					sBuffer.append("<br>");

					sBuffer.append("【" + productproperty.getPropertyKey() + "】");

					sBuffer.append("" + productproperty.getPropertyValue() + "");

				}

			}
		}

		sBuffer.append("</body>");

		sBuffer.append("</html>");

		return sBuffer.toString();

	}

}
