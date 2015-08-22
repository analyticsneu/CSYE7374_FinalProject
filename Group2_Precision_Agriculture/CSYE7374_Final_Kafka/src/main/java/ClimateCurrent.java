/**
 * Created by leonli0326 on 8/19/2015.
 */
public class ClimateCurrent {
    String station;
    String date;
    Double prcp;
    Double tmax;
    Double tmin;
    Double tavg;

    public ClimateCurrent(String station, String date, Double prcp, Double tmax, Double tmin, Double tavg) {
        this.station = station;
        this.date = date;
        this.prcp = prcp;
        this.tmax = tmax;
        this.tmin = tmin;
        this.tavg = tavg;
    }

    @Override
    public String toString() {
        return station+","+date+","+prcp+","+tmax+","+tmin+","+tavg;
    }
}
