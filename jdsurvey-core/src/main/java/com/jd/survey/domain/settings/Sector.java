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
	@NamedQuery(name = "Sector.findAll", query = "select o from Sector o"),
	@NamedQuery(name = "Sector.findById", query = "select o from Sector o where o.id = ?1"),
	@NamedQuery(name = "Sector.findByName", query = "select o from Sector o where o.name = ?1"),
	@NamedQuery(name = "Sector.getCount", query = "select count(o) from Sector o")
})

public class Sector implements Serializable {

	private static final long serialVersionUID = -7800705420832403822L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@NotBlank
	@Size(max = 75)
	@Column(unique= true, length = 75, nullable= false)
	private String name;
	
	@Size(max = 200)
	@Column(length = 200, nullable= false)
	private String description;
	
	@Version
	@Column(name = "version")
	private Integer version;
	
	@OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY, mappedBy="sector")
	@Sort(type = SortType.NATURAL)
	private SortedSet<SurveyTemplate> templates = new TreeSet<SurveyTemplate>();


	public Sector() {
		super();
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public SortedSet<SurveyTemplate> getTemplates(){
		return templates;
	}
	
	public void setTemplates(SortedSet<SurveyTemplate> templates) {
		this.templates = templates;
	}
	
	
	
	

}
