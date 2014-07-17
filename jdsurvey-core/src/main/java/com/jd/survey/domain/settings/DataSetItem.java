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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;



@Entity
@NamedQueries({
	@NamedQuery(name = "DataSetItem.findAll", query = "select o from DataSetItem o"),
	@NamedQuery(name = "DataSetItem.findById", query = "select o from DataSetItem o where o.id = ?1"),
	@NamedQuery(name = "DataSetItem.getCount", query = "select count(o) from DataSetItem o where o.dataSet.id = ?1"),
	@NamedQuery(name = "DataSetItem.deleteByDataSetId", query = "delete from DataSetItem o where o.dataSet.id=?1"),
	@NamedQuery(name = "DataSetItem.findByDataSetId", query = "select o from DataSetItem o where o.dataSet.id=?1 order by o.order")
	})
public class DataSetItem implements Comparable <DataSetItem>,  Serializable{

	private static final long serialVersionUID = 3431688013681352385L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	@NotNull
	@NotEmpty
    @Size(max = 75)
    @Column(name = "ITEM_VALUE",length = 75, nullable= false)
    private String value;

	
    @NotNull
    @NotEmpty
    @Size(max = 250)
    @Column(name = "ITEM_TEXT",length = 250, nullable= false)
    private String text;

    @NotNull
    @Column(name = "ITEM_ORDER")
    private Integer order;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "DATASET_ID")
    private DataSet dataSet;
    
    
    
    
    
    
	public DataSetItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	
	
	public DataSetItem(DataSet dataSet, Integer order, String value, String text) {
		super();
		this.value = value;
		this.text = text;
		this.order = order;
		this.dataSet = dataSet;
	}









	public DataSetItem(DataSet dataSet) {
		super();
		this.dataSet = dataSet;
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


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Integer getOrder() {
		return order;
	}


	public void setOrder(Integer order) {
		this.order = order;
	}


	


	public DataSet getDataSet() {
		return dataSet;
	}


	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}


	public String toString() {
        return this.text;
    }
	
	
	
	
	//comparable interface
	@Override
	public int compareTo(DataSetItem that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Integer> thisDataSetItem = this.getOrder();
		Comparable<Integer> thatDataSetItem = that.getOrder();
		if(thisDataSetItem == null) {
			return AFTER;
		} else if(thatDataSetItem == null) {
			return BEFORE;
		} else {
			return thisDataSetItem.compareTo(that.getOrder());
		}
	}




    
   
	
}
