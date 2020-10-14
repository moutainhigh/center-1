package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 *  城市类
 * @author yangrong
 * date 2014-9-11
 * @version 1.0
 */
public class City implements Comparable<Object>{

	@ZapcomApi(value="城市id")
	private String Id  = "";
	
	@ZapcomApi(value="城市名称")
	private String name = "";

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(Object obj) {
		
		if(this ==obj){
            return 0;            
        }else if (obj!=null && obj instanceof City) {   
        	City city = (City) obj; 
            if(Integer.parseInt(Id) <= Integer.parseInt(city.Id)){
               return -1;
            }else{
               return 1;
            }
       }else{
         return -1;
       }
	}
}
