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

import com.jd.survey.dao.interfaces.security.UserDAO;
import com.jd.survey.domain.security.User;
import com.jd.survey.service.security.JDUserDetailsService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyway.spring.util.dao.AbstractJpaDao;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

/** DAO implementation to handle persistence for object :User
 */
@Repository("UserDAO")
@Transactional
public class UserDAOImpl extends AbstractJpaDao<User> implements	UserDAO {
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { User.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public UserDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<User> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("User.findAll", startResult,maxRows);
		return new LinkedHashSet<User>(query.getResultList());
	}

	
	
	

	
	@Transactional
	public Set<User> findAllInternal() throws DataAccessException {
		return findAllInternal(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> findAllInternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("User.findAllInternal", startResult,maxRows);
		return new LinkedHashSet<User>(query.getResultList());
	}

	
	@Transactional
	public Set<User> findAllExternal() throws DataAccessException {
		return findAllExternal(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> findAllExternal(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("User.findAllExternal", startResult,maxRows);
		return new LinkedHashSet<User>(query.getResultList());
	}

	
	
	
	@Transactional
	public User findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("User.findById", -1, -1, id);
			return (User) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}

	}

	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("User.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}
	}


	
	@Transactional
	public Long getCountInternal() throws DataAccessException {
		try {
			Query query = createNamedQuery("User.getCountInternal",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}
	}

	@Transactional
	public Long getCountExternal() throws DataAccessException {
		try {
			Query query = createNamedQuery("User.getCountExternal",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}
	}
	
	
	@Transactional
	public User findByLogin(String login) throws DataAccessException {
		try {
			Query query = createNamedQuery("User.findByLogin", -1, -1, login);
			return (User) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	
	@Transactional
	public User findByEmail(String email) throws DataAccessException {
		try {
			Query query = createNamedQuery("User.findByEmail", -1, -1, email);
			return (User) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> searchByFirstName(String firstName) 	throws DataAccessException {
		Query query = createNamedQuery("User.searchByFirstName", -1, -1 , "%" + firstName +"%" );
		return new LinkedHashSet<User>(query.getResultList());
	}

	

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> searchByLastName(String lastName)	throws DataAccessException {
		Query query = createNamedQuery("User.searchByLastName", -1, -1 , "%" + lastName +"%" );
		return new LinkedHashSet<User>(query.getResultList());
	}

	

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> searchByFirstNameAndLastName(String firstName , String lastName) 	throws DataAccessException {
		Query query = createNamedQuery("User.searchByFirstNameAndLastName", -1, -1 , "%" + firstName +"%" , "%" + lastName +"%");
		return new LinkedHashSet<User>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> searchByLogin(String login)	throws DataAccessException {
		Query query = createNamedQuery("User.searchByLogin", -1, -1 , "%" + login +"%" );
		return new LinkedHashSet<User>(query.getResultList());
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<User> searchByEmail(String email)	throws DataAccessException {
		Query query = createNamedQuery("User.searchByEmail", -1, -1 , "%" + email +"%" );
		return new LinkedHashSet<User>(query.getResultList());
	}

	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public int deleteByDepartmentId(Long id) throws DataAccessException {
		Query query = createNamedQuery("deleteByDepartmentId", 0, 0, id);
		return query.executeUpdate();
		
	}

	
	

	

	public boolean canBeMerged(User entity) {
		return true;


	}

	
	


}