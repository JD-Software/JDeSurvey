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
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;




@Entity
@Table(name = "sec_password_reset_request")
@NamedQueries({
	@NamedQuery(name = "PasswordResetRequest.findAll", query = "select o from PasswordResetRequest o"),
	@NamedQuery(name = "PasswordResetRequest.findById", query = "select o from PasswordResetRequest o where o.id = ?1"),
	@NamedQuery(name = "PasswordResetRequest.findByHash", query = "select o from PasswordResetRequest o where o.hash = ?1"),
	@NamedQuery(name = "PasswordResetRequest.getCount", query = "select count(o) from User o")
	})
public class PasswordResetRequest implements Serializable {
	
	private static final long serialVersionUID = -6677965533812094354L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	
	@NotEmpty
	@Size(max = 100)
	@Column(length = 100, nullable= false)
	private String login;
	
	
	@Column(nullable= false)
	private Date requestDate;
	
	@Column(nullable= true)
	private Date resetDate;
	
	@Column(length = 500, nullable= false,name = "hashkey")
	private String hash;

	
	
	
	
	
	
	
	public PasswordResetRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PasswordResetRequest(String login,String hash) {
		super();
		this.login = login;
		this.requestDate = new Date();
		this.hash = hash;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}
				
	
	





}
