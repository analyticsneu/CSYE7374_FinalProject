package neu.finalproject;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MainController {
      
	@RequestMapping("/index/{id}")
	@ResponseBody public Attributes getIndexPage (@PathVariable String id) throws IOException{
		Get g = new Get(Bytes.toBytes(id));
	      // Reading the data
	    Result result = table.get(g);
	    // Reading values from Result class object
	     byte [] views = result.getValue(Bytes.toBytes("attributes"),Bytes.toBytes("views"));
	     byte [] title = result.getValue(Bytes.toBytes("attributes"),Bytes.toBytes("title"));
	     byte [] url = result.getValue(Bytes.toBytes("attributes"),Bytes.toBytes("url"));
	     byte [] vote_post_count = result.getValue(Bytes.toBytes("attributes"),Bytes.toBytes("vote_post_count"));
	     byte [] answer = result.getValue(Bytes.toBytes("attributes"),Bytes.toBytes("answer"));
	     String views_ = Bytes.toString(views);
	     String title_ = Bytes.toString(title);
	     String url_ = Bytes.toString(url);
	     String vote_post_count_ = Bytes.toString(vote_post_count);
	     String answer_ = Bytes.toString(answer);
	     Attributes attributes = new Attributes();
	     attributes.setAnswer(answer_);
	     attributes.setViews(views_);
	     attributes.setTitle(title_);
	     attributes.setVote_post_count(vote_post_count_);
	     attributes.setUrl(url_);
	     
		 System.out.println("row id" + id);
	     return attributes;
	}
	
	
	@RequestMapping("/index")
	public String indexPage(){
		return "index";
	}
	
	private HTable table;
	public MainController() throws IOException{
		  // Instantiating Configuration class
	      Configuration config = HBaseConfiguration.create();

	      // Instantiating HTable class
	      table = new HTable(config, "questions_table");
	}
}
