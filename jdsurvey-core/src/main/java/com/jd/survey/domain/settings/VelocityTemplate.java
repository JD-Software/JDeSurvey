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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;


@Entity
@NamedQueries({
	@NamedQuery(name = "VelocityTemplate.findAll", query = "select o from VelocityTemplate o"),
	@NamedQuery(name = "VelocityTemplate.findById", query = "select o from VelocityTemplate o where o.id = ?1"),
	@NamedQuery(name = "VelocityTemplate.findByName", query = "select o from VelocityTemplate o where o.name = ?1"),
	@NamedQuery(name = "VelocityTemplate.getCount", query = "select count(o) from VelocityTemplate o")	
	})
public class VelocityTemplate implements  Serializable{
	private static final long serialVersionUID = -2522406788452821343L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	@NotBlank
    @Column(name = "TEMPLATE_NAME",length = 250,unique = true, nullable= false)
    @Size(max = 250)
    private String name;

	@Lob 
    @NotNull
    @Size(max = 50000)
    @Column(name = "TEMPLATE_DEFINITION",length = 5000, nullable= false)
    private String definition;

    
    @Column(name = "template_timestamp", nullable= false)
    private Date timestamp;
    
    
    
	public Long getId() {
        return this.id;
    }
	public void setId(Long id) {
        this.id = id;
    }
	public Integer getVersion() {
        return this.version;
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
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String toString() {
        return this.name;
    }
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	
	

}
