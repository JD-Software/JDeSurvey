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
package com.jd.survey.dao.settings;

import com.jd.survey.dao.interfaces.settings.QuestionDAO;
import com.jd.survey.domain.settings.Question;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.skyway.spring.util.dao.AbstractJpaDao;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

/** DAO implementation to handle persistence for object :Question
 */
@Repository("QuestionDAO")
@Transactional
public class QuestionDAOImpl extends AbstractJpaDao<Question> implements	QuestionDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Question.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public QuestionDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<Question> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Question> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Question.findAll", startResult,maxRows);
		return new LinkedHashSet<Question>(query.getResultList());
	}

	@Transactional
	public Question findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Question.findById", -1, -1, id);
			return (Question) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Override
	public Question findByOrder(Long surveyDefinitionId, Short pageOrder,Short questionOrder) throws DataAccessException {
		
		try {
			Query query = createNamedQuery("Question.findByOrder", -1, -1, surveyDefinitionId,pageOrder,questionOrder);
			return (Question) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("Question.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	
	@Transactional
	public int deleteBySurveyDefinitionPageId(Long id) throws DataAccessException {
		Query query = createNamedQuery("Question.deleteBySurveyDefinitionPageId", 0, 0, id);
		return query.executeUpdate();
	}

	
	
	
	public boolean canBeMerged(Question entity) {
		return true;
	}

	

}
