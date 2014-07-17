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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.settings.InvitationDAO;
import com.jd.survey.domain.settings.Invitation;

/** DAO implementation to handle persistence for object :Invitation
 */
@Repository("InvitationDAO")
@Transactional
public class InvitationDAOImpl extends AbstractJpaDao<Invitation> implements	InvitationDAO {
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Invitation.class }));

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public InvitationDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Override
	@Transactional
	public SortedSet<Invitation> findSurveyAll(Long surveyDefinitionId) throws DataAccessException {
		return findSurveyAll(surveyDefinitionId , -1, -1);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public SortedSet<Invitation> findSurveyAll(Long surveyDefinitionId,int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Invitation.findSurveyAll", startResult,maxRows , surveyDefinitionId);
		return new TreeSet<Invitation>(query.getResultList());
	}

	@Override
	@Transactional
	public Invitation findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Invitation.findById", -1, -1, id);
			return (Invitation) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}

	}
	
	@Override
	@Transactional
	public Invitation findByUuid(String uuid) throws DataAccessException {
		try {
			Query query = createNamedQuery("Invitation.findByUuid", -1, -1, uuid);
			return (Invitation) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}

	}


	@Override
	@Transactional
	public Long getSurveyCount(Long surveyDefinitionId) throws DataAccessException {
		try {
			Query query = createNamedQuery("Invitation.getSurveyCount",-1,-1,surveyDefinitionId);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}
	}

	
	@Override
	@Transactional
	public Long getSurveyOpenedCount(Long surveyDefinitionId) throws DataAccessException {
		try {
			Query query = createNamedQuery("Invitation.getSurveyOpenedCount",-1,-1, surveyDefinitionId);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			
			return null;
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SortedSet<Invitation> searchByFirstName(String firstName) 	throws DataAccessException {
		Query query = createNamedQuery("Invitation.searchByFirstName", -1, -1 , "%" + firstName +"%" );
		return new TreeSet<Invitation>(query.getResultList());
	}

	

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SortedSet<Invitation> searchByLastName(String lastName)	throws DataAccessException {
		Query query = createNamedQuery("Invitation.searchByLastName", -1, -1 , "%" + lastName +"%" );
		return new TreeSet<Invitation>(query.getResultList());
	}

	

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SortedSet<Invitation> searchByFirstNameAndLastName(String firstName , String lastName) 	throws DataAccessException {
		Query query = createNamedQuery("Invitation.searchByFirstNameAndLastName", -1, -1 , "%" + firstName +"%" , "%" + lastName +"%");
		return new TreeSet<Invitation>(query.getResultList());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SortedSet<Invitation> searchByEmail(String email)	throws DataAccessException {
		Query query = createNamedQuery("Invitation.searchByEmail", -1, -1 , "%" + email +"%" );
		return new TreeSet<Invitation>(query.getResultList());
	}

	
	
	

	
	

	

	public boolean canBeMerged(Invitation entity) {
		return true;


	}

	


	
	


}