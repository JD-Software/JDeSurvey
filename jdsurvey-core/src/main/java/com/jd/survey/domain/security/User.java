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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.settings.Department;
import com.jd.survey.domain.settings.SurveyDefinition;





@Entity
@Table(name = "sec_user")
@NamedQueries({
	@NamedQuery(name = "User.findAll", query = "select o from User o order by o.type desc,o.lastName asc "),
	@NamedQuery(name = "User.findAllInternal", query = "select o from User o where o.type='I' order by o.lastName asc "),
	@NamedQuery(name = "User.findAllExternal", query = "select o from User o where o.type='E' order by o.lastName asc "),
	@NamedQuery(name = "User.findById", query = "select o from User o where o.id = ?1"),
	@NamedQuery(name = "User.findByLogin", query = "select o from User o left join fetch o.groups g left join fetch g.authorities a where o.login = ?1"),
	@NamedQuery(name = "User.findByFirstName", query = "select o from User o where  o.firstName = ?1"),
	@NamedQuery(name = "User.findByEmail", query = "select o from User o where  o.email= ?1"),
	
	@NamedQuery(name = "User.getCount", query = "select count(o) from User o"),
	@NamedQuery(name = "User.getCountInternal", query = "select count(o) from User o  where o.type='I'"),
	@NamedQuery(name = "User.getCountExternal", query = "select count(o) from User o  where o.type='E'"),
	
	
	@NamedQuery(name = "User.searchByFirstName", query = "select o from User o where  o.firstName like ?1"),
	@NamedQuery(name = "User.searchByLastName", query = "select o from User o where  o.lastName like ?1"),
	@NamedQuery(name = "User.searchByFirstNameAndLastName", query = "select o from User o where  o.firstName like ?1 or o.lastName like ?2"),
	@NamedQuery(name = "User.searchByEmail", query = "select o from User o where  o.email like ?1"),
	@NamedQuery(name = "User.searchByLogin", query = "select o from User o where o.login like ?1")
	
	
})
public class User implements Comparable <User>, UserDetails , Serializable , SecurityObject{
	private static final long serialVersionUID = 5913313636780482541L;
	private static final String SALT = "@JDe$urvey#";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_SURVEY_ADMIN = "ROLE_SURVEY_ADMIN"; 
	private static final String ROLE_SURVEY_PARTICIPANT = "ROLE_SURVEY_PARTICIPANT";
	
	
	public interface UserInfo {}
	public interface Password {}
	public interface UserSearchByName {}
	public interface UserSearchByEmail {}
	public interface UserSearchByLogin {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	@Enumerated(EnumType.STRING)
	@Column(name = "USER_TYPE")
	private SecurityType type;

