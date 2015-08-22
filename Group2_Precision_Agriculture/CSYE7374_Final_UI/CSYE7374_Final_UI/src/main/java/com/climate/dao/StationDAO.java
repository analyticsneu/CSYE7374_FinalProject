package com.climate.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.climate.bean.Station;

public class StationDAO {
	public Station getNearestStation(Double latitude, Double longitude) {
		Station station = null;
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ec2-52-20-252-81.compute-1.amazonaws.com:3306/data", "root", "");

			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(
					"select m.station, m.name, m.state, m.latitude, m.longitude, m.elevation from stationMeta m order by geography_distance(location, 'point("
							+ longitude + " " + latitude + ")') limit 1;");

			if (rs.next()) {
				station = new Station(rs.getString("station"), rs.getString("name"), rs.getString("state"), rs.getDouble("latitude"), rs.getDouble("longitude"),
						rs.getDouble("elevation"));
			}
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return station;
	}
	
	public List<Station> getClusteringStations(String station) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ec2-52-20-252-81.compute-1.amazonaws.com:3306/data",
					"root", "");

			Statement statement = conn.createStatement();
			String sql = "select s.* from climateClustering c left join stationMeta s on c.station=s.station where c.climateGroup=(select climateGroup from climateClustering where station='"
					+ station + "') limit 25;";
			
			System.out.println("clustering sql:\n" + sql);
			ResultSet rs = statement.executeQuery(sql);

			List<Station> list = new ArrayList<Station>();
			while (rs.next()) {
				list.add(new Station(rs.getString("station"), rs.getString("name"), rs.getString("state"),
						rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getDouble("elevation")));
			}

			conn.close();
			System.out.println("Clustering station size = " + list.size());
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
