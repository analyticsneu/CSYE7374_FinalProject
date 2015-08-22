package neu.finalproject;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class FinalProjectApplication {

	    @RequestMapping("/a")
	    @ResponseBody
	    String home() throws IOException {
	    	  // Instantiating configuration class
	        Configuration con = HBaseConfiguration.create();

	        // Instantiating HbaseAdmin class
	        HBaseAdmin admin = new HBaseAdmin(con);

	        // Instantiating table descriptor class
	        HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("emp"));

	        // Adding column families to table descriptor
	        tableDescriptor.addFamily(new HColumnDescriptor("personal"));
	        tableDescriptor.addFamily(new HColumnDescriptor("professional"));

	        // Execute the table through admin
	        admin.createTable(tableDescriptor);
	        System.out.println(" Table created ");
	    	
	        return "Hello World!";
	    }
	
    public static void main(String[] args) {
        SpringApplication.run(FinalProjectApplication.class, args);
    }
}
