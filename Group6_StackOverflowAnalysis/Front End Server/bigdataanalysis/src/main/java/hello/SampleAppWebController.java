package hello;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import kafka.Consumer;
import kafka.KafkaProperties;
import kafka.Producer;

import org.apache.log4j.Logger;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
// @RequestMapping("/receiveClickEventTest")
public class SampleAppWebController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
    private static Logger LOG = Logger.getLogger(SampleAppWebController.class);
    
	@RequestMapping(value = "/receiveClickEventTest", method = RequestMethod.GET)
	public String sayHello(
			@RequestParam(value = "name", required = false, defaultValue = "Stranger") String name,
			Model model) {
		model.addAttribute("name", name);
		System.out.println("------------Receive-------------");
	//	System.out.println(body);
		LOG.info("------Recevie----" + name);
		System.out.println(name);
		if(!name.equalsIgnoreCase("hello")){
			Producer producerThread = new Producer(KafkaProperties.topic);
			producerThread.content = name;
			producerThread.start();
		}
		return "greeting";
	}

	@RequestMapping(value = "/receiveClickEventTest", method = RequestMethod.POST )
	public String click(
			@RequestParam(value = "name", required = false, defaultValue = "Stranger") String name,
			Model model) {
		model.addAttribute("name", name);
		Consumer consumerThread = new Consumer(KafkaProperties.topic);
		consumerThread.start();
		return "success";
	}

}
