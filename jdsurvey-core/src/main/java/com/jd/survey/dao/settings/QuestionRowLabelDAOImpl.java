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

import com.jd.survey.dao.interfaces.settings.QuestionRowLabelDAO;
import com.jd.survey.domain.settings.QuestionRowLabel;


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

/** DAO implementation to handle persistence for object :QuestionRowLabel
 */
@Repository("QuestionRowLabelDAO")
@Transactional
public class QuestionRowLabelDAOImpl extends AbstractJpaDao<QuestionRowLabel> implements	QuestionRowLabelDAO {

	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { QuestionRowLabel.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public QuestionRowLabelDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	
	@Transactional
	public QuestionRowLabel findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("QuestionRowLabel.findById", -1, -1, id);
			return (QuestionRowLabel) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<QuestionRowLabel> findByQuestionId(Long id)	throws DataAccessException {
		Query query = createNamedQuery("QuestionRowLabel.findByQuestionId", -1,-1, id);
		return new LinkedHashSet<QuestionRowLabel>(query.getResultList());
	}
	
	@Transactional
	public int deleteByQuestionId(Long id) throws DataAccessException {
		Query query = createNamedQuery("QuestionRowLabel.deleteByQuestionId", 0, 0, id);
		return query.executeUpdate();
	}
	

	public boolean canBeMerged(QuestionRowLabel entity) {
		return true;
	}

	

}
