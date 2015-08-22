package com.climate.bean;

public class Station {
	private String station;
	private String name;
	private String state;
	private Double latitude;
	private Double longitude;
	private Double elevation;

	public Station() {
	}

	public Station(String station, String name, String state, Double latitude, Double longitude, Double elevation) {
		super();
		this.station = station;
		this.name = name;
		this.state = state;
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	@Override
	public String toString() {
		return "Station [station=" + station + ", name=" + name + ", state=" + state + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", elevation=" + elevation + "]";
	}
}
