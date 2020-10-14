package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryProperties {

	private String properties_code = "";
	
	private String properties_name = "";
	
	private String is_must = "";
	
	private String properties_value_type = "";
	
	private String properties_value_code = "";
	
	private String properties_value = "";
	
	private List<PropertiesValue> list = new ArrayList<PropertiesValue>();

	public String getProperties_value_code() {
		return properties_value_code;
	}

	public void setProperties_value_code(String properties_value_code) {
		this.properties_value_code = properties_value_code;
	}

	public String getProperties_value() {
		return properties_value;
	}

	public void setProperties_value(String properties_value) {
		this.properties_value = properties_value;
	}

	public String getProperties_code() {
		return properties_code;
	}

	public void setProperties_code(String properties_code) {
		this.properties_code = properties_code;
	}

	public String getProperties_name() {
		return properties_name;
	}

	public void setProperties_name(String properties_name) {
		this.properties_name = properties_name;
	}

	public String getIs_must() {
		return is_must;
	}

	public void setIs_must(String is_must) {
		this.is_must = is_must;
	}

	public String getProperties_value_type() {
		return properties_value_type;
	}

	public void setProperties_value_type(String properties_value_type) {
		this.properties_value_type = properties_value_type;
	}

	public List<PropertiesValue> getList() {
		return list;
	}

	public void setList(List<PropertiesValue> list) {
		this.list = list;
	}
	
}
