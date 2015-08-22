package com.climate.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.climate.bean.NassRecord;
import com.climate.bean.NassData;
import com.climate.bean.NassRawRecord;
import com.climate.util.JsonUtil;

public class NassService {
	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
		String s = "3,905,136,000";
		Integer i = Integer.valueOf(s.replaceAll(",", ""));
		System.out.println(i);

		// NassService http = new NassService();

		// System.out.println("Testing 1 - Send Http GET request");
		// http.get("CORN", "MA", 2015);

	}

	public List<NassRecord> getCommodityYield(String commodity, String state, Integer year) {
		String url = "http://quickstats.nass.usda.gov/api/api_GET/?key=619C8A99-5D26-3615-A96A-1C6C27BD8868&commodity_desc="
				+ commodity + "&year_" + "_GE=" + year + "&state_alpha=" + state
				+ "&domain_desc=TOTAL&freq_desc=ANNUAL&agg_level_desc=STATE&statisticcat_desc=PRODUCTION&unit_desc=$&util_practice_desc=GRAIN";
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

			NassData data = (NassData) JsonUtil.deserialize(response.toString(), NassData.class);
			System.out.println(data);

			List<NassRecord> yields = new ArrayList<NassRecord>();
			for (NassRawRecord record : data.getData()) {
				yields.add(new NassRecord(record));
			}

			return yields;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public List<NassRecord> getComsumerIndex(Integer year) {
		String url = "http://quickstats.nass.usda.gov/api/api_GET/?key=619C8A99-5D26-3615-A96A-1C6C27BD8868&source_desc=SURVEY&commodity_desc=COMMODITY%20TOTALS&sector_desc=ECONOMICS&group_desc=INCOME&statisticcat_desc=INDEX%20FOR%20PRICE%20RECEIVED,%202011&freq_desc=ANNUAL&year_"
				+ "_GE=" + year;
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

			NassData data = (NassData) JsonUtil.deserialize(response.toString(), NassData.class);
			System.out.println(data);

			List<NassRecord> records = new ArrayList<NassRecord>();
			for (NassRawRecord record : data.getData()) {
				records.add(new NassRecord(record));
			}

			return records;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
