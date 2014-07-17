package com.jd.survey.dao.defaults;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.domain.settings.Department;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.survey.SurveyEntry;
import com.jd.survey.domain.survey.SurveyPage;

/** DAO implementation to handle persistence for object :Group
 */
//@Repository("QueriesDAO")
//@Transactional
public class QueriesDAOImp  implements QueriesDAO{

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public QueriesDAOImp() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}


	
	
	//@Test
	public void test2() {
		/*
		String queryStr =
						"select new com.jd.survey.dao.defaults.AppType(t.id, t.name,t.id) " +
						"from QuestionType t where not exists (select q from Question q where q.type.id=t.id)";
		
		//Long Id (UserId)
		//GetUserDepartments
		//select d from Department d where d.id in (select ud.id from User u join  u.departments ud where u.id=?)
		
		
		//Long Id (userId)
		//GetInternalUserSurveyDefinitions
		//select new com.jd.survey.dao.defaults.AppType(s.id, s.name) from SurveyDefinition s where s.department.id in (select ud.id from User u join  u.departments ud where u.id=?)
		
		
		//SurveyEntry(Long surveyId, Long surveyDefinitionId,
		//		String departmentName, String surveyName, String createdByLogin,
		//		String createdByFirstName, String createdByMiddleName,
		//		String createdByLastName, Date creationDate,
		//		Date lastUpdateDate, Date submissionDate, SurveyStatus status)
		
		
		
	
		//Filter Departments by user	
		queryStr =	"select new com.jd.survey.dao.defaults.AppType(d.id, d.name) from Department d where d.id in (select ud.id from User u join  u.departments ud where u.id=2)";
		TypedQuery<AppType> query =   entityManager.createQuery(queryStr, AppType.class);
		List<AppType> results = query.getResultList();
		for (AppType result: results) {
		  System.out.println("type id:"+ result.id +" name:" +result.name );
		 }
		
		
		
		//Filter Departments by user	
		queryStr =	"select new com.jd.survey.dao.defaults.AppType(s.id, s.name) from SurveyDefinition s where s.department.id in (select ud.id from User u join  u.departments ud where u.id=2)";
		
		TypedQuery<AppType> query2 =   entityManager.createQuery(queryStr, AppType.class);
		List<AppType> results2 = query2.getResultList();
		for (AppType result: results2) {
			System.out.println("type id:"+ result.id +" name:" +result.name );
		 }
		*/
		
		
				
		String queryStr2 =
				"select new com.jd.survey.domain.survey.SurveyEntry(s.id,sd.id,d.name,sd.name,u.login,u.firstName,u.middleName,u.lastName,s.creationDate,s.lastUpdateDate,s.submissionDate,s.status) " +  
				"from Survey s, SurveyDefinition sd, User u, Department d " +
				"where s.login=u.login " +
				"and s.typeId=sd.id " +
				"and sd.department.id = d.id " + 
				"and s.typeId = ?" ; 
				TypedQuery<SurveyEntry> query =   entityManager.createQuery(queryStr2, SurveyEntry.class);
				query.setParameter(1, (long) 2);
				query.setFirstResult(0);
				query.setMaxResults(3);			
				List<SurveyEntry> results2 = query.getResultList();
				for (SurveyEntry result: results2) {
					System.out.println("s id:"+ result.getSurveyId());
				 }	
		
		
	}
			  
		  
	public void test() {

		String queryStr =
		      "select new com.jd.survey.dao.defaults.AppType(t.id, t.name,q.id) " +
		      "from Question q right join q.type t";
		  TypedQuery<AppType> query =   entityManager.createQuery(queryStr, AppType.class);
		  List<AppType> results = query.getResultList();
		  
		  for (AppType apptype: results) {
			  //System.out.println("type id:"+ apptype.surveyDefinitionId +" name"+apptype.surveyDefinitionName +" question-id:" +apptype.surveyId );
		  }
		
	}

	
	
}