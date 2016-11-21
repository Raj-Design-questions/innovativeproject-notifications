package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.*;
import services.ReceiveFromRabbit;
import services.SendToRabbit;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.GregorianCalendar;
import java.util.Calendar;
import views.html.*;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    Notification notification = new Notification();

    public Result receive() throws IOException, TimeoutException{
	GregorianCalendar dt = new GregorianCalendar();
	String time = ((Integer.toString(dt.get(Calendar.DAY_OF_MONTH))+ ":" +Integer.toString(dt.get(Calendar.MONTH))+ ":" +Integer.toString(dt.get(Calendar.YEAR))+ " " +Integer.toString(dt.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(dt.get(Calendar.MINUTE)) + ":" + Integer.toString(dt.get(Calendar.SECOND))));

                SendToRabbit.setQueueName("hello");
                SendToRabbit.send(notification);
                return ok();
                
        
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() throws IOException, java.lang.InterruptedException, TimeoutException {
        RecieveFromRabbit.setQueueName("hello");
        return ok("Your message: " + RecieveFromRabbit.getMessage());
    }
}
