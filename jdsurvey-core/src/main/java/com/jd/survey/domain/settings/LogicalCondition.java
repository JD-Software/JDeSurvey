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
import java.math.BigDecimal;
import java.util.Date;

import java.util.List;




public class LogicalCondition implements Serializable{
	private static final long serialVersionUID = -7512505021440759469L;

	private boolean enabled;
	private LogicOperator logicOperator;
	private String stringValue;
	private List<String> stringValues;
	private List<Integer> integerValues;
	private Boolean booleanValue;
	
	private Date dateValue;
	private Date dateMin;
	private Date dateMax;
	
	private Long longValue;
	private Long longMin;
	private Long longMax;
	
	private BigDecimal bigDecimalValue;
	private BigDecimal bigDecimalMin;
	private BigDecimal bigDecimalMax;
	
	public LogicalCondition() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public LogicOperator getLogicOperator() {
		return logicOperator;
	}
	public void setLogicOperator(LogicOperator logicOperator) {
		this.logicOperator = logicOperator;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public List<String> getStringValues() {
		return stringValues;
	}
	public void setStringValues(List<String> stringValues) {
		this.stringValues = stringValues;
	}
	public Boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}
	public Date getDateMin() {
		return dateMin;
	}
	public void setDateMin(Date dateMin) {
		this.dateMin = dateMin;
	}
	public Date getDateMax() {
		return dateMax;
	}
	public void setDateMax(Date dateMax) {
		this.dateMax = dateMax;
	}
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
	public Long getLongMin() {
		return longMin;
	}
	public void setLongMin(Long longMin) {
		this.longMin = longMin;
	}
	public Long getLongMax() {
		return longMax;
	}
	public void setLongMax(Long longMax) {
		this.longMax = longMax;
	}
	public BigDecimal getBigDecimalValue() {
		return bigDecimalValue;
	}
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}
	public BigDecimal getBigDecimalMin() {
		return bigDecimalMin;
	}
	public void setBigDecimalMin(BigDecimal bigDecimalMin) {
		this.bigDecimalMin = bigDecimalMin;
	}
	public BigDecimal getBigDecimalMax() {
		return bigDecimalMax;
	}
	public void setBigDecimalMax(BigDecimal bigDecimalMax) {
		this.bigDecimalMax = bigDecimalMax;
	}







	public List<Integer> getIntegerValues() {
		return integerValues;
	}
	public void setIntegerValues(List<Integer> integerValues) {
		this.integerValues = integerValues;
	}
	
	
	
	
	
	
	}
