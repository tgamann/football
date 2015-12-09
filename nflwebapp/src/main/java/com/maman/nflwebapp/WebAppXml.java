package com.maman.nflwebapp;

// This file and Application.java are mutually exclusive; that is, by converting this
// project into a WAR file, we no longer need public static void main(), but we now
// need HellloWebXml as an alternative to web.xml.

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

// WebAppXml is a pure Java class that provides an alternative to creating a web.xml.
// It extends the SpringServletInitializer class. This extension offers many configurable
// options by overriding methods. But one required method is configure().

// configure() provides the means to register the classes that are needed to launch the application.
// This is where you supply a handle to your Application configuration. Remember: Application has
// the @ComponentScan, so it will find the web controller automatically.

public class WebAppXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}