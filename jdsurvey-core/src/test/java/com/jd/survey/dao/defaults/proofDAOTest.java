package com.jd.survey.dao.defaults;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.SurveyStatisticDAO;
import com.jd.survey.domain.survey.SurveyStatistic;



/**
 * Class used to test the basic Data Store Functionality
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@ContextConfiguration(locations = {"file:C:/webapps/shared/JD_SURVEY/xml/internal/service-context.xml"})
public class proofDAOTest {
	/**
	 * The DAO being tested, injected by Spring
	 *
	 */
	@Autowired private  SurveyStatisticDAO surveyStatisticDAO;
	

	public proofDAOTest() {
	}

	@Rollback(true)
	@Test
	public void mytest() {
		try {

			 //SurveyStatistic surveyStatistic =surveyStatisticDAO.get((long) 2);
			//List<SurveyStatistic> surveyStatistics =surveyStatisticDAO.getAll("");
			
			List<SurveyStatistic> surveyStatistics =surveyStatisticDAO.getAll("ladmin");
			 for (SurveyStatistic surveyStatistic: surveyStatistics){
			 System.out.println(" getSurveyDefiniti onId:" + surveyStatistic.getSurveyDefinitionId()+ 
					 			" getSurveyName:" + surveyStatistic.getSurveyName()+
					 			" getDepartmentName:" + surveyStatistic.getDepartmentName()+
					 			" getIcompletedCount:" + surveyStatistic.getIcompletedCount()+
					 			" getSubmittedCount:" + surveyStatistic.getSubmittedCount()+
					 			" getDeletedCount:" + surveyStatistic.getDeletedCount()+
					 			" getTotalCount:" + surveyStatistic.getTotalCount());
			 
			 }
		
		System.out.println("done");
		} catch (DataAccessException e) {
			System.out.println("err");
			e.printStackTrace();
		}
		catch (Exception e) {
		System.out.println("err");
		e.printStackTrace();
	}
		
	}

}
