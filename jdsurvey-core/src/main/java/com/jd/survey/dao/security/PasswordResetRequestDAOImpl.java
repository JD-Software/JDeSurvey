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
package com.jd.survey.dao.security;

import com.jd.survey.dao.interfaces.security.PasswordResetRequestDAO;
import com.jd.survey.domain.security.PasswordResetRequest;

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

/** DAO implementation to handle persistence for object :PasswordResetRequest
 */
@Repository("PasswordResetRequestDAO")
@Transactional
public class PasswordResetRequestDAOImpl extends AbstractJpaDao<PasswordResetRequest> implements	PasswordResetRequestDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { PasswordResetRequest.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public PasswordResetRequestDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<PasswordResetRequest> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<PasswordResetRequest> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("PasswordResetRequest.findAll", startResult,maxRows);
		return new LinkedHashSet<PasswordResetRequest>(query.getResultList());
	}

	@Transactional
	public PasswordResetRequest findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("PasswordResetRequest.findById", -1, -1, id);
			return (PasswordResetRequest) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("PasswordResetRequest.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}


	@Transactional
	public PasswordResetRequest findByHash(String hash) throws DataAccessException {
		try {
			Query query = createNamedQuery("PasswordResetRequest.findByHash", -1, -1, hash);
			return (PasswordResetRequest) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}





	@Transactional
	public PasswordResetRequest findByEmail(String email) throws DataAccessException {

		try {
			Query query = createNamedQuery("PasswordResetRequest.findByEmail", -1, -1, email);
			return (PasswordResetRequest) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public boolean canBeMerged(PasswordResetRequest entity) {
		return true;


	}


}