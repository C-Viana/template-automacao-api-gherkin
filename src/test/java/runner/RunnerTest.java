package runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber"},
		features = "src/test/java/features",
		glue = "steps",
		monochrome = true,
		snippets = SnippetType.CAMELCASE,
		stepNotifications = true,
		dryRun = false
//		,tags = "@barrigaTest3"
)
public class RunnerTest {
	
}
