package com.maman.nflwebapp;

//This file and WebAppXml.java are mutually exclusive; that is, although it is
// possible to package this service as a traditional WAR file for deployment to an
// external application server, the simpler approach demonstrated below creates a
// stand-alone application. You package everything in a single, executable JAR file,
// driven by a good old Java main() method.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

    // The main() method defers to the SpringApplication helper class, providing Application.class
    // as an argument to its run() method. This tells Spring to read the annotation metadata from
    // Application and to manage it as a component in the Spring application context.
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


// The @ComponentScan annotation tells Spring to search recursively through the com.maman.webapp1 package and its
// children for classes marked directly or indirectly with Spring’s @Component annotation. This directive
// ensures that Spring finds and registers the WebAppController, because it is marked with @Controller,
// which in turn is a kind of @Component annotation.

// The @EnableAutoConfiguration annotation switches on reasonable default behaviors based on the content
// of your classpath. For example, because the application depends on the embeddable version of Tomcat
// (tomcat-embed-core.jar), a Tomcat server is set up and configured with reasonable defaults on your behalf.
// And because the application also depends on Spring MVC (spring-webmvc.jar), a Spring MVC DispatcherServlet
// is configured and registered for you — no web.xml necessary!