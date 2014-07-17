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

import org.skyway.spring.util.dao.AbstractJpaDao;

import com.jd.survey.dao.interfaces.settings.SurveyTemplateDAO;
import com.jd.survey.domain.settings.SurveyTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("SurveyTemplateDAO")
@Transactional
public class SurveyTemplateDAOImpl extends AbstractJpaDao<SurveyTemplate> implements SurveyTemplateDAO{

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { SurveyTemplate.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyTemplateDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<SurveyTemplate> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<SurveyTemplate> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("SurveyTemplate.findAll", startResult,maxRows);
		return new LinkedHashSet<SurveyTemplate>(query.getResultList());
	}

	@Transactional
	public SurveyTemplate findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyTemplate.findById", -1, -1, id);
			return (SurveyTemplate) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public Set<SurveyTemplate> findBySectorId(Long id)	throws DataAccessException {
		return findBySectorId(id,-1,-1);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<SurveyTemplate> findBySectorId(Long id,int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("SurveyTemplate.findBySectorId", startResult,maxRows, id);
		return new LinkedHashSet<SurveyTemplate>(query.getResultList());
	}
	
	@Transactional
	public Long getCount(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyTemplate.getCount",-1,-1, id);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public boolean canBeMerged(SurveyTemplate entity) {
		return true;
	}

	@Transactional
	public int deleteBySectorId(Long id) throws DataAccessException {
		Query query = createNamedQuery("SurveyTemplate.deleteBySectorId", 0, 0, id);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<SurveyTemplate> findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("SurveyTemplate.findByName", -1, -1, name);
			return new LinkedHashSet<SurveyTemplate>(query.getResultList());
		} catch (NoResultException nre) {
			return null;
		}
	}


}
