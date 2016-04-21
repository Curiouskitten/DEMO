package com.itheima.im3.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

public class ProtocalObj implements Serializable{
	public String toXml() {
		XStream x = new XStream();
		x.alias(this.getClass().getSimpleName(), this.getClass());
		// ②　调用toXml 或者fromXml
		return x.toXML(this);

	}

	public Object fromXml(String xml) {
		XStream x = new XStream();
		x.alias(this.getClass().getSimpleName(), this.getClass());
		// ②　调用toXml 或者fromXml
		return x.fromXML(xml);
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);

	}

	public Object fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, this.getClass());
	}
}
