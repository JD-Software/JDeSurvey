package com.jd.survey.dao.defaults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionPageDAO;
import com.jd.survey.dao.interfaces.survey.ReportDAO;
import com.jd.survey.dao.interfaces.survey.SurveyDAO;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyPage;

/**
 * Class used to test the basic Data Store Functionality
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@Transactional
@ContextConfiguration(locations = {"file:C:/webapps/shared/JD_SURVEY/xml/internal/service-context.xml"})
public class JDBCTemplateTests {
	/**
	 * The DAO being tested, injected by Spring
	 *
	 */
	@Autowired	private ReportDAO reportDAO;
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private SurveyDefinitionPageDAO surveyDefinitionPageDAO;
	@Autowired	private SurveyDAO surveyDAO;
	
	@Transactional
	@Test
	public void QuestionType() {
		try{
		//reportDAO.publishSurveyDefinition(surveyDefinitionDAO.findById((long) 2));
		//Survey survey =surveyDAO.findById((long) 27);

		/*	
		int i =1;
		while(i<1000){		
			reportDAO.publishSurveySubmission(survey);
			i++;
		}
		*/
			
			
		surveyDAO.publish(surveyDefinitionDAO.findById((long) 2));
				
		Survey survey = new Survey();
		survey.setId((long) 5);
		survey.setTypeId((long) 2);
		
		//surveyDAO.create(survey);
		
		
		SurveyDefinitionPage surveyDefinitionPage = surveyDefinitionPageDAO.findById((long) 1);
		int i =1;
		
		SurveyPage surveyPage = new SurveyPage(survey ,surveyDefinitionPage); 
		for (QuestionAnswer questionAnswer :  surveyPage.getQuestionAnswers()) {
			switch (questionAnswer.getQuestion().getType())
			{
			case YES_NO_DROPDOWN: //Yes No DropDown
				questionAnswer.setBooleanAnswerValue(true);
				break;
			default: 
				questionAnswer.setStringAnswerValue("..."+ i );
				break;	
			}
			 i++;
		}
		
		
		surveyDAO.updatePage(surveyPage);
		
		
		
		
		
		
		
		
		
		
		
		
		surveyPage = surveyDAO.getPage(survey, surveyDefinitionPage,"MM-DD-YYYY");
		for (QuestionAnswer questionAnswer :  surveyPage.getQuestionAnswers()) {
			switch (questionAnswer.getQuestion().getType())
			{
			
			case YES_NO_DROPDOWN: //Yes No DropDown
				System.out.println("q:" + questionAnswer.getQuestion().getQuestionText());
				System.out.println("a:" + questionAnswer.getBooleanAnswerValue());
				break;
			default: 
				System.out.println("q:" + questionAnswer.getQuestion().getQuestionText());
				System.out.println("a:" + questionAnswer.getStringAnswerValue());
				break;	
			}
			
			
		}
		}
	catch (Exception e)
	{
		System.out.println(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
	}
		
	}

}
