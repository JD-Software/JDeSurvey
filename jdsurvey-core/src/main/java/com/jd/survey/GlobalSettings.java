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
package com.jd.survey;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;



@Entity
@NamedQueries({
	@NamedQuery(name = "GlobalSettings.findAll", query = "select o from GlobalSettings o"),
	@NamedQuery(name = "GlobalSettings.findById", query = "select o from GlobalSettings o where o.id = ?1"),
	@NamedQuery(name = "GlobalSettings.findBypasswordEnforcementRegex", query = "select o from GlobalSettings o where o.passwordEnforcementRegex = ?1"),
	@NamedQuery(name = "GlobalSettings.getCount", query = "select count(o) from GlobalSettings o")
})
public class GlobalSettings {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;
	
	@NotNull
	@NotEmpty
	@Column(name = "password_regex")
	private String passwordEnforcementRegex;
	
	@NotNull
	@NotEmpty
	@Size(max = 500)
	@Column(length = 500)
	private String passwordEnforcementMessage;
		
	@NotNull
	@NotEmpty
	@Lob
	@Size(max = 3000)
	@Column(length = 3000, nullable= false)
	private String validContentTypes;
	
	@NotNull
	@NotEmpty
	@Lob
	@Size(max = 1000)
	@Column(length = 1000, nullable= false)
	private String validImageTypes;
		
	@NotNull
	private Integer maximunFileSize;
	
	@NotNull
	@NotEmpty
	@Size(max = 500)
	@Column(length = 500, nullable= false)
	private String invalidContentMessage;
	
	@NotNull
	@NotEmpty
	@Size(max = 500)
	@Column(length = 500, nullable= false)
	private String invalidFileSizeMessage;

	public String getValidContentTypes() {
		return validContentTypes;
	}

	public void setValidContentTypes(String validContentTypes) {
		this.validContentTypes = validContentTypes;
	}

	public String getValidImageTypes() {
		return validImageTypes;
	}

	public void setValidImageTypes(String validImageTypes) {
		this.validImageTypes = validImageTypes;
	}

	public List<String> getValidContentTypesAsList() {
		return Arrays.asList(validContentTypes.toLowerCase().split(","));
	}
	
	public List<String> getValidImageTypesAsList() {
		return Arrays.asList(validImageTypes.toLowerCase().split(","));
	}
	

	public Integer getMaximunFileSize() {
		return maximunFileSize;
	}

	public void setMaximunFileSize(Integer maximunFileSize) {
		this.maximunFileSize = maximunFileSize;
	}

	public String getInvalidContentMessage() {
		return invalidContentMessage;
	}

	public void setInvalidContentMessage(String invalidContentMessage) {
		this.invalidContentMessage = invalidContentMessage;
	}

	public String getInvalidFileSizeMessage() {
		return invalidFileSizeMessage;
	}

	public void setInvalidFileSizeMessage(String invalidFileSizeMessage) {
		this.invalidFileSizeMessage = invalidFileSizeMessage;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getPasswordEnforcementRegex() {
		return passwordEnforcementRegex;
	}

	public void setPasswordEnforcementRegex(String passwordEnforcementRegex) {
		this.passwordEnforcementRegex = passwordEnforcementRegex;
	}

	public String getPasswordEnforcementMessage() {
		return passwordEnforcementMessage;
	}

	public void setPasswordEnforcementMessage(String passwordEnforcementMessage) {
		this.passwordEnforcementMessage = passwordEnforcementMessage;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}