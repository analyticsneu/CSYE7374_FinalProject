<%@ page session="false"%>
<!DOCTYPE html>
<html>
<head>
<title>Climate Prediction</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1, user-scalable=no">
<style>
html, body {
	height: 100%;
	min-height: 2000px;
	padding-top: 20px;
}

#map {
	height: 600px;
}

.controls {
	margin-top: 10px;
	border: 1px solid transparent;
	border-radius: 2px 0 0 2px;
	box-sizing: border-box;
	-moz-box-sizing: border-box;
	height: 32px;
	outline: none;
	box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

#pac-input {
	background-color: #fff;
	font-family: Roboto;
	font-size: 15px;
	font-weight: 300;
	margin-left: 12px;
	padding: 0 11px 0 13px;
	text-overflow: ellipsis;
	width: 300px;
}

#pac-input:focus {
	border-color: #4d90fe;
}

.pac-container {
	font-family: Roboto;
}

#type-selector {
	color: #fff;
	background-color: #4d90fe;
	padding: 5px 11px 0px 11px;
}

#type-selector label {
	font-family: Roboto;
	font-size: 13px;
	font-weight: 300;
}

#target {
	width: 345px;
}
</style>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<script type="text/javascript">
	google.load('visualization', '1', {
		packages : [ 'corechart', 'line', 'bar' ]
	});
	// 	google.setOnLoadCallback(drawChart);

	function drawChart() {
		drawHistoryCharts(2010);

		drawForecastCharts(2020);

		$.getJSON("/app/commodityYield", {
			"commodity" : "CORN",
			"year" : 2010,
			"state" : document.getElementById('state_alpha').value
		}).done(function(data) {
			console.log(data);

			var yieldTable = new google.visualization.DataTable();

			yieldTable.addColumn('number', 'Year');
			yieldTable.addColumn('number', 'Yield');

			console.log("====================");
			console.log(data.length);
			for (var i = 0; i < data.length; i++) {
				yieldTable.addRow([ data[i].year, data[i].value ]);
			}

			var chartWrapper = new google.visualization.ChartWrapper({
				chartType : 'ColumnChart',
				dataTable : yieldTable,
				options : {
					'title' : 'Commodity Chart',
					'height' : 600,
					hAxis : {
						title : 'Year',
						format : '0'
					},
					vAxis : {
						title : 'Yeild ($)'
					}
				},
				containerId : 'commodity_chart'
			});

			chartWrapper.draw();

		}).fail(function(jqxhr, textStatus, error) {
			var err = textStatus + ", " + error;
			console.log("Request Failed: " + err);
			console.log(jqxhr);
		});

		$.getJSON("/app/comsumerIndex", {
			"year" : 2010
		}).done(function(data) {
			console.log(data);

			var table = new google.visualization.DataTable();

			table.addColumn('number', 'Year');
			table.addColumn('number', 'Comsumer Index');

			console.log("====================");
			console.log(data.length);
			for (var i = 0; i < data.length; i++) {
				table.addRow([ data[i].year, data[i].value - 100 ]);
			}

			var chartWrapper = new google.visualization.ChartWrapper({
				chartType : 'ColumnChart',
				dataTable : table,
				options : {
					'title' : 'Comsumer Index Chart',
					'height' : 600,
					hAxis : {
						title : 'Year',
						format : '0'
					},
					vAxis : {
						title : 'Comsumer Index'
					}
				},
				containerId : 'comsumer_index_chart'
			});

			chartWrapper.draw();

		}).fail(function(jqxhr, textStatus, error) {
			var err = textStatus + ", " + error;
			console.log("Request Failed: " + err);
			console.log(jqxhr);
		});

		drawRealTimeChart();
		var myVar = setInterval(function() {
			drawRealTimeChart()
		}, 60000);
	}

	function drawHistoryCharts(startYear) {
		$.getJSON("/app/climateDaily", {
			"station" : document.getElementById('station_id').value,
			"startYear" : startYear
		}).done(function(data) {
			//Temp table
			var tempTable = new google.visualization.DataTable();
			tempTable.addColumn('date', 'Date');
			tempTable.addColumn('number', 'Max Temperature');
			tempTable.addColumn('number', 'Min Temperature');
			tempTable.addColumn('number', 'Avg Temperature');

			// prcp table
			var prcpTable = new google.visualization.DataTable();
			prcpTable.addColumn('date', 'Date');
			prcpTable.addColumn('number', 'Precipitation')

			for (var i = 0; i < data.length; i++) {
				tempTable.addRow([ new Date(data[i].date), data[i].tmax, data[i].tmin, data[i].tavg ]);
				prcpTable.addRow([ new Date(data[i].date), data[i].prcp ]);
			}

			var tempChartWrapper = new google.visualization.ChartWrapper({
				chartType : 'LineChart',
				dataTable : tempTable,
				options : {
					title : 'Climate History',
					height : 600,
					hAxis : {
						title : 'Date'
					},
					vAxis : {
						title : 'Temperature (Celsius)',
						viewWindow : {
							max : 45,
							min : -20,
						}
					},
					legend : {
						position : 'bottom'
					}
				},
				containerId : 'climate_daily_chart'
			});

			var prcpChartWrapper = new google.visualization.ChartWrapper({
				chartType : 'LineChart',
				dataTable : prcpTable,
				options : {
					'title' : 'Precipitation Daily',
					'height' : 600

				},
				containerId : 'prcp_daily_chart'
			});

			tempChartWrapper.draw();
			prcpChartWrapper.draw();
		}).fail(function(jqxhr, textStatus, error) {
			var err = textStatus + ", " + error;
			console.log("Request Failed: " + err);
			console.log(jqxhr);
		});
	}

	function drawForecastCharts(endYear) {
		$.getJSON("/app/climateForecast", {
			"station" : document.getElementById('station_id').value,
			"endYear" : endYear
		}).done(function(data) {
			var tempTable = new google.visualization.DataTable();
			tempTable.addColumn('date', 'Date');
			tempTable.addColumn('number', 'Max Temperature');
			tempTable.addColumn('number', 'Min Temperature');
			tempTable.addColumn('number', 'Avg Temperature');

			var prcpTable = new google.visualization.DataTable();
			prcpTable.addColumn('date', 'Date');
			prcpTable.addColumn('number', 'Precipitation');

			for (var i = 0; i < data.length; i++) {
				tempTable.addRow([ new Date(data[i].date), data[i].tmax, data[i].tmin, data[i].tavg ]);
				prcpTable.addRow([ new Date(data[i].date), data[i].prcp ]);
			}

			var tempChartWrapper = new google.visualization.ChartWrapper({
				chartType : 'LineChart',
				dataTable : tempTable,
				options : {
					title : 'Climate Forecast',
					height : 600,
					hAxis : {
						title : 'Date'
					},
					vAxis : {
						title : 'Temperature (Celsius)',
						viewWindow : {
							max : 45,
							min : -20,
						}
					},
					legend : {
						position : 'bottom'
					}
				},
				containerId : 'climate_forecast_chart'
			});

			var prcpChartWrapper = new google.visualization.ChartWrapper({
				chartType : 'LineChart',
				dataTable : prcpTable,
				options : {
					'title' : 'Precipitation Forecast',
					'height' : 600

				},
				containerId : 'prcp_forecast_chart'
			});

			tempChartWrapper.draw();
			prcpChartWrapper.draw();

		}).fail(function(jqxhr, textStatus, error) {
			var err = textStatus + ", " + error;
			console.log("Request Failed: " + err);
			console.log(jqxhr);
		});
	}

	function drawRealTimeChart() {
		$.getJSON("/app/climateCurrent", {
			"station" : document.getElementById('station_id').value
		}).done(function(data) {
			console.log(data);

			var table = new google.visualization.DataTable();

			table.addColumn('datetime', 'Time');
			table.addColumn('number', 'Max Temperature');
			table.addColumn('number', 'Min Temperature');
			table.addColumn('number', 'Avg Temperature');

			console.log("====================");
			console.log(data.length);
			for (var i = 0; i < data.length; i++) {
				table.addRow([ new Date(data[i].date), data[i].tmax, data[i].tmin, data[i].tavg ]);
			}

			var chartWrapper = new google.visualization.ChartWrapper({
				chartType : 'LineChart',
				dataTable : table,
				options : {
					'title' : 'RealTime Temperature',
					'height' : 600,
					hAxis : {
						title : 'Time'
					},
					vAxis : {
						title : 'Temperature (Celsius)'
					}
				},
				containerId : 'climate_current_chart'
			});

			chartWrapper.draw();

		}).fail(function(jqxhr, textStatus, error) {
			var err = textStatus + ", " + error;
			console.log("Request Failed: " + err);
			console.log(jqxhr);
		});
	}

	function renderElements() {
		var startYearSelect = document.getElementById("history_start_year");
		var endYearSelect = document.getElementById("forcast_end_year");
		var startYear = 1981;
		var endYear = 2015;

		for (var i = startYear; i <= endYear; i++) {
			var startYearOption = document.createElement("option");
			startYearOption.value = i;
			startYearOption.text = i;
			startYearSelect.add(startYearOption);
		}

		for (var i = 0; i <= 5; i++) {
			var endYearOption = document.createElement("option");
			endYearOption.value = endYear;
			endYearOption.text = endYear;
			endYearSelect.add(endYearOption);
			endYear++;
		}

		startYearSelect.value = 2010;
		endYearSelect.value = 2020;

		$("#start_year_container").show();
		$("#end_year_container").show();
		$("#commodity_container").show();
	}

	function onHistoryStartYearChange(sel) {
		drawHistoryCharts(sel.value);
	}

	function onForecastEndYearChange(sel) {
		drawForecastCharts(sel.value);
	}
