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
package com.jd.survey.dao.interfaces.settings;

import java.util.Set;

import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;
import com.jd.survey.domain.settings.Sector;

public interface SectorDAO extends JpaDao<Sector> {
	public Set<Sector> findAll() throws DataAccessException;
	public Set<Sector> findAll(int startResult, int maxRows) throws DataAccessException;
	public Sector findById(Long id) throws DataAccessException;
	//public Sector findByCode(String code) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public Sector findByName(String name) throws DataAccessException;;
}