package ge.reklapp.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tornike on 15.07.2016.
 */
public class Listener implements javax.servlet.ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent context) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(new Runnable() {
            public void run() {
                // TODO bolomde nayurebi reklamis amogeba
            }
        }, 1, TimeUnit.DAYS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event){}
}