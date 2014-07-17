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

import com.jd.survey.dao.interfaces.settings.VelocityTemplateDAO;
import com.jd.survey.domain.settings.VelocityTemplate;

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

/** DAO implementation to handle persistence for object :VelocityTemplate
 */
@Repository("VelocityTemplateDAO")
@Transactional
public class VelocityTemplateDAOImpl extends AbstractJpaDao<VelocityTemplate> implements	VelocityTemplateDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { VelocityTemplate.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public VelocityTemplateDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<VelocityTemplate> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VelocityTemplate> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("VelocityTemplate.findAll", startResult,maxRows);
		return new LinkedHashSet<VelocityTemplate>(query.getResultList());
	}

	@Transactional
	public VelocityTemplate findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("VelocityTemplate.findById", -1, -1, id);
			return (VelocityTemplate) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	@Transactional
	public VelocityTemplate findByName(String velocityTemplate) throws DataAccessException {
		try {
			Query query = createNamedQuery("VelocityTemplate.findByName", -1, -1, velocityTemplate);
			return (VelocityTemplate) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("VelocityTemplate.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	public boolean canBeMerged(VelocityTemplate entity) {
		return true;
	}

}
