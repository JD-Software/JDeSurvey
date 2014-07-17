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
package com.jd.survey.domain.security;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;

import com.jd.survey.domain.settings.SurveyDefinition;


@Entity
@Table(name = "sec_group")
@NamedQueries({
	@NamedQuery(name = "Group.findAll", query = "select o from Group o order by o.type desc,o.name asc"),
	@NamedQuery(name = "Group.findAllInternal", query = "select o from Group o where o.type='I' order by o.name asc"),
	@NamedQuery(name = "Group.findAllExternal", query = "select o from Group o where o.type='E' order by o.name asc"),
	@NamedQuery(name = "Group.findById", query = "select o from Group o where o.id = ?1"),
	@NamedQuery(name = "Group.findByName", query = "select o from Group o where o.name = ?1"),
	@NamedQuery(name = "Group.getCount", query = "select count(o) from Group o")
	})
public class Group implements  Comparable <Group> ,Serializable, SecurityObject {

	private static final long serialVersionUID = 1533223669720699638L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "GROUP_TYPE")
	private SecurityType type;
	
	@NotNull
	@NotEmpty
	@Size(max = 75)
	@Column(unique = true,length = 75, nullable= false)
	private String name;
	
	@Size(max = 500)
	@Column(length = 500)
    private String description;

	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_group_authority",joinColumns={@JoinColumn(name="group_id", referencedColumnName="id")},inverseJoinColumns={@JoinColumn(name="authority_id", referencedColumnName="id")})
	private SortedSet<Authority> authorities  = new TreeSet<Authority>();
	
	
	@NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_user_group",joinColumns={@JoinColumn(name="group_id", referencedColumnName="id")},
	inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
	private SortedSet<User> users = new TreeSet<User>();

	
	
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
	
	public SecurityType getType() {
		return type;
	}

	public void setType(SecurityType type) {
		this.type = type;
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
	
	public SortedSet<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(SortedSet<Authority> authorities) {
		this.authorities = authorities;
	}
	
	

	public String toString() {
        return this.name;
    }
	
	
	
	
	public Group(SecurityType type) {
		super();
		this.type = type;
	}
	
	public Group() {
		super();
		// TODO Auto-generated constructor stub
	}

	//Comparable interface
    @Override
	public int compareTo(Group that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<String> thisGroup = this.getName();
		Comparable<String> thatGroup = that.getName();
		if(thisGroup == null) {
			return AFTER;
		} else if(thatGroup == null) {
			return BEFORE;
		} else {
			return thisGroup.compareTo(that.getName());
		}
    
    }

	public SortedSet<User> getUsers() {
		return users;
	}

	public void setUsers(SortedSet<User> users) {
		this.users = users;
	}
	
  

	
	
}
