package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try (Connection connection = initConnection(getProperties())) {
            try {
                int interval = Integer.parseInt(getProperties().getProperty("rabbit.interval"));
                int sleep_timer = Integer.parseInt(getProperties().getProperty("sleep_timer"));
                List<Long> store = new ArrayList<>();
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("store", store);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(sleep_timer);
                scheduler.shutdown();
                System.out.println(store);
            } catch (Exception se) {
                se.printStackTrace();
            }
        }
    }

    private static Properties getProperties() {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return config;
    }

    private static Connection initConnection(Properties properties) throws SQLException, ClassNotFoundException {
        Class.forName(properties.getProperty("driver-class-name"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url, login, password);
    }


    public static class Rabbit implements Job {
        private Connection connection;

        public Rabbit(Connection connection) {
            this.connection = connection;
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            try (PreparedStatement st = connection.prepareStatement("insert into rabbit(created_date) values(?)")) {
                st.setString(1, String.valueOf(System.currentTimeMillis()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

