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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.SurveyDocumentDAO;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyDocument;


/** 
 * DAO implementation to handle persistence for object :Survey
 * Uses Both JPA and JDBCTemplate
 */
@Repository("SurveyDocumentDAO")
@Transactional
public class SurveyDocumentDAOImpl extends AbstractJpaDao<SurveyDocument> implements	SurveyDocumentDAO {
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Survey.class }));




	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyDocumentDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}


	@Transactional
	public SurveyDocument findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDocument.findById", -1, -1, id);
			return (SurveyDocument) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public SurveyDocument findBySurveyIdAndQuestionId(Long surveyId,Long questionId) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDocument.findBySurveyIdAndQuestionId", -1, -1, surveyId,questionId);
			return (SurveyDocument) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	@Transactional
	public int deleteBySurveyIdAndQuestionId(Long surveyId,Long questionId) throws DataAccessException {
		Query query = createNamedQuery("SurveyDocument.deleteBySurveyIdAndQuestionId", 0, 0, surveyId,questionId);
		return query.executeUpdate();
	}
	
	public boolean canBeMerged(SurveyDocument entity) {
		return true;
	}







}
