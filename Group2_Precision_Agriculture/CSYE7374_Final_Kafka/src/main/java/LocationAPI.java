
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonli0326 on 8/19/2015.
 */
public class LocationAPI {

    public static List<Location> getAllPOI(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://ec2-52-20-252-81.compute-1.amazonaws.com:3306/data","dataAdmin","");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select station, longitude, latitude from stationMeta order by station");
            List<Location> result = new ArrayList<Location>();
            while(rs.next()){
                result.add(new Location(rs.getString("station"), rs.getDouble("longitude"), rs.getDouble("latitude")));
            }
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
