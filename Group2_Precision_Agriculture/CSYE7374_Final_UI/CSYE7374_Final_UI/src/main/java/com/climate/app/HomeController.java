package com.climate.app;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.climate.bean.CliamteCurrent;
import com.climate.bean.ClimateDaily;
import com.climate.bean.NassRecord;
import com.climate.bean.Station;
import com.climate.dao.ClimateCurrentDAO;
import com.climate.dao.ClimateDailyDAO;
import com.climate.dao.ClimateForecastDAO;
import com.climate.dao.StationDAO;
import com.climate.service.NassService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		// Date date = new Date();
		// DateFormat dateFormat =
		// DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG,
		// locale);
		//
		// String formattedDate = dateFormat.format(date);
		//
		// model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	@RequestMapping(value = "/stationInfo", method = RequestMethod.GET)
	public @ResponseBody Station getStationInfo(@RequestParam("latitude") Double latitude,
			@RequestParam("longitude") Double longitude) {
		logger.info("Getting station info for location: {}, {}", latitude, longitude);

		Station station = new StationDAO().getNearestStation(latitude, longitude);

		return station;
	}

	@RequestMapping(value = "/commodityYield", method = RequestMethod.GET)
	public @ResponseBody List<NassRecord> getCommodityYield(@RequestParam("commodity") String commodity,
			@RequestParam("state") String state, @RequestParam("year") Integer year) {

		List<NassRecord> list = new NassService().getCommodityYield(commodity, state, year);

		System.out.println("commodity list size = " + list.size());

		return list;

	}

	@RequestMapping(value = "/climateForecast", method = RequestMethod.GET)
	public @ResponseBody List<ClimateDaily> getClimateForecast(@RequestParam("station") String station,
			@RequestParam("endYear") Integer endYear) {
		logger.info("Getting Climate Forecast for station - {}", station);

		return new ClimateForecastDAO().getClimateForecast(station, endYear);
	}

	@RequestMapping(value = "/climateDaily", method = RequestMethod.GET)
	public @ResponseBody List<ClimateDaily> getClimateDaily(@RequestParam("station") String station,
			@RequestParam("startYear") Integer startYear) throws Exception {
		List<ClimateDaily> list = new ClimateDailyDAO().getClimateDaily(station, startYear);
		return list;
	}

	@RequestMapping(value = "/clusteringStations", method = RequestMethod.GET)
	public @ResponseBody List<Station> getClusteringStations(@RequestParam("station") String station) {
		List<Station> list = new StationDAO().getClusteringStations(station);
		return list;
	}

	@RequestMapping(value = "/comsumerIndex", method = RequestMethod.GET)
	public @ResponseBody List<NassRecord> getComsumerIndex(Integer year) {
		List<NassRecord> list = new NassService().getComsumerIndex(year);

		System.out.println("cosumer index:\n" + list.toString());

		return list;
	}

	@RequestMapping(value = "/climateCurrent", method = RequestMethod.GET)
	public @ResponseBody List<CliamteCurrent> getClimateCurrent(String station) {
		List<CliamteCurrent> list = new ClimateCurrentDAO().getClimateDaily(station);
		return list;
	}
}
