package com.udea.lab12025p.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // Apunta a la carpeta donde ubicamos los .feature
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.udea.lab12025p.cucumber") // Donde busca las
                                                                                                   // traducciones Java
public class CucumberTestRunner {
    // Clase vacía, sirve como Switch maestro de JUnit Platform
}
