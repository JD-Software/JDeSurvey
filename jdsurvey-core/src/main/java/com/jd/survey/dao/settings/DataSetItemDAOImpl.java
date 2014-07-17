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

import com.jd.survey.dao.interfaces.settings.DataSetItemDAO;
import com.jd.survey.domain.settings.DataSetItem;

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

/** DAO implementation to handle persistence for object :DataSetItem
 */
@Repository("DataSetItemDAO")
@Transactional
public class DataSetItemDAOImpl extends AbstractJpaDao<DataSetItem> implements	DataSetItemDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { DataSetItem.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public DataSetItemDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<DataSetItem> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DataSetItem> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("DataSetItem.findAll", startResult,maxRows);
		return new LinkedHashSet<DataSetItem>(query.getResultList());
	}

	@Transactional
	public DataSetItem findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("DataSetItem.findById", -1, -1, id);
			return (DataSetItem) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DataSetItem> findByDataSetId(Long id)	throws DataAccessException {
		return findByDataSetId(id,-1,-1);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<DataSetItem> findByDataSetId(Long id,int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("DataSetItem.findByDataSetId", startResult,maxRows, id);
		return new LinkedHashSet<DataSetItem>(query.getResultList());
	}
	
	@Transactional
	public Long getCount(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("DataSetItem.getCount",-1,-1, id);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public boolean canBeMerged(DataSetItem entity) {
		return true;
	}

	@Transactional
	public int deleteByDataSetId(Long id) throws DataAccessException {
		Query query = createNamedQuery("DataSetItem.deleteByDataSetId", 0, 0, id);
		return query.executeUpdate();
	}

}
