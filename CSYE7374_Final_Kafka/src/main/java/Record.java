import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by leonli0326 on 8/20/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {
    @JsonProperty("main")
    Temperature main = new Temperature();
    @JsonProperty("rain")
    Rain rain = new Rain();

    public Temperature getMain() {
        return main;
    }

    public void setMain(Temperature main) {
        this.main = main;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }
}
