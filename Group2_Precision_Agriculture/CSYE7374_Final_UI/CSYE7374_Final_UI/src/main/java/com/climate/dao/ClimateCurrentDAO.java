package com.climate.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.climate.bean.CliamteCurrent;

public class ClimateCurrentDAO {
	@SuppressWarnings("deprecation")
	public List<CliamteCurrent> getClimateDaily(String station) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ec2-52-20-252-81.compute-1.amazonaws.com:3306/data",
					"root", "");

			long date = Calendar.getInstance().getTime().getTime();
			Timestamp startTs = new Timestamp(date);
			startTs.setHours(0);
			startTs.setMinutes(0);
			startTs.setSeconds(0);
			startTs.setNanos(0);
			Timestamp endTs = new Timestamp(date);
			endTs.setHours(23);
			endTs.setMinutes(59);
			endTs.setSeconds(59);
			endTs.setNanos(0);
			Statement statement = conn.createStatement();
			String sql = "select * from climateCurrent where station = '" + station + "' and date >= '" + startTs
					+ "' and date <= '" + endTs + "' order by date;";
			System.out.println(sql);
			ResultSet rs = statement.executeQuery(sql);

			List<CliamteCurrent> result = new ArrayList<CliamteCurrent>();
			while (rs.next()) {
				result.add(new CliamteCurrent(rs.getString("station"), rs.getTimestamp("date"), rs.getDouble("prcp"),
						rs.getDouble("tmax"), rs.getDouble("tmin"), rs.getDouble("tavg")));
			}

			conn.close();
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
