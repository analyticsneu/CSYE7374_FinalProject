package com.climate.bean;

import java.sql.Timestamp;

public class CliamteCurrent {
	private String station;
	private Timestamp date;
	private Double prcp;
	private Double tmax;
	private Double tmin;
	private Double tavg;

	public CliamteCurrent() {

	}

	public CliamteCurrent(String station, Timestamp date, Double prcp, Double tmax, Double tmin, Double tavg) {
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

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
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
		return "CliamteCurrent [station=" + station + ", date=" + date + ", prcp=" + prcp + ", tmax=" + tmax + ", tmin="
				+ tmin + ", tavg=" + tavg + "]";
	}

}