</script>
</head>
<body>
	<nav class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Climate Prediction</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a href="#">Map</a></li>
					<li><a href="#climate_charts">Climate</a></li>
					<li><a href="#commodity_chart">Commodity</a></li>
					<li><a href="#climate_current_chart">Real Time</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>

	<input type="hidden" id="station_id" value="USC00195246">
	<input type="hidden" id="state_alpha">

	<div class="container">
		<!-- Main component for a primary marketing message or call to action -->
		<div class="page-header">
			<h1>Pick a location from map</h1>
			<p class="lead">Pick a location from map to get climate and
				commodity yield info.</p>
		</div>
		<input id="pac-input" class="controls" type="text"
			placeholder="Search Box">
		<div id="map"></div>
	</div>

	<div class="container" id="chart">
		<div class="page-header">
			<h1>Google Chart</h1>
			<p class="lead" id="chart_info">This is a google chart.</p>
		</div>

		<div id="climate_charts">
			<div id="start_year_container" style="display: none">
				<label>History Start Year: &nbsp;&nbsp;</label><select
					id="history_start_year" onchange="onHistoryStartYearChange(this);">
				</select>
			</div>

			<div id="end_year_container" style="display: none">
				<label>Forecast End Year: &nbsp;&nbsp;</label><select
					id="forcast_end_year" onchange="onForecastEndYearChange(this);">
				</select>
			</div>

			<div id="climate_daily_chart"></div>

			<div id="climate_forecast_chart"></div>

			<div id="prcp_daily_chart"></div>

			<div id="prcp_forecast_chart"></div>
		</div>

		<div id="commodity_container">
			<div id="commodity_container" style="display: none">
				<label>Commodity Type: &nbsp;&nbsp;</label> <select
					id="forcast_end_year">
					<option>CORN</option>
				</select>
			</div>
			<div id="commodity_chart"></div>
		</div>

		<div id="comsumer_index_chart"></div>
		<div id="climate_current_chart"></div>
	</div>

	<script type="text/javascript">
		var myMarker;
		var markers = [];

		function placeMarkerAndPanTo(latLng, map) {
			if (myMarker) {
				myMarker.setPosition(latLng);
			} else {
				myMarker = new google.maps.Marker({
					position : latLng,
					map : map
				});
			}
			map.panTo(latLng);
			map.setCenter(myMarker.getPosition());
			getStationInfo(latLng, map);
		}

		function getStationInfo(latLng, map) {
			$.getJSON("/app/stationInfo", {
				"latitude" : latLng.G,
				"longitude" : latLng.K
			}).done(function(data) {
				document.getElementById('chart_info').innerHTML = "Station - " + data.station + "</p>State - " + data.state + "";
				$.getJSON("/app/clusteringStations", {
					"station" : data.station
				}).done(function(list) {

					console.log("station list length = " + list.length);
					// Clear out the old markers.
					markers.forEach(function(marker) {
						marker.setMap(null);
					});
					markers = [];

					// For each place, get the icon, name and location.
					var bounds = new google.maps.LatLngBounds();
					list.forEach(function(item) {
						// Create a marker for each place.
						var sMarker = new google.maps.Marker({
							map : map,
							title : 'Station: ' + item.station + '\nState : ' + item.state,
							position : {
								lat : item.latitude,
								lng : item.longitude
							}
						});
						markers.push(sMarker);
						google.maps.event.addListener(sMarker, 'click', function() {
							placeMarkerAndPanTo(sMarker.position, map);
						});

						bounds.extend(sMarker.position);
					});
					map.fitBounds(bounds);
				}).fail(function(jqxhr, textStatus, error) {
					var err = textStatus + ", " + error;
					console.log("Request Failed: " + err);
					console.log(jqxhr);
				});

				console.log(data);
				document.getElementById('station_id').value = data.station;
				document.getElementById('state_alpha').value = data.state;

				drawChart();
				renderElements();
			}).fail(function(jqxhr, textStatus, error) {
				var err = textStatus + ", " + error;
				console.log("Request Failed: " + err);
				console.log(jqxhr);
			});
		}

		// This example adds a search box to a map, using the Google Place Autocomplete
		// feature. People can enter geographical searches. The search box will return a
		// pick list containing a mix of places and predicted search terms.

		function initAutocomplete() {
			var myLatlng = {
				lat : 39.74,
				lng : -96.855
			};
			var map = new google.maps.Map(document.getElementById('map'), {
				center : myLatlng,
				zoom : 5,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			});

			// Create the search box and link it to the UI element.
			var input = document.getElementById('pac-input');
			var searchBox = new google.maps.places.SearchBox(input);
			map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

			map.addListener('click', function(e) {
				placeMarkerAndPanTo(e.latLng, map);
			});

			// Bias the SearchBox results towards current map's viewport.
			map.addListener('bounds_changed', function() {
				searchBox.setBounds(map.getBounds());
			});

			// 			var markers = [];
			// [START region_getplaces]
			// Listen for the event fired when the user selects a prediction and retrieve
			// more details for that place.
			searchBox.addListener('places_changed', function() {
				var places = searchBox.getPlaces();

				if (places.length == 0) {
					return;
				}

				// Clear out the old markers.
				markers.forEach(function(marker) {
					marker.setMap(null);
				});
				markers = [];

				// For each place, get the icon, name and location.
				var bounds = new google.maps.LatLngBounds();
				places.forEach(function(place) {
					var icon = {
						url : place.icon,
						size : new google.maps.Size(71, 71),
						origin : new google.maps.Point(0, 0),
						anchor : new google.maps.Point(17, 34),
						scaledSize : new google.maps.Size(25, 25)
					};

					// Create a marker for each place.
					var marker = new google.maps.Marker({
						map : map,
						icon : icon,
						title : place.name,
						position : place.geometry.location
					});
					markers.push(marker);
					google.maps.event.addListener(marker, 'click', function() {
						placeMarkerAndPanTo(marker.position, map);
					});

					if (place.geometry.viewport) {
						// Only geocodes have viewport.
						bounds.union(place.geometry.viewport);
					} else {
						bounds.extend(place.geometry.location);
					}
				});
				map.fitBounds(bounds);
			});
			// [END region_getplaces]
		}
	</script>
	<script
		src="https://maps.googleapis.com/maps/api/js?libraries=places&callback=initAutocomplete"
		async defer></script>
</body>
</html>