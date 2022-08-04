package steps.barrigarest;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import reporter.ReportManager;

public class _Hooks {

	@BeforeAll
    public static void beforeAll() {
		ReportManager.suite_test = true;
    }

    @AfterAll
    public static void afterAll() {
        if (ReportManager.suite_test)
            ReportManager.endReport();
    }

    @Before
    public void startTest(Scenario scenario) {
        ReportManager.setScenario(scenario);
        ReportManager.setFeature(scenario.getUri().toString());
        ReportManager.setResultPath(scenario.getUri().toString(), scenario.getName());
        ReportManager.startReport();
        String prefix = (ReportManager.suite_test) ? ReportManager.getFeature() + " - " : "";
        ReportManager.startTest(prefix + scenario.getName());
    }

    @After
    public void endTest(Scenario scenario) {
        ReportManager.endTest();
        if (!ReportManager.suite_test)
            ReportManager.endReport();
    }

}
