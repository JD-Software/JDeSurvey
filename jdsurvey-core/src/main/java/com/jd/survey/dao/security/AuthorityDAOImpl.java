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

import com.jd.survey.dao.interfaces.security.AuthorityDAO;
import com.jd.survey.domain.security.Authority;

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

/** DAO implementation to handle persistence for object :Authority
 */
@Repository("AuthorityDAO")
@Transactional
public class AuthorityDAOImpl extends AbstractJpaDao<Authority> implements	AuthorityDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Authority.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public AuthorityDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<Authority> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@Override
	public Set<Authority> findAllInternal() throws DataAccessException {
		return findAllInternal(-1, -1);
	}

	@Override
	public Set<Authority> findAllExternal() throws DataAccessException {
		return findAllExternal(-1, -1);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Authority> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Authority.findAll", startResult,maxRows);
		return new LinkedHashSet<Authority>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Authority> findAllInternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Authority.findAllInternal", startResult,maxRows);
		return new LinkedHashSet<Authority>(query.getResultList());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Authority> findAllExternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Authority.findAllExternal", startResult,maxRows);
		return new LinkedHashSet<Authority>(query.getResultList());
	}

	@Transactional
	public Authority findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Authority.findById", -1, -1, id);
			return (Authority) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public  Set<Authority> findbyUserId(Long id) throws DataAccessException {
		Query query = createNamedQuery("Authority.getbyUserId", -1, -1, id);
		return new LinkedHashSet<Authority>(query.getResultList());
	}
	
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("Authority.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	
	@Transactional
	public Authority findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("Authority.findByName", -1, -1, name);
			return (Authority) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	public boolean canBeMerged(Authority entity) {
		return true;
	}

}
