package com.jd.survey.dao.defaults;

import org.hibernate.annotations.DynamicUpdate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.security.UserDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.service.settings.SurveySettingsService;
@DynamicUpdate(value=true)
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@Transactional
@TransactionConfiguration(defaultRollback=false)
@ContextConfiguration(locations = {"file:C:/webapps/shared/JD_SURVEY/xml/internal/service-context.xml"})
public class AdvancedJPAQueries {
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private UserDAO userDAO;
	
	@Test
	@Transactional
	public void mytest() {

		try{
		
//			//surveySettingsService.sendEmailReminders();
//			
//			SurveyDefinition surveyDefinition =  surveyDefinitionDAO.findById(1l);
//			System.out.println("---------------updating date");
//			surveyDefinition.setName("ddddddddddddd");
//			surveyDefinition.setLastReminderNoticeDate(new Date());
//			surveyDefinition = surveyDefinitionDAO.merge(surveyDefinition);
//			System.out.println("---------------end updating date " + surveyDefinition.getName()  +"-" + surveyDefinition.getLastReminderNoticeDate().toString());
//			
//			User user  = userDAO.findById(1l);
//			user.setFirstName("XXXXXXXXXX");
//			userDAO.merge(user);
//			//userDAO.;
//		
			SurveyDefinition surveyDefinition =  surveyDefinitionDAO.findById(44l);
			surveySettingsService.sendEmailReminder(surveyDefinition);
			
		}
		catch (Exception e)	{
			System.out.println(e.getStackTrace());
		}
		
		
	}
}
