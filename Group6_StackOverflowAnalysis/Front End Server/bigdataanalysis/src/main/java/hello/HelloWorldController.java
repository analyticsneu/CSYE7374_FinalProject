package hello;

import java.util.concurrent.atomic.AtomicLong;

import kafka.KafkaProperties;
import kafka.Producer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping("/receiveClickEvent") //url
public class HelloWorldController {
    private static Logger Log = Logger.getLogger(HelloWorldController.class); 
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    @RequestMapping(value="/receiveClickEvent",method=RequestMethod.GET)
    public @ResponseBody Greeting sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
    	Log.info("/receiveClickEvent was called");
    	return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    
    @RequestMapping(value="/receiveClickEvent/success", method=RequestMethod.GET)
    public @ResponseBody Greeting click(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
    	return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody Greeting sayHelloPOST(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    } 

}
