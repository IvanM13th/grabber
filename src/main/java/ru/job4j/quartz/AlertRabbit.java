package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Properties config = getProperties();
        try (Connection connection = initConnection(config)) {
            try {
                int interval = Integer.parseInt(config.getProperty("rabbit.interval"));
                int sleepTimer = Integer.parseInt(config.getProperty("sleep_timer"));
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("connection", connection);
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
                Thread.sleep(sleepTimer);
                scheduler.shutdown();
            } catch (Exception se) {
                se.printStackTrace();
            }
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
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

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail()
                    .getJobDataMap().get("connection");
            try (PreparedStatement st = connection.prepareStatement("insert into rabbit(created_date) values(?)")) {
                st.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                st.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


