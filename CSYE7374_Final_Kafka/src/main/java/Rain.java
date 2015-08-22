import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by leonli0326 on 8/20/2015.
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Rain {
    @JsonProperty("3h")
    Double rain3h = 0.0;
    @JsonProperty("1h")
    Double rain1h = 0.0;

    public Double getRain3h() {
        return rain3h;
    }

    public void setRain3h(Double rain3h) {
        this.rain3h = rain3h;
    }

    public Double getRain1h() {
        return rain1h;
    }

    public void setRain1h(Double rain1h) {
        this.rain1h = rain1h;
    }
}
