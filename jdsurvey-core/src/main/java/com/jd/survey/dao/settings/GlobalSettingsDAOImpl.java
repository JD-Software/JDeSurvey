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

import com.jd.survey.GlobalSettings;
import com.jd.survey.dao.interfaces.settings.GlobalSettingsDAO;


@Repository("GlobalSettingsDAO")
@Transactional
public class GlobalSettingsDAOImpl extends AbstractJpaDao<GlobalSettings> implements GlobalSettingsDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { GlobalSettings.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public GlobalSettingsDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<GlobalSettings> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<GlobalSettings> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("GlobalSettings.findAll", startResult,maxRows);
		return new LinkedHashSet<GlobalSettings>(query.getResultList());
	}

	
	@Transactional
	public GlobalSettings findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("GlobalSettings.findById", -1, -1, id);
			return (GlobalSettings) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("GlobalSettings.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	
	@Transactional
	public GlobalSettings findBypasswordEnforcementRegex(String passwordEnforcementRegex) throws DataAccessException {
		try {
			Query query = createNamedQuery("GlobalSettings.findByPasswordEnforcementRegex", -1, -1, passwordEnforcementRegex);
			return (GlobalSettings) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	
	public boolean canBeMerged(GlobalSettings entity) {
		return true;
	}

	
	

}
