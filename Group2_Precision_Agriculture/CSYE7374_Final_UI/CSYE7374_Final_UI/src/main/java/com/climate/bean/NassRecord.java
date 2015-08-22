package com.climate.bean;

public class NassRecord {
	private String commodity;
	private Integer year;
	private String state;
	private String domain;
	private Long value;

	public NassRecord() {

	}

	public NassRecord(NassRawRecord record) {
		this.commodity = record.getCommodity_desc();
		this.year = record.getYear();
		this.state = record.getState_alpha();
		this.domain = record.getDomain_desc();
		this.value = Long.parseLong(record.getValue().replace(",", ""));
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "NassRecord [commodity=" + commodity + ", year=" + year + ", state=" + state + ", domain=" + domain
				+ ", value=" + value + "]";
	}

}
