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
package com.jd.survey.dao.interfaces.security;

import com.jd.survey.domain.security.User;
import java.lang.Long;
import java.util.Set;

import javax.persistence.NamedQuery;

import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 */
public interface UserDAO extends JpaDao<User> {
	public Set<User> findAll() throws DataAccessException;
	public Set<User> findAll(int startResult, int maxRows) throws DataAccessException;
	
	public Set<User> findAllExternal() throws DataAccessException;
	public Set<User> findAllExternal(int startResult, int maxRows) throws DataAccessException;
	
	
	public Set<User> findAllInternal() throws DataAccessException;
	public Set<User> findAllInternal(int startResult, int maxRows) throws DataAccessException;
	
	
	
	public User findById(Long id) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public Long getCountInternal() throws DataAccessException;
	public Long getCountExternal() throws DataAccessException;
	public User findByLogin(String login) throws DataAccessException;
	public User findByEmail(String login) throws DataAccessException;
	
	
	
	public Set<User> searchByFirstName(String firstName) throws DataAccessException;
	public Set<User> searchByLastName(String lastName) throws DataAccessException;
	public Set<User> searchByFirstNameAndLastName(String firstName , String lastName) throws DataAccessException;
	public Set<User> searchByLogin(String login) throws DataAccessException;
	public Set<User> searchByEmail(String email) throws DataAccessException;
	
	public int deleteByDepartmentId(Long id) throws DataAccessException;
}