package com.climate.bean;

import java.sql.Date;

public class ClimateDaily {
	private String station;
	private Date date;
	private Double prcp;
	private Double tmax;
	private Double tmin;
	private Double tavg;

	public ClimateDaily() {
		
	}
	
	public ClimateDaily(String station, Date date, Double prcp, Double tmax, Double tmin, Double tavg) {
		super();
		this.station = station;
		this.date = date;
		this.prcp = prcp;
		this.tmax = tmax;
		this.tmin = tmin;
		this.tavg = tavg;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getPrcp() {
		return prcp;
	}

	public void setPrcp(Double prcp) {
		this.prcp = prcp;
	}

	public Double getTmax() {
		return tmax;
	}

	public void setTmax(Double tmax) {
		this.tmax = tmax;
	}

	public Double getTmin() {
		return tmin;
	}

	public void setTmin(Double tmin) {
		this.tmin = tmin;
	}

	public Double getTavg() {
		return tavg;
	}

	public void setTavg(Double tavg) {
		this.tavg = tavg;
	}

	@Override
	public String toString() {
		return "ClimateDaily [station=" + station + ", date=" + date + ", prcp=" + prcp + ", tmax=" + tmax + ", tmin="
				+ tmin + ", tavg=" + tavg + "]";
	}

}
