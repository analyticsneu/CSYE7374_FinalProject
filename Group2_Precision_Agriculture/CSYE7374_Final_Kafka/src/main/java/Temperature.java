import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by leonli0326 on 8/20/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature {
    @JsonProperty("temp")
    Double temp = 0.0;
    @JsonProperty("temp_min")
    Double temp_min = 0.0;
    @JsonProperty("temp_max")
    Double temp_max = 0.0;

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(Double temp_min) {
        this.temp_min = temp_min;
    }

    public Double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(Double temp_max) {
        this.temp_max = temp_max;
    }
}
