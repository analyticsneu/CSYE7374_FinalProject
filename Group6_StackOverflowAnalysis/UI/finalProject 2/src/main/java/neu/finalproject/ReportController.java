package neu.finalproject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ReportController {

	@RequestMapping("/report")
	public String report(){
		return "report";
	}
	
	
	@RequestMapping("/topDiscussed")
	public String topDiscussed(){
		return "TopDiscussed";
	}
	
	@RequestMapping("/topViewed")
	public String topViewed(){
		return "TopViewed";
	}
	
	@RequestMapping("/topAnswered")
	public String topAnswered(){
		return "TopAnswered";
	}
	
	@RequestMapping("/topUpvote")
	public String topUpvote(){
		return "TopUpvote";
	}
	
	@RequestMapping("/association")
	public String association(){
		return "AssociationAnalysis";
	}
	
	@RequestMapping("/association/button1")
	public String associationButton1(){
		return "Button1Page";
	}
	
	@RequestMapping("/association/button2")
	public String associationButton2(){
		return "Button1Page";
	}
	
	@RequestMapping("/association/button3")
	public String associationButton3(){
		return "Button1Page";
	}
	
	
}
