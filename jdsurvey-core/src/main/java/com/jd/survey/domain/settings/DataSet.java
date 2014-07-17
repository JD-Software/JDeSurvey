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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	@NamedQuery(name = "DataSet.findAll", query = "select o from DataSet o"),
	@NamedQuery(name = "DataSet.findById", query = "select o from DataSet o where o.id = ?1"),
	@NamedQuery(name = "DataSet.findByName", query = "select o from DataSet o where o.name = ?1"),
	@NamedQuery(name = "DataSet.getCount", query = "select count(o) from DataSet o")
})
public class DataSet implements Serializable{

	private static final long serialVersionUID = -7065417669111580160L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	@Enumerated(EnumType.STRING)
    private DataSetStatus status = DataSetStatus.I;
	
	/*
	@NotBlank
	@Size(max = 10)
	@Column( length = 10, nullable= false)
	private String code;
	*/

	@NotBlank
	@Size(max = 75)
	@Column(unique= true, length = 75, nullable= false)
	private String name;
	
	
	//@NotNull
	//@NotEmpty
	@Size(max = 200)
	@Column(length = 200, nullable= false)
	private String description;
	
	
	


	
	@OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY, mappedBy="dataSet")
	//@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
	@Sort(type = SortType.NATURAL)
	private SortedSet<DataSetItem> items = new TreeSet<DataSetItem>();


	public DataSet() {
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

	
	
	/*
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}*/

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

	public SortedSet<DataSetItem> getItems() {
		return items;
	}

	public void setItems(SortedSet<DataSetItem> items) {
		this.items = items;
	}

	public DataSetStatus getStatus() {
		return status;
	}

	public void setStatus(DataSetStatus status) {
		this.status = status;
	}

	public String toString() {
		return "set:"  + this.name;
	}












}
