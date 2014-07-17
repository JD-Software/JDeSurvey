package com.jd.survey.dao.defaults;



import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {
	
	public static void main(String[] args) {
		
		try {
			
//			// specify the job' s details..
//			JobDetail job = JobBuilder.newJob(TestJob.class)
//			    .withIdentity("testJob")
//			    .build();
//			
//			// specify the running period of the job
//			Trigger trigger = TriggerBuilder.newTrigger()
//			      .withSchedule(  
//	                    SimpleScheduleBuilder.simpleSchedule()
//	                    .withIntervalInSeconds(3)
//	                    .repeatForever())  
//                             .build();  
//	    	
//			//schedule the job
//			SchedulerFactory schFactory = new StdSchedulerFactory();
//			Scheduler sch = schFactory.getScheduler();
//	    	sch.start();	    	
//	    	sch.scheduleJob(job, trigger);		
		
//			org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
			
//			org.springframework.scheduling.quartz.JobDetailFactoryBean
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
