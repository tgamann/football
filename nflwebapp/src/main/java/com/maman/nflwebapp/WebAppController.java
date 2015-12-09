package com.maman.nflwebapp;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // The @Controller annotation signals that this class contains web application paths
public class WebAppController {
	// private static final String _HOST = "localhost";
	private static final String _HOST = System.getenv("OPENSHIFT_MYSQL_DB_HOST");

    // This @RequestMapping annotation ensures that HTTP requests
    // to / are mapped to the index() method.
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index(@RequestParam(value="name", required=false, defaultValue="Tommy") String name, Model model) {
       	model.addAttribute("name", name);
        return "index";
    }

    @RequestMapping(value="/nfl", method=RequestMethod.GET)
    public String nfl(@RequestParam(value="name", required=false, defaultValue="Tommy") String name, Model model) {
        model.addAttribute("name", name);
    	return "nfl";
    }

    @RequestMapping(value="/weeks", method=RequestMethod.GET)
    public @ResponseBody List<Integer> weeks() {
        DataStore datastore = DataStore.getInstance(_HOST);
        return datastore.getWeeks();
    }
    
    @RequestMapping(value="/teams", method=RequestMethod.GET)
    public @ResponseBody List<String> teams() {
        DataStore datastore = DataStore.getInstance(_HOST);
        List<String> teams = datastore.getTeams();
        return teams;
    }

    @RequestMapping(value="/schedule", method=RequestMethod.GET)
    public @ResponseBody List<Game> schedule(@RequestParam(value="week", required=true) int week) {
        DataStore datastore = DataStore.getInstance(_HOST);
        List<Game> schedule = datastore.getSchedule(week);
        return schedule;
    }
    
}

// The web controller returns the string "index" when someone does GET / on your web site.
// Spring Boot has automatically added Thymeleaf beans to the application context to convert this
// into a request for the Thymeleaf template located at src/main/resources/templates/index.html.

