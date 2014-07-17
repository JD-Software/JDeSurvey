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

import com.jd.survey.dao.interfaces.settings.RegularExpressionDAO;
import com.jd.survey.domain.settings.RegularExpression;

@Repository("RegularExpressionDAO")
@Transactional
public class RegularExpressionDAOImpl extends AbstractJpaDao<RegularExpression> implements	RegularExpressionDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] {RegularExpression.class})) ;
	
	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public RegularExpressionDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<RegularExpression> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<RegularExpression> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("RegularExpression.findAll", startResult,maxRows);
		return new LinkedHashSet<RegularExpression>(query.getResultList());
	}

	@Transactional
	public RegularExpression findByName(String name) throws DataAccessException {
		try {
			Query query = createNamedQuery("RegularExpression.findByName", -1, -1, name);
			return (RegularExpression) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public RegularExpression findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("RegularExpression.findById", -1, -1, id);
			return (RegularExpression) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("RegularExpression.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	

	
	
	
	public boolean canBeMerged(RegularExpression entity) {
		return true;
	}

	@Override
	public Long getCount(Long Id) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
