package Util;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Message;

public class AmazonOTPHandle
{
	public static final String ACCOUNT_SID="AC51ebbe7f41913fbe2b9286b4a619b83b";
	public static final String AUTH_TOKEN="e92b7d23134be6cc08b5c9ec50c8253b";
	

	public static void main(String[] args) 
	{
		System.setProperty("wedriver.driver.chrome", "G:\\chromedriver.exe");
		WebDriver driver= new ChromeDriver();
		driver.get("https://www.amazon.in");
		
		//waits
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		//WebElements
		driver.findElement(By.cssSelector("a#nav-link-accountList>span>span")).click();
		driver.findElement(By.xpath("//a[text()=\"Start here.\" and @rel=\"nofollow\"]")).click();
		
		driver.findElement(By.id("ap_customer_name")).sendKeys("Kiran");
	
		driver.findElement(By.id("auth-country-picker-container")).click();
		driver.findElement(By.xpath("//ul[@role='application']/li/a[text()=\"United States +1\"]")).click();;
	/*	Select select= new Select(driver.findElement(By.name("countryCode")));
		select.selectByVisibleText("US +1");*/
		driver.findElement(By.id("ap_phone_number")).sendKeys("2068097590");
		driver.findElement(By.id("ap_password")).sendKeys("Bhargavi");
		driver.findElement(By.id("continue")).click();
		
		//get OTP using twilio
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);//important initialization
		String smsBody=getMessage();
		System.out.println(smsBody);
		String OTPNumber= smsBody.replaceAll("[^-?0-9]+", " ");
		System.out.println(OTPNumber);
		
		driver.findElement(By.id("auth-pv-enter-code")).sendKeys(OTPNumber);
		//driver.findElement(By.id("auth-verify-button")).click();
		//driver.quit();
	}
	public static String getMessage() {
		return getMessages().filter(m -> m.getDirection().compareTo(Message.Direction.INBOUND) == 0)
				.filter(m -> m.getTo().equals("+12068097590")).map(Message::getBody).findFirst()
				.orElseThrow(IllegalStateException::new);
	}

	private static Stream<Message> getMessages() {
		ResourceSet<Message> messages = Message.reader(ACCOUNT_SID).read();
		return StreamSupport.stream(messages.spliterator(), false);
	}

}
