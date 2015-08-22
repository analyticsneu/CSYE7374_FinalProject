package com.climate.bean;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NassData {
	List<NassRawRecord> data;

	public List<NassRawRecord> getData() {
		return data;
	}

	public void setData(List<NassRawRecord> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "NassData [data=" + data + "]";
	}

}
