package com.climate.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NassRawRecord {
	private String commodity_desc;
	private Integer year;
	private String state_alpha;
	private String domain_desc;
	@JsonProperty("Value")
	private String value;

	public String getCommodity_desc() {
		return commodity_desc;
	}

	public void setCommodity_desc(String commodity_desc) {
		this.commodity_desc = commodity_desc;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getState_alpha() {
		return state_alpha;
	}

	public void setState_alpha(String state_alpha) {
		this.state_alpha = state_alpha;
	}

	public String getDomain_desc() {
		return domain_desc;
	}

	public void setDomain_desc(String domain_desc) {
		this.domain_desc = domain_desc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "NassRawData [commodity_desc=" + commodity_desc + ", year=" + year + ", state_alpha=" + state_alpha
				+ ", domain_desc=" + domain_desc + ", Value=" + value + "]";
	}
}
