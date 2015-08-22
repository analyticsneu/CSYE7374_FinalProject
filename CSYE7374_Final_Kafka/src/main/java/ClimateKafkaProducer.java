import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by leonli0326 on 8/19/2015.
 */
public class ClimateKafkaProducer {
//    Properties props = new Properties();
    private KafkaProducer<String, String> producer;

    public ClimateKafkaProducer(){
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092,localhost:9092 ");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "example.producer.SimplePartitioner");
        producer = new KafkaProducer<String, String>(props, new StringSerializer(), new StringSerializer());
    }

    public void sendMessage(String k, String v){
        producer.send(new ProducerRecord<String, String>("weather", k, v));
    }

    private void saveToDB(String weather, String dbConnectPoint){
        String[] attr = weather.split(",");
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://"+dbConnectPoint+"/data", "dataAdmin", "");
            PreparedStatement stmt = conn.prepareStatement("insert ignore into climateCurrent values (?,?,?,?,?,?)");
            stmt.setString(1, attr[0]);
            stmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTime().getTime()));
            stmt.setDouble(3, Double.parseDouble(attr[2])*100);
            stmt.setDouble(4, Double.parseDouble(attr[3]));
            stmt.setDouble(5, Double.parseDouble(attr[4]));
            stmt.setDouble(6, Double.parseDouble(attr[5]));
            stmt.execute();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if(args.length<1){
            System.err.println("Must specify Database entry point!");
            return;
        }
        String dbConnectPoint = args[0];
        ClimateKafkaProducer producer = new ClimateKafkaProducer();

        while(true){
            List<Location> locations = LocationAPI.getAllPOI();
            for(Location location: locations) {
                String weather = WeatherAPI.getWeather(location);
                producer.saveToDB(weather, dbConnectPoint);
                producer.sendMessage(location.station, weather);
                Thread.sleep(5000);
            }
        }
    }


}
