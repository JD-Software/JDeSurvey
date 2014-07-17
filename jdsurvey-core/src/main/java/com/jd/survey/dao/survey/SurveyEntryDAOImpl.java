  /*Copyright (C) 2014  JD Software, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.jd.survey.dao.survey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.SurveyEntryDAO;
import com.jd.survey.domain.survey.SurveyEntry;

/** DAO implementation to handle persistence for object :Group
 */
@Repository("SurveyEntryDAO")
@Transactional
public class SurveyEntryDAOImpl extends AbstractJpaDao<SurveyEntry> implements SurveyEntryDAO{
	public static final int DEFAULT_FIRST_RESULT_INDEX = 0;
	public static final int MAX_RESULTS= 25;
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { SurveyEntry.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyEntryDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}
	
	@Override
	public boolean canBeMerged(SurveyEntry o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SurveyEntry get(Long surveyId) {
		String queryStr =
				"select new com.jd.survey.domain.survey.SurveyEntry(s.id,sd.id,s.ipAddress,d.name,sd.name,s.login, s.firstName,s.middleName,s.lastName,s.email,s.creationDate,s.lastUpdateDate,s.submissionDate,s.status) " +  
				"from Survey s, SurveyDefinition sd, Department d " +
				"where s.typeId=sd.id " +
				"and sd.department.id = d.id " + 
				"and s.id = ?" ; 
		TypedQuery<SurveyEntry> query =   entityManager.createQuery(queryStr, SurveyEntry.class);
		query.setParameter(1, surveyId);
		SurveyEntry surveyEntry = query.getSingleResult();
		return surveyEntry;
	}

	@Override
	public SortedSet<SurveyEntry> getAll(Long surveyDefinitionId) {
		return getAll(surveyDefinitionId,-1, -1);
	}
	
	@Override
	public SortedSet<SurveyEntry> getAll(Long surveyDefinitionId, Integer firstResult, Integer maxResults) {
		
		
	
		
		
		
		String queryStr =
				"select new com.jd.survey.domain.survey.SurveyEntry(s.id,sd.id,s.ipAddress,d.name,sd.name,s.login, s.firstName,s.middleName,s.lastName,s.email,s.creationDate,s.lastUpdateDate,s.submissionDate,s.status) " +  
				"from Survey s, SurveyDefinition sd, Department d " +
				"where s.typeId=sd.id " +
				"and sd.department.id = d.id " + 
				"and s.typeId = ? order by s.creationDate desc" ; 
		TypedQuery<SurveyEntry> query =   entityManager.createQuery(queryStr, SurveyEntry.class);
		query.setParameter(1, surveyDefinitionId);
		query.setFirstResult(firstResult == null || firstResult < 0 ? DEFAULT_FIRST_RESULT_INDEX : firstResult);
		query.setMaxResults(maxResults == null || maxResults < 0 ? MAX_RESULTS : maxResults);
		
		
		return new TreeSet<SurveyEntry>(query.getResultList());
	}

	
	@Override
	public Long getCount(Long surveyDefinitionId) {
		String queryStr ="select count(s) from Survey s where s.typeId = ?" ; 
		Query  query=   entityManager.createQuery(queryStr);
		query.setParameter(1, surveyDefinitionId);
		return  (Long) query.getSingleResult();
	}


}
	