	@NotBlank
	@NotNull
	@NotEmpty(groups={UserInfo.class})
	@Size(max = 100, groups={UserInfo.class, UserSearchByLogin.class})
	@Column(unique = true, length = 100, nullable= false)
	private String login;

	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= false)
	private Date creationDate;

	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= true)
	private Date lastUpdateDate;


	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	@NotNull(groups={UserInfo.class})
	@Column(nullable= false)
	private Date dateOfBirth;

	@NotBlank
	@NotNull
	@NotEmpty(groups={UserInfo.class})
	@Pattern (regexp ="^[0-9a-zA-Z\\.\\-, ]{0,75}$" , groups={UserInfo.class,UserSearchByName.class})
	@Column(length = 75, nullable= false)
	private String firstName;


	@Pattern (regexp ="^[0-9a-zA-Z\\.\\-, ]{0,75}$" , groups={UserInfo.class,UserSearchByName.class})
	@Column(length = 75)
	private String middleName;

	@NotBlank
	@NotNull
	@NotEmpty(groups={UserInfo.class})
	@Pattern (regexp ="^[0-9a-zA-Z\\.\\-, ]{0,75}$" , groups={UserInfo.class,UserSearchByName.class})
	@Column(length = 75, nullable= false)
	private String lastName;

	
	@NotBlank
	@NotNull
	@NotEmpty(groups={UserInfo.class})
	@Email(groups={UserInfo.class, UserSearchByEmail.class})
	@Column(unique = true,length = 100, nullable=false)
	private String email;

	//The password must be at least eight characters long, contain at least one number, contain at least one lower case letter, 
	//contain at least upper case letter, contain at least one of these special characters ([ @ # $ % ^ & + = ]) 
	//and not contain any white characters.
	//@Pattern (regexp =GlobalSettings.PASSWORD_ENFORCEMENT_REGEX , groups={Password.class})
	@Column(length = 500, nullable= false)
	private String password;

	@Transient
	@Size(max = 20)
	private String confirmPassword;

	
	
	private boolean enabled;

	@NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_user_group",joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
									 inverseJoinColumns={@JoinColumn(name="group_id", referencedColumnName="id")})
	private SortedSet<Group> groups = new TreeSet<Group>();


	@NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_user_department",joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
										  inverseJoinColumns={@JoinColumn(name="department_id", referencedColumnName="id")})
	private SortedSet<Department> departments = new TreeSet<Department>();

	@NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="sec_user_surveyDefinition",joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
												inverseJoinColumns={@JoinColumn(name="surveyDefinition_id", referencedColumnName="id")})
	private SortedSet<SurveyDefinition> surveyDefinitions = new TreeSet<SurveyDefinition>();
	
	
	
	public User() {
		super();
	}
	
	
	


	public User(SecurityType type) {
		super();
		this.type = type;
	}





	public Boolean isSurveyParticipant() {
		return hasRole(ROLE_SURVEY_PARTICIPANT);
	}


	public Boolean isSurveyAdmin() {
		return hasRole(ROLE_SURVEY_ADMIN);
	}

	public Boolean isAdmin() {
		return hasRole(ROLE_ADMIN);
	}

	private Boolean hasRole(String roleName){
		for (Group group : this.groups ){
			for (Authority authority: group.getAuthorities()){
				if (authority.getName() != null  && authority.getName().equalsIgnoreCase(roleName) ){
					return true;
 
				}
			}

		}
		return false;
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

	public SecurityType getType() {
		return type;
	}


	public void setType(SecurityType type) {
		this.type = type;
	}


	public String getLogin() {
		if (login != null) {
			return login.trim().toLowerCase();
		}
		else
		{
			return login;
		}
	}

	public void setLogin(String login) {
		if (login != null) {
			this.login = login.trim().toLowerCase();
		}
		else
		{
			this.login = login;
		}	
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}



	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public SortedSet<Group> getGroups() {
		return groups;
	}

	public void setGroups(SortedSet<Group> groups) {
		this.groups = groups;
	}

	public SortedSet<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(SortedSet<Department> departments) {
		this.departments = departments;
	}
	


	public SortedSet<SurveyDefinition> getSurveyDefinitions() {
		return surveyDefinitions;
	}





	public void setSurveyDefinitions(SortedSet<SurveyDefinition> surveyDefinitions) {
		this.surveyDefinitions = surveyDefinitions;
	}



	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public String getSalt() {
		return SALT;
	}






	public String getFullName() {
		StringBuilder s = new StringBuilder("");
		if (this.firstName != null) {
			s.append(this.firstName).append(" ").append(this.lastName);
		}
		return s.toString();
	}
	














	public String toString() {
		return this.login;
	}

	@Override
	public Collection<Authority> getAuthorities() {
		List<Authority> authorities = new ArrayList<Authority> ();
		for (Group group :this.groups) {
			for (Authority authority :group.getAuthorities()) {
				authorities.add(authority);
			}
		}
		return authorities;
	}



	@Override
	public String getUsername() {
		return this.login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	
	public void  refreshUserInfo(User user) {
		this.enabled = user.getEnabled();
		this.login = user.getLogin();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.dateOfBirth = user.getDateOfBirth();
	}





	
	@Override
	public int compareTo(User that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<String> thisUser = this.getFirstName();
		Comparable<String> thatUser = that.getFirstName();
		if(thisUser == null) {
			return AFTER;
		} else if(thisUser == null) {
			return BEFORE;
		} else {
			return thisUser.compareTo(that.getFirstName());
		}
    
    }
	
	
	
	
	
	
	
	
	
}
