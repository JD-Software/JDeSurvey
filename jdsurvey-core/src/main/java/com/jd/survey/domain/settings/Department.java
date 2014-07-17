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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotBlank;

import com.jd.survey.domain.security.User;




@Entity
@NamedQueries({
	@NamedQuery(name = "Department.findAll", query = "select o from Department o order by o.name asc"),
	@NamedQuery(name = "Department.findById", query = "select o from Department o where o.id = ?1"),
	@NamedQuery(name = "Department.findByName", query = "select o from Department o where o.name = ?1"),
	@NamedQuery(name = "Department.getCount", query = "select count(o) from Department o"),
	@NamedQuery(name = "Department.getUserDepartments", query = "select d from Department d where d.id in (select ud.id from User u join  u.departments ud where u.login=?)")
		})
public class Department implements Comparable <Department> ,Serializable {

	private static final long serialVersionUID = -3925744214916426959L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotBlank
    @Column(unique = true,length = 75, nullable= false)
    @Size(max = 75)
    private String name;
    
    @Size(max = 2000)
    @Column(length = 2000, nullable= true)
    private String description;

    
    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="department")
    @Sort(type = SortType.NATURAL)
    private SortedSet<SurveyDefinition> surveyDefinitions = new TreeSet<SurveyDefinition>();
    
    
    @NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_user_department",joinColumns={@JoinColumn(name="department_id", referencedColumnName="id")},
												inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
	private SortedSet<User> users= new TreeSet<User>();
    
    

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

	
	public SortedSet<SurveyDefinition> getSurveyDefinitions() {
		return surveyDefinitions;
	}

	public void setSurveyDefinitions(SortedSet<SurveyDefinition> surveyDefinitions) {
		this.surveyDefinitions = surveyDefinitions;
	}
	
	

	public SortedSet<User> getUsers() {
		return users;
	}

	public void setUsers(SortedSet<User> users) {
		this.users = users;
	}

	public String toString() {
        return this.name;
    }

	

	//Comparable interface
    @Override
	public int compareTo(Department that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<String> thisDepartment = this.getName();
		Comparable<String> thatDepartment = that.getName();
		if(thisDepartment == null) {
			return AFTER;
		} else if(thatDepartment == null) {
			return BEFORE;
		} else {
			return thisDepartment.compareTo(that.getName());
		}
    
    }
	
	
}
