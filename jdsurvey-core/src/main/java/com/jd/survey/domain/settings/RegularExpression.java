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
package com.jd.survey.domain.settings;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotBlank;




@Entity
@NamedQueries({
	@NamedQuery(name = "RegularExpression.findAll", query = "select o from RegularExpression o"),
	@NamedQuery(name = "RegularExpression.findById", query = "select o from RegularExpression o where o.id = ?1"),
	@NamedQuery(name = "RegularExpression.findByName", query = "select o from RegularExpression o where o.name = ?1"),
	@NamedQuery(name = "RegularExpression.getCount", query = "select count(o) from DataSet o")
})
public class RegularExpression implements Serializable {

	private static final long serialVersionUID = 2177816806369647192L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;
	
	
	@NotBlank
	@Size(max = 250)
	@Column(length = 250, nullable= false)
	private String regex;
	

	@NotBlank
	@Size(max = 500)
	@Column(length = 500, nullable= false)
	private String name;
	
	

	@Size(max = 2000)
	@Column(length = 2000, nullable= true)
	private String description;
	
	
	public RegularExpression() {
		super();
	}

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Integer getVersion() {
		return version;
	}



	public void setVersion(Integer version) {
		this.version = version;
	}



	public String getRegex() {
		return regex;
	}



	public void setRegex(String regex) {
		this.regex = regex;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



	


}
