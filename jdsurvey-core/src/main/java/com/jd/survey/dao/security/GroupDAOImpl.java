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

import com.jd.survey.dao.interfaces.security.GroupDAO;
import com.jd.survey.domain.security.Group;

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

/** DAO implementation to handle persistence for object :Group
 */
@Repository("GroupDAO")
@Transactional
public class GroupDAOImpl extends AbstractJpaDao<Group> implements	GroupDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Group.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public GroupDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<Group> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}
	
	@Transactional
	public Set<Group> findAllInternal() throws DataAccessException {
		return findAllInternal(-1, -1);
	}
	
	@Transactional
	public Set<Group> findAllExternal() throws DataAccessException {
		return findAllExternal(-1, -1);
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Group> findAllInternal(int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("Group.findAllInternal", startResult,maxRows);
		return new LinkedHashSet<Group>(query.getResultList());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Group> findAllExternal(int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("Group.findAllExternal", startResult,maxRows);
		return new LinkedHashSet<Group>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Group> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Group.findAll", startResult,maxRows);
		return new LinkedHashSet<Group>(query.getResultList());
	}

	@Transactional
	public Group findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Group.findById", -1, -1, id);
			return (Group) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("Group.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	
	@Transactional
	public Group findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("Group.findByName", -1, -1, name);
			return (Group) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	
	
	public boolean canBeMerged(Group entity) {
		return true;
	}

}
