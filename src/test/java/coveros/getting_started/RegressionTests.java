package coveros.getting_started;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.BufferedReader;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.coveros.selenified.output.Assert;
import com.coveros.selenified.selenium.Action;
import com.coveros.selenified.selenium.Selenium.Locator;
import com.coveros.selenified.tools.TestBase;

public class RegressionTests extends TestBase {
	private static String ipaAdminUsername = "";
	private static String ipaAdminPassword = "";
	private static String newUserName = "";
	private static String newUserFirstName = "";
	private static String newUserLastName = "";
	private static String newUserInitialPassword = "";
	private static String newUserUpdatedPassword = "";
	private static String googleEmail = "";
	private static String googlePassword = "";
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass() throws IOException {
		readProperties();
		// set the base URL for the tests here
		setTestSite("https://lb.ipa.secureci.com");
		// set the author of the tests here
		setAuthor("Rahul Sharma\n<br/>rahul.sharma@coveros.com");
		// set the version of the tests or of the software, possibly with a
		// dynamic check
		setVersion("0.0.1");
	}
    

	@Test(groups = { "regression" }, description = "A test for selenium.user to add data.convert to ipa")
	public void addUserToIPA() throws IOException, InterruptedException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		Action actions = this.actions.get();
		asserts.compareTitle("Identity Management");
		loginToFreeIPA(ipaAdminUsername, ipaAdminPassword);
		addUserToFreeIPA(newUserName, newUserFirstName, newUserLastName, newUserInitialPassword);
		actions.waitForElementNotPresent(Locator.XPATH, ".//*[@id='notification']/div[2]/div");
		logoutOfFreeIPA();
		finish();
	}
	
	@Test(dependsOnMethods = { "addUserToIPA" }, groups = { "regression" }, description = "A test for data.convert to update password")
	public void updatePassword() throws IOException, InterruptedException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// use this object to manipulate the page
		Action actions = this.actions.get();
		// perform some actions
		asserts.compareTitle("Identity Management");
		loginToFreeIPA(newUserName, newUserInitialPassword);
		updatePassword(newUserInitialPassword, newUserUpdatedPassword);
		actions.waitForElementNotPresent(Locator.XPATH, ".//*[@id='notification']/div[2]/div");
		logoutOfFreeIPA();
		finish();
	}

	@Test(groups = { "regression" }, description = "A test to enable SSO in Google")
	public void enableSSOinGoogle() throws IOException, InterruptedException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// use this object to manipulate the page
		Action actions = this.actions.get();
		actions.goToURL("https://admin.google.com/AdminHome?");
		asserts.compareTitle("Sign in - Google Accounts");
		signInToGoogle(googleEmail, googlePassword);
		enableGoogleSSO();
		logoutOfGoogle();		
		finish();
	}
	
	@Test(dependsOnMethods = {"enableSSOinGoogle", "updatePassword"}, groups = { "regression" }, description = "A test to verify data.convert can login Google via FreeIPA")
	public void loginToGoogleViaFreeIPA() throws IOException, InterruptedException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// use this object to manipulate the page
		Action actions = this.actions.get();
		actions.goToURL("https://accounts.google.com");
		asserts.compareTitle("Sign in - Google Accounts");
		actions.type(Locator.ID, "Email", newUserName+"@darkseer.org");
		actions.click(Locator.ID, "next");
		loginToIpsilon(newUserName, newUserUpdatedPassword);
		logoutOfGoogle();
		finish();
	}
	
	@Test(dependsOnMethods = { "loginToGoogleViaFreeIPA" }, groups = { "regression" }, description = "A test to verify data.convert can login Google via FreeIPA")
	public void deleteUserFromFreeIPA() throws IOException, InterruptedException {
		// use this object to verify the page looks as expected
		Assert asserts = this.asserts.get();
		// use this object to manipulate the page
		Action actions = this.actions.get();
		asserts.compareTitle("Identity Management");
		loginToFreeIPA(ipaAdminUsername, ipaAdminPassword);
		deleteUser(newUserName);
        logoutOfFreeIPA();
		finish();
	}
	
	private static void readProperties(){
		try{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream reader = classLoader.getResourceAsStream("config.properties");
		    Properties properties = new Properties();
		    properties.load(reader);
		    ipaAdminUsername = properties.getProperty("ipaAdminUsername");
		    ipaAdminPassword = properties.getProperty("ipaAdminPassword");
		    newUserName = properties.getProperty("newUserName");
		    newUserFirstName = properties.getProperty("newUserFirstName");
		    newUserLastName = properties.getProperty("newUserLastName");
		    newUserInitialPassword = properties.getProperty("newUserInitialPassword"); 
		    newUserUpdatedPassword = properties.getProperty("newUserUpdatedPassword"); 
		    googleEmail = properties.getProperty("googleEmail"); 
		    googlePassword = properties.getProperty("googlePassword");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void loginToFreeIPA(String username, String password) throws IOException {
		Action actions = this.actions.get();
		actions.type(Locator.ID, "username1", username);
		actions.type(Locator.ID, "password2", password);
		actions.click(Locator.XPATH, ".//*[@id='simple-container']/div/div/div[2]/div/div/div/div[2]/div[2]/div/button[2]");
	}
	
	private void addUserToFreeIPA(String userName, String firstName, String lastName, String password) throws IOException {
		Action actions = this.actions.get();
		actions.waitForElementPresent(Locator.NAME, "add");
		actions.click(Locator.NAME, "add");
		actions.type(Locator.XPATH, ".//*[@id='add']/div/div[2]/div[2]/div/div/div[1]/div[2]/div/div/input", userName);	
		actions.type(Locator.XPATH, ".//*[@id='add']/div/div[2]/div[2]/div/div/div[2]/div[2]/div/div/input", firstName);	
		actions.type(Locator.XPATH, ".//*[@id='add']/div/div[2]/div[2]/div/div/div[3]/div[2]/div/div/input", lastName);	
		actions.type(Locator.XPATH, ".//*[@id='add']/div/div[2]/div[3]/div/div/div[1]/div[2]/div/div/input", password);
		actions.type(Locator.XPATH, ".//*[@id='add']/div/div[2]/div[3]/div/div/div[2]/div[2]/div/div/input", password);
		actions.click(Locator.XPATH, ".//*[@id='add']/div/div[3]/div[1]/button[1]");		
	}
	
	private void logoutOfFreeIPA() throws IOException {
		Action actions = this.actions.get();
		Assert asserts = this.asserts.get();
		actions.click(Locator.CSS, ".loggedinas");
		actions.click(Locator.XPATH, ".//*[@id='container']/nav/div[2]/ul[1]/li[2]/ul/li[6]/a");
	}
	
	private void updatePassword(String initialPassword, String updatedPassword) throws IOException{
		Action actions = this.actions.get();
		actions.type(Locator.ID, "current_password4", initialPassword);
		actions.type(Locator.ID, "new_password6", updatedPassword);
		actions.type(Locator.ID, "verify_password7", updatedPassword);
		actions.click(Locator.XPATH, ".//*[@id='simple-container']/div/div/div[2]/div/div/div/div[2]/div[2]/div/button[5]");
	}
	
	private void signInToGoogle(String email, String password) throws IOException {
		Action actions = this.actions.get();
		Assert asserts = this.asserts.get();
		actions.type(Locator.ID, "Email", email);
		actions.click(Locator.ID, "next");
		actions.type(Locator.ID, "Passwd", password);
		actions.click(Locator.ID, "signIn");
		asserts.compareTitle("Admin console");
	}
	
	private void enableGoogleSSO() throws IOException {
		Action actions = this.actions.get();
		Assert asserts = this.asserts.get();
		actions.click(Locator.ID, "dashboard-icon-14");
		actions.click(Locator.XPATH, ".//*[@id='center-panel']/div/div[3]/div/div/div/div[3]/div/div[1]/div[4]/div[1]");
        
		//check if SSO is enabled
		asserts.checkTextVisible("Setup SSO with third party identity provider");
		String isChecked = actions.getDriver().findElement(By.xpath("//*[@id='center-panel']/div/div[3]/div/div/div/div[3]/div/div[1]/div[4]/div[2]/div[2]/div[2]/div/div/div/div[4]/div/div")).getAttribute("aria-checked");
		if(!isChecked.equals("true")){
			actions.click(Locator.XPATH, "//*[@id='center-panel']/div/div[3]/div/div/div/div[3]/div/div[1]/div[4]/div[2]/div[2]/div[2]/div/div/div/div[4]/div/div");
            actions.click(Locator.CSS, ".quantumButton.quantumButton-flat.button-default-action.button-first");		
		}
	}
	
	private void logoutOfGoogle() throws IOException {
		Action actions = this.actions.get();
		Assert asserts = this.asserts.get();
		actions.click(Locator.CSS, ".gb_8a.gbii");
		actions.click(Locator.ID, "gb_71");
		asserts.checkTextVisible("Sign in with your Google Account");
	}
	
	private void loginToIpsilon(String username, String password) throws IOException {
		Action actions = this.actions.get();
		actions.type(Locator.ID, "login_name", username);
		actions.type(Locator.ID, "login_password", password);
		actions.click(Locator.CSS, ".btn.btn-primary.btn-lg");
	}
	
	private void deleteUser(String usernameToDelete) throws IOException {
		Action actions = this.actions.get();
		Assert asserts = this.asserts.get();
		actions.click(Locator.LINKTEXT, usernameToDelete);
        actions.waitForElementNotPresent(Locator.XPATH, ".//*[@id='container']/div[2]/div/div");
        actions.click(Locator.CSS, ".dropdown-toggle.btn.btn-default");
        actions.click(Locator.LINKTEXT, "Delete");
        actions.click(Locator.NAME, "ok");
        actions.waitForElementNotPresent(Locator.XPATH, ".//*[@id='notification']/div[2]/div");
        asserts.checkTextNotVisible(usernameToDelete);
	}
}