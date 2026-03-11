package com.udea.lab12025p.cucumber;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class CucumberTest {
    // Esta clase solo sirve como punto de entrada para JUnit 5 Suite.
    // La configuración detallada está en junit-platform.properties
}
