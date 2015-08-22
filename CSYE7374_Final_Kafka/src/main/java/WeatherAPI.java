import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by leonli0326 on 8/19/2015.
 */
public class WeatherAPI {

    private static final String apiKey = "ac8cab62420a664a835d0b6255be61b2";
    private static final String apiUrl = "http://api.openweathermap.org/data/2.5/weather";

    public static String getWeather(Location location) {
        try {
            URI uri = new URIBuilder(apiUrl)
                    .addParameter("units", "metric")
                    .addParameter("lat", location.latitude.toString())
                    .addParameter("lon", location.longitude.toString())
                    .addParameter("APPID", apiKey).build();
            System.out.println(uri.toString());
            String json = Request.Get(uri).execute().returnContent().asString();
            System.out.println(json);
            ObjectMapper mapper = new ObjectMapper();
            Record record = mapper.readValue(json, Record.class);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            return new ClimateCurrent(
                    location.station,
                    date,
                    Math.max(record.rain.rain3h / 3, record.rain.rain1h),
                    record.main.temp_max,
                    record.main.temp_min,
                    record.main.temp).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Location l = new Location("USC00012813",  -85.4981,  40.8556);
        System.out.println(getWeather(l));
    }

}
