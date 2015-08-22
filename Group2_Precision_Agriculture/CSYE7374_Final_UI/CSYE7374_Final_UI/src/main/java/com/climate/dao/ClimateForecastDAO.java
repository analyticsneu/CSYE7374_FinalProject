package com.climate.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.climate.bean.ClimateDaily;

public class ClimateForecastDAO {
	public List<ClimateDaily> getClimateForecast(String station, Integer endYear) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ec2-52-20-252-81.compute-1.amazonaws.com:3306/data",
					"root", "");

			Statement statement = conn.createStatement();
			String sql = "select * from climateForecast where station = '" + station + "' and year(date) <= "+ endYear + " order by date;";
			System.out.println(sql);
			ResultSet rs = statement.executeQuery(sql);

			List<ClimateDaily> result = new ArrayList<ClimateDaily>();
			while (rs.next()) {
				result.add(new ClimateDaily(rs.getString("station"), rs.getDate("date"), rs.getDouble("prcp"),
						rs.getDouble("tmax"), rs.getDouble("tmin"), rs.getDouble("tavg")));
			}

			conn.close();
			System.out.println("forecast : " + result.size());
			return result;

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
