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

import com.jd.survey.domain.security.Authority;

import java.lang.Long;
import java.util.Set;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 */
public interface AuthorityDAO extends JpaDao<Authority> {
	public Set<Authority> findAll() throws DataAccessException;
	public Set<Authority> findAllInternal() throws DataAccessException;
	public Set<Authority> findAllExternal() throws DataAccessException;
	public Set<Authority> findAll(int startResult, int maxRows) throws DataAccessException;
	public Authority findById(Long id) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public Authority findByName(String name) throws DataAccessException;
	public  Set<Authority> findbyUserId(Long id) throws DataAccessException;

}