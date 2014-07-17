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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@NamedQueries({
	@NamedQuery(name = "SurveyTemplate.findAll", query = "select o from SurveyTemplate o"),
	@NamedQuery(name = "SurveyTemplate.findById", query = "select o from SurveyTemplate o where o.id = ?1"),
	@NamedQuery(name = "SurveyTemplate.getCount", query = "select count(o) from SurveyTemplate o where o.sector.id = ?1"),
	@NamedQuery(name = "SurveyTemplate.deleteBySectorId", query = "delete from SurveyTemplate o where o.sector.id=?1"),
	@NamedQuery(name = "SurveyTemplate.findBySectorId", query = "select o from SurveyTemplate o where o.sector.id=?1 order by o.name"),
	@NamedQuery(name = "SurveyTemplate.findByName", query = "select o from SurveyTemplate o where o.name = ?1")
	})
public class SurveyTemplate implements Comparable<SurveyTemplate>, Serializable{

	private static final long serialVersionUID = -1953281702005408717L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotBlank
	@Size(max = 75)
	@Column(unique= false, length = 75, nullable= false)
	private String name;
	
	@Size(max = 200)
	@Column(length = 200, nullable= false)
	private String description;
	
	@Lob 
	@NotNull
    @NotEmpty
    @Column(name = "JSON_CODE", nullable= false)
    private String json;
	
    @NotNull
    @ManyToOne
    @JoinColumn(name = "SECTOR_ID")
    private Sector sector;
    
    public SurveyTemplate() {
		super();
	}
    
	public SurveyTemplate(Sector sector, String name,  String decription, String json) {
		super();
		this.json = json;
		this.name = name;
		this.name = description;
		this.sector = sector;
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

	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}


	public Sector getSector() {
		return sector;
	}


	public void setSector(Sector sector) {
		this.sector = sector;
	}


	//comparable interface
	@Override
	public int compareTo(SurveyTemplate that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<String> thisSurveyTemplate = this.getName();
		Comparable<String> thatSurveyTemplate = that.getName();
		if(thisSurveyTemplate == null) {
			return AFTER;
		} else if(thatSurveyTemplate == null) {
			return BEFORE;
		} else {
			return thisSurveyTemplate.compareTo(that.getName());
		}
	}
	
	
	
	
}
