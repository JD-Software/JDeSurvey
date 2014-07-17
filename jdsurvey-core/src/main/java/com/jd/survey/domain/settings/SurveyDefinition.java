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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;
import com.jd.survey.domain.security.User;
import com.jd.survey.util.SortedSetUpdater;


@Entity
@NamedQueries({
	@NamedQuery(name = "SurveyDefinition.findAllInternal", query = "select o from SurveyDefinition o left join o.department d left join d.users u order by d.name, o.name"),
	@NamedQuery(name = "SurveyDefinition.findAllForManagerInternal", query = "select s from SurveyDefinition s  where s.department.id in (select ud.id from User u join  u.departments ud where u.login=?)"),
	@NamedQuery(name = "SurveyDefinition.findAllCompletedInternal", query = "select o from SurveyDefinition o left join o.department d where o.status!='I' order by d.name, o.name"),
	@NamedQuery(name = "SurveyDefinition.findAllCompletedForManagerInternal", query = "select s from SurveyDefinition s  where s.department.id in (select ud.id from User u join  u.departments ud where u.login=?) and s.status!='I'"),
	@NamedQuery(name = "SurveyDefinition.findAllPublishedInternal", query = "select o from SurveyDefinition o left join o.department d where o.status='P' order by d.name, o.name"),
	@NamedQuery(name = "SurveyDefinition.findAllPublishedForManagerInternal", query = "select s from SurveyDefinition s  where s.department.id in (select ud.id from User u join  u.departments ud where u.login=?) and s.status!='P'"),
	@NamedQuery(name = "SurveyDefinition.findAllPublishedExternal", query = "select o from SurveyDefinition o  left join o.department d where o.status='P' and o.id in (select sd.id from User u left join u.surveyDefinitions sd where u.login=?) order by d.name, o.name"),
	@NamedQuery(name = "SurveyDefinition.findAllPublishedPublic", query = "select o from SurveyDefinition o  left join o.department d where o.status='P' and o.isPublic=1 order by d.name, o.name"),
	@NamedQuery(name = "SurveyDefinition.findById", query = "select o from SurveyDefinition o where o.id = ?1"),
	@NamedQuery(name = "SurveyDefinition.findByIdEager", query = "select o from SurveyDefinition s " + 
																 "left join s.department d "+
																 "left join s.pages p "+
																 "left join p.questions q "+
																 "left join q.options  o " +
																 "where s.id = ?1"),
	@NamedQuery(name = "SurveyDefinition.findByName", query = "select o from SurveyDefinition o where o.name = ?1"),
	@NamedQuery(name = "SurveyDefinition.getCount", query = "select count(o) from SurveyDefinition o"),
	@NamedQuery(name = "SurveyDefinition.deleteByDepartmentId", query = "delete from SurveyDefinition o where o.department.id=?1"),
	@NamedQuery(name = "SurveyDefinition.getSurveyDefinitionUsers",query = "select s from SurveyDefinition s where s.id in (select us.id from User u join  u.surveyDefinitions us where u.login=?)")
	})
