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

import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.SurveyDefinition;

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

/** DAO implementation to handle persistence for object :SurveyDefinition
 */
@Repository("SurveyDefinitionDAO")
@Transactional
public class SurveyDefinitionDAOImpl extends AbstractJpaDao<SurveyDefinition> implements	SurveyDefinitionDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { SurveyDefinition.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyDefinitionDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	
	
	@Override
	public Set<SurveyDefinition> findAllInternal() throws DataAccessException {
		return findAllInternal(-1, -1);
	}

	@Override
	public Set<SurveyDefinition> findAllInternal(int startResult, int maxRows)throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllInternal", startResult,maxRows);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}
	
	
	
	@Override
	public Set<SurveyDefinition> findAllInternal(String login)	throws DataAccessException {
		return findAllInternal(login , -1, -1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllInternal(String login, int startResult,	int maxRows) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllForManagerInternal", startResult,maxRows,login);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}
	
	
	
	
	@Override
	public Set<SurveyDefinition> findAllCompletedInternal() throws DataAccessException {
		return findAllCompletedInternal(-1, -1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllCompletedInternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllCompletedInternal", startResult,maxRows);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}


	
	@Override
	public Set<SurveyDefinition> findAllCompletedInternal(String login)	throws DataAccessException {
		return findAllInternal(login , -1, -1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllCompletedInternal(String login,	int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllCompletedForManagerInternal", startResult,maxRows,login);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}
	
	
	
	
	@Override
	public Set<SurveyDefinition> findAllPublishedInternal() throws DataAccessException {
		return findAllPublishedInternal(-1, -1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllPublishedInternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllPublishedInternal", startResult,maxRows);
		Set<SurveyDefinition> surveyDefinitions   = new LinkedHashSet<SurveyDefinition>(query.getResultList());
		return surveyDefinitions;
	}


	
	@Override
	public Set<SurveyDefinition> findAllPublishedInternal(String login)	throws DataAccessException {
		return findAllInternal(login , -1, -1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllPublishedInternal(String login,	int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllPublishedForManagerInternal", startResult,maxRows,login);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}
	
	@Override
	public Set<SurveyDefinition> findAllPublishedPublic()	throws DataAccessException {
		return findAllInternal(-1, -1);
	}
	
	@Override
	public Set<SurveyDefinition> findAllPublishedPublic(int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllPublishedPublic", startResult,maxRows);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}
	
	
	
	
	
	@Transactional
	public SurveyDefinition findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinition.findById", -1, -1, id);
			return (SurveyDefinition) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	
	@Transactional
	public SurveyDefinition findByIdEager(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinition.findByIdEager", -1, -1, id);
			return (SurveyDefinition) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}



	

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<SurveyDefinition> findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinition.findByName", -1, -1, name);
			return new LinkedHashSet<SurveyDefinition>(query.getResultList());
		} catch (NoResultException nre) {
			return null;
		}
	}


	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinition.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public int deleteByDepartmentId(Long id) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.deleteByDepartmentId", 0, 0, id);
		return query.executeUpdate();
	}


	public boolean canBeMerged(SurveyDefinition entity) {
		return true;
	}


	
	@Override
	public Set<SurveyDefinition> findAllPublishedExternal(String login)	throws DataAccessException {
		return findAllPublishedExternal(login , -1, -1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Set<SurveyDefinition> findAllPublishedExternal(String login, int startResult,	int maxRows) throws DataAccessException {
		Query query = createNamedQuery("SurveyDefinition.findAllPublishedExternal", startResult,maxRows,login);
		return new LinkedHashSet<SurveyDefinition>(query.getResultList());
	}

	

	@Transactional
	public Set<SurveyDefinition> getSurveyDefinitionUsers(String login ,int startResult, int maxRows) throws DataAccessException {
		try{
			Query query = createNamedQuery("SurveyDefinition.getSurveyDefinitionUsers", startResult, maxRows, login);
			return new LinkedHashSet<SurveyDefinition>(query.getResultList());
		}catch(NoResultException nre) {
			return null;
		}
	}


	@Transactional
	public Set<SurveyDefinition> getSurveyDefinitionUsers(String login) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyDefinition.getSurveyDefinitionUsers", -1, -1, login);
			return new LinkedHashSet<SurveyDefinition>(query.getResultList());
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public Set<SurveyDefinition> getSurveyDefinitionUsers() {
		try {
			Query query = createNamedQuery("SurveyDefinition.getSurveyDefinitionUsers", -1, -1);
			return new LinkedHashSet<SurveyDefinition>(query.getResultList());
		} catch (NoResultException nre) {
			return null;
		}
	
		
	}
	
}

	
