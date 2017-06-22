package coveros.getting_started;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.coveros.selenified.output.Assert;
import com.coveros.selenified.selenium.Action;
import com.coveros.selenified.selenium.Selenium.Locator;
import com.coveros.selenified.tools.TestBase;

public class SampleTests extends TestBase {

	@BeforeClass(alwaysRun = true)
	public void beforeClass() throws IOException {
		// set the base URL for the tests here
		setTestSite("http://www.google.com");
		// set the author of the tests here
		setAuthor("Max Saperstone\n<br/>max.saperstone@coveros.com");
		// set the version of the tests or of the software, possibly with a
		// dynamic check
		setVersion("0.0.1");
	}

	@DataProvider(name = "google search terms", parallel = true)
	public Object[][] DataSetOptions() {
		return new Object[][] { new Object[] { "python" }, new Object[] { "perl" }, new Object[] { "bash" }, };
	}

	@Test(groups = { "sample" }, description = "A sample test to check a title")
	public void sampleTest() throws IOException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// perform some actions
		asserts.compareTitle("Google");
		// verify no issues
		finish();
	}

	@Test(dataProvider = "google search terms", groups = { "sample" },
			description = "A sample test using a data provider to perform searches")
	public void sampleTestWDataProvider(String searchTerm) throws Exception {
		// use this object to manipulate the page
		Action actions = this.actions.get();
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// perform some actions
		actions.type(Locator.NAME, "q", searchTerm);
		actions.click(Locator.NAME, "btnG");
		actions.waitForElementDisplayed(Locator.ID, "resultStats");
		asserts.compareTitle(searchTerm + " - Google Search");
		// verify no issues
		finish();
	}
}