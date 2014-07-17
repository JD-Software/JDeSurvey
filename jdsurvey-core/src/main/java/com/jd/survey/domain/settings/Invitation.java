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
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.jd.survey.domain.survey.SurveyEntry;





@Entity
@Table(name = "invitation")
@NamedQueries({
	@NamedQuery(name = "Invitation.findSurveyAll", query = "select o from Invitation o where o.surveyDefinition.id = ?1 order by o.invitationEmailSentDate desc "),
	@NamedQuery(name = "Invitation.findById", query = "select o from Invitation o where o.id = ?1"),
	@NamedQuery(name = "Invitation.findByUuid", query = "select o from Invitation o where o.uuid = ?1"),	
	@NamedQuery(name = "Invitation.getSurveyCount", query = "select count(o) from Invitation o where o.surveyDefinition.id = ?1"),
	@NamedQuery(name = "Invitation.getSurveyOpenedCount", query = "select count(o) from Invitation o where o.invitationEmailOpenedDate is not null and o.surveyDefinition.id = ?1"),
	@NamedQuery(name = "Invitation.searchByFirstName", query = "select o from Invitation o where  o.firstName like ?1 order by o.invitationEmailSentDate desc"),
	@NamedQuery(name = "Invitation.searchByLastName", query = "select o from Invitation o where  o.lastName like ?1 order by o.invitationEmailSentDate desc"),
	@NamedQuery(name = "Invitation.searchByFirstNameAndLastName", query = "select o from Invitation o where  o.firstName like ?1 or o.lastName like ?2 order by o.invitationEmailSentDate desc"),
	@NamedQuery(name = "Invitation.searchByEmail", query = "select o from Invitation o where  o.email like ?1 order by o.invitationEmailSentDate desc")	
})
public class Invitation implements Serializable, Comparable <Invitation>{
	private static final long serialVersionUID = -5811352739020913066L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= false)
	private Date invitationEmailSentDate;

	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= true)
	private Date invitationEmailOpenedDate;

	
	@Column(length = 75, nullable= true)
	private String firstName;


	@Column(length = 75, nullable= true)
	private String middleName;

	@Column(length = 75, nullable= true)
	private String lastName;

	
	@NotBlank
	@NotNull
	@Column(length = 100, nullable=false)
	private String email;

	//A unique identifier of the invitation 
	@Column(length = 500, nullable= false)
	private String uuid;
	
	
	@NotNull
    @ManyToOne
    @JoinColumn(name = "SURVEY_DEFINITION_ID")
    private SurveyDefinition surveyDefinition;


    
	public Invitation() {
		super();
		this.uuid = UUID.randomUUID().toString().replace("-", "");
		this.invitationEmailSentDate = new Date();
	}
    
    


	public Invitation(String firstName, String middleName, String lastName,
			String email, SurveyDefinition surveyDefinition) {
		super();
		this.uuid = UUID.randomUUID().toString().replace("-", "");
		this.invitationEmailSentDate = new Date();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.surveyDefinition = surveyDefinition;
	}


	public void updateAsRead() {
		this.invitationEmailOpenedDate = new Date();
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


	public Date getInvitationEmailSentDate() {
		return invitationEmailSentDate;
	}


	public void setInvitationEmailSentDate(Date invitationEmailSentDate) {
		this.invitationEmailSentDate = invitationEmailSentDate;
	}


	public Date getInvitationEmailOpenedDate() {
		return invitationEmailOpenedDate;
	}


	public void setInvitationEmailOpenedDate(Date invitationEmailOpenedDate) {
		this.invitationEmailOpenedDate = invitationEmailOpenedDate;
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


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
	public SurveyDefinition getSurveyDefinition() {
		return surveyDefinition;
	}


	public void setSurveyDefinition(SurveyDefinition surveyDefinition) {
		this.surveyDefinition = surveyDefinition;
	}
    
	
	public String getFullName() {
		StringBuilder s = new StringBuilder("");
		if (this.firstName != null) {
			if (this.firstName != null) {s.append(firstName).append(" ").append(lastName);} else {s.append(firstName);}
		}	
		else{
			if (this.lastName != null) {s.append(lastName);}
		} 
		return s.toString();
	}
	
	//comparable interface
			@Override
			public int compareTo(Invitation that) {
				final int BEFORE = -1;
				final int AFTER = 1;
				if (that == null) {
					return BEFORE;
				}
				Comparable<Date> thisInvitationSentDate = this.getInvitationEmailSentDate();
				Comparable<Date> thatInvitationSentDate = that.getInvitationEmailSentDate();
				if(thisInvitationSentDate == null) {
					return AFTER;
				} else if(thatInvitationSentDate == null) {
					return BEFORE;
				} else {
					if  (thisInvitationSentDate.compareTo(that.getInvitationEmailSentDate()) == 0) {
						return this.id.compareTo(that.id);
						} 
					else {
						return thisInvitationSentDate.compareTo(that.getInvitationEmailSentDate());
					}
				}
			}
	
}
