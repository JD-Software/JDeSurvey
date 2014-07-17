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

import com.jd.survey.dao.interfaces.settings.DepartmentDAO;
import com.jd.survey.domain.settings.Department;

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

/** DAO implementation to handle persistence for object :Department
 */
@Repository("DepartmentDAO")
@Transactional
public class DepartmentDAOImpl extends AbstractJpaDao<Department> implements	DepartmentDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Department.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public DepartmentDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<Department> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Department> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Department.findAll", startResult,maxRows);
		return new LinkedHashSet<Department>(query.getResultList());
	}

	@Transactional
	public Department findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Department.findById", -1, -1, id);
			return (Department) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public Department findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("Department.findByName", -1, -1, name);
			return (Department) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}






	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("Department.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public boolean canBeMerged(Department entity) {
		return true;
	}


	@Transactional
	public Set<Department> getUserDepartments(String login ,int startResult, int maxRows) throws DataAccessException {
		try{
			Query query = createNamedQuery("Department.getUserDepartments", startResult, maxRows, login);
			return new LinkedHashSet<Department>(query.getResultList());
		}catch(NoResultException nre) {
			return null;
		}
	}


	@Transactional
	public Set<Department> getUserDepartments(String login ) throws DataAccessException {
		try {
			Query query = createNamedQuery("Department.getUserDepartments", -1, -1, login);
			return new LinkedHashSet<Department>(query.getResultList());
		} catch (NoResultException nre) {
			return null;
		}
	}

}