public class SurveyDefinition extends SortedSetUpdater<SurveyDefinitionPage> 
							 implements Comparable <SurveyDefinition>,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8791101346943625237L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	@Enumerated(EnumType.STRING)
	private SurveyTheme surveyTheme = SurveyTheme.STANDARD;
	
	@Enumerated(EnumType.STRING)
    private SurveyDefinitionStatus status = SurveyDefinitionStatus.I;
	
	
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private  Department department;
	
    
    @NotEmpty
    @Column(length = 75, nullable= false)
    @Size(max = 75)
    private String name;
    
    @Size(max = 2000)
    @Column(length = 2000, nullable= true)
    private String description;
    
    @Size(max = 5000)
    @Column(length = 5000, nullable= false)
    private String emailInvitationTemplate;

    @Size(max = 5000)
    @Column(length = 5000, nullable= false)
    private String completedSurveyTemplate;
    
    

    
    private boolean allowMultipleSubmissions;
    
    
    //@IndexColumn(name="QUESTION_ORDER")
    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="surveyDefinition")
    //@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER,mappedBy="surveyDefinition")
    @Sort(type = SortType.NATURAL)
    private SortedSet<SurveyDefinitionPage> pages = new TreeSet<SurveyDefinitionPage>();
    
    
    @NotNull
	@ManyToMany
	@Sort(type = SortType.NATURAL)
    @JoinTable(name="sec_user_surveyDefinition",joinColumns={@JoinColumn(name="surveyDefinition_id", referencedColumnName="id")},
												inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
	private SortedSet<User> users= new TreeSet<User>();
    

	private boolean isPublic;
	


	@Column
	private boolean sendAutoReminders;
	
	@Enumerated(EnumType.STRING)
	@Column(name="reminders_frequency")
	private Frequency autoRemindersFrequency;
	
	
	
	@Column(length = 2)
	private Integer autoRemindersWeeklyOccurrence;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Sort(type = SortType.NATURAL)
	@JoinTable(name="surveydefinition_reminders_daily_port_schedule",joinColumns={@JoinColumn(name="surveyDefinition_id", referencedColumnName="id")},
																	inverseJoinColumns={@JoinColumn(name="day_id", referencedColumnName="id")})
	private SortedSet<Day> autoRemindersDays= new TreeSet<Day>();

	
	
	@Column(length = 2)
	private Integer autoRemindersMonthlyOccurrence ;
	@Column(length = 2)
	private Integer autoRemindersDayOfMonth;
	
	@Column()
	private Date  autoReminderLastSentDate;
	
	
	
	
	@Lob
	private byte[]  logo;
	 
	
	
	

	public SurveyDefinition(){
		super();

		
		
		this.sendAutoReminders = false;
		this.autoRemindersFrequency = Frequency.WEEKLY;
		this.autoRemindersWeeklyOccurrence =1;
		this.autoRemindersMonthlyOccurrence =1;
		this.autoRemindersDayOfMonth = 1;
	}
	
	public SurveyDefinition(String emailInvitationTemplate , String completedSurveyTemplate) {
		super();
		this.isPublic=false;
		this.emailInvitationTemplate= emailInvitationTemplate;
		this.completedSurveyTemplate = completedSurveyTemplate;
		// TODO Auto-generated constructor stub

		
		this.sendAutoReminders = false;
		this.autoRemindersFrequency = Frequency.WEEKLY;
		this.autoRemindersWeeklyOccurrence =1;
		this.autoRemindersMonthlyOccurrence =1;
		this.autoRemindersDayOfMonth = 1;
		
		
	}
	

	public SurveyDefinition(Department department , String emailInvitationTemplate , String completedSurveyTemplate) {
		super();
		this.emailInvitationTemplate= emailInvitationTemplate;
		this.completedSurveyTemplate = completedSurveyTemplate;
		this.department = department;
		this.isPublic = false;
		
	
		
		this.sendAutoReminders = false;
		this.autoRemindersFrequency = Frequency.WEEKLY;
		this.autoRemindersWeeklyOccurrence =1;
		this.autoRemindersMonthlyOccurrence =1;
		this.autoRemindersDayOfMonth = 1;
		
		
	}

	public SurveyDefinition(Department department , String name) {
		super();
		this.department = department;
		this.name=name;
		this.isPublic = false;
		
		
		this.sendAutoReminders = false;
		this.autoRemindersFrequency = Frequency.WEEKLY;
		this.autoRemindersWeeklyOccurrence =1;
		this.autoRemindersMonthlyOccurrence =1;
		this.autoRemindersDayOfMonth = 1;
		
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

	public SurveyTheme getSurveyTheme() {
		if  (this.surveyTheme == null) { return SurveyTheme.STANDARD;} else {return this.surveyTheme;}
	}

	public void setSurveyTheme(SurveyTheme surveyTheme) {
		this.surveyTheme = surveyTheme;
	}

	public SurveyDefinitionStatus getStatus() {
		return status;
	}

	public String getStatusAsString() {
		return status.name();
	}
	public void setStatus(SurveyDefinitionStatus status) {
		this.status = status;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
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
	
	public boolean getAllowMultipleSubmissions() {
		return allowMultipleSubmissions;
	}


	public void setAllowMultipleSubmissions(boolean allowMultipleSubmissions) {
		this.allowMultipleSubmissions = allowMultipleSubmissions;
	}

	public SortedSet<SurveyDefinitionPage> getPages() {
		return pages;
	}

	public void setPages(SortedSet<SurveyDefinitionPage> pages) {
		this.pages = pages;
	}
	
	
	public String toString() {
        return this.name;
    }
	
	   
	public boolean getIsPublic() {
			return isPublic;
		}


	public void setIsPublic(boolean isPublic) {
			this.isPublic = isPublic;
		}
	
	
	public String getSurveyDefinitionLongName(){
		return this.name +" (" +this.department.getName() +")";
		}
		
	
	
	
	
	public byte[] getLogo() {
		return logo;
	}


	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	
	public String getEmailInvitationTemplate() {
		return emailInvitationTemplate;
	}


	public void setEmailInvitationTemplate(String emailInvitationTemplate) {
		this.emailInvitationTemplate = emailInvitationTemplate;
	}


	public String getCompletedSurveyTemplate() {
		return completedSurveyTemplate;
	}


	public void setCompletedSurveyTemplate(String completedSurveyTemplate) {
		this.completedSurveyTemplate = completedSurveyTemplate;
	}

	public Boolean getHasLogo() {
		if (this.logo != null && this.logo.length > 0) {return true;} else {return false;} 
	}

		

	public SortedSet<User> getUsers() {
		return users;
	}

	public void setUsers(SortedSet<User> users) {
		this.users = users;
	}

	//Comparable interface
    @Override
	public int compareTo(SurveyDefinition that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Long> thisSurveyDefinition = this.getId();
		Comparable<Long> thatSurveyDefinition = that.getId();
		if(thisSurveyDefinition == null) {
			return AFTER;
		} else if(thatSurveyDefinition == null) {
			return BEFORE;
		} else {
			return thisSurveyDefinition.compareTo(that.getId());
		}
    
    }


	public boolean getSendAutoReminders() {
		return sendAutoReminders;
	}

	public void setSendAutoReminders(boolean sendAutoReminders) {
		this.sendAutoReminders = sendAutoReminders;
	}

	public Frequency getAutoRemindersFrequency() {
		return autoRemindersFrequency;
	}

	public void setAutoRemindersFrequency(Frequency autoRemindersFrequency) {
		this.autoRemindersFrequency = autoRemindersFrequency;
	}

	public Integer getAutoRemindersWeeklyOccurrence() {
		return autoRemindersWeeklyOccurrence;
	}

	public void setAutoRemindersWeeklyOccurrence(
			Integer autoRemindersWeeklyOccurrence) {
		this.autoRemindersWeeklyOccurrence = autoRemindersWeeklyOccurrence;
	}

	public SortedSet<Day> getAutoRemindersDays() {
		return autoRemindersDays;
	}

	public void setAutoRemindersDays(SortedSet<Day> autoRemindersDays) {
		this.autoRemindersDays = autoRemindersDays;
	}

	public Integer getAutoRemindersMonthlyOccurrence() {
		return autoRemindersMonthlyOccurrence;
	}

	public void setAutoRemindersMonthlyOccurrence(
			Integer autoRemindersMonthlyOccurrence) {
		this.autoRemindersMonthlyOccurrence = autoRemindersMonthlyOccurrence;
	}

	public Integer getAutoRemindersDayOfMonth() {
		return autoRemindersDayOfMonth;
	}

	public void setAutoRemindersDayOfMonth(Integer autoRemindersDayOfMonth) {
		this.autoRemindersDayOfMonth = autoRemindersDayOfMonth;
	}

	public Date getAutoReminderLastSentDate() {
		return autoReminderLastSentDate;
	}

	public void setAutoReminderLastSentDate(Date autoReminderLastSentDate) {
		this.autoReminderLastSentDate = autoReminderLastSentDate;
	}

	

    
}
