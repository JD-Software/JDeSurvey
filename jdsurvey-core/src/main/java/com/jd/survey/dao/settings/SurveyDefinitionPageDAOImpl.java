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

import com.jd.survey.dao.interfaces.settings.SurveyDefinitionPageDAO;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;

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

/** DAO implementation to handle persistence for object :SurveyDefinitionPage
 */
@Repository("SurveyDefinitionPageDAO")
@Transactional
public class SurveyDefinitionPageDAOImpl extends AbstractJpaDao<SurveyDefinitionPage> implements	SurveyDefinitionPageDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { SurveyDefinitionPage.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyDefinitionPageDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<SurveyDefinitionPage> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<SurveyDefinitionPage> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinitionPage.findAll", startResult,maxRows);
		return new LinkedHashSet<SurveyDefinitionPage>(query.getResultList());
	}

	@Transactional
	public SurveyDefinitionPage findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinitionPage.findById", -1, -1, id);
			return (SurveyDefinitionPage) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public SurveyDefinitionPage findByOrder(Long surveyDefintionId,Short pageOrder) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinitionPage.findByOrder", -1, -1, surveyDefintionId,pageOrder);
			return (SurveyDefinitionPage) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	
	
	

	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinitionPage.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}


	@Transactional
	public int deleteBySurveyDefinitionId(Long id) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinitionPage.deleteBySurveyDefinitionId", 0, 0, id);
		return query.executeUpdate();
	}




	@Transactional
	public Set<SurveyDefinitionPage> findByTitle(String title) throws DataAccessException {
		try{
			Query query = createNamedQuery("SurveyDefinitionPage.findByTitle", -1,-1, title);
			return new LinkedHashSet<SurveyDefinitionPage>(query.getResultList());
		}catch (NoResultException nre){

			return null; 

		}



	}



	public boolean canBeMerged(SurveyDefinitionPage entity) {
		return true;
	}



}
