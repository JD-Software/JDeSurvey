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
package com.jd.survey.domain.survey;

import java.text.DecimalFormat;
import java.util.Date;

public class QuestionStatistic {
	private  String entry;
	private  Long count = (long) 0;

	private  Short columnOrder;
	private  Short rowOrder;
	private Short optionOrder;

	private  double min;
	private  double max;

	private  Date minDate;
	private  Date maxDate;
	private  double average;
	private  double sampleStandardDeviation;
	private long totalCount;

	public QuestionStatistic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionStatistic(String entry, Long count) {
		super();
		this.entry = entry;
		this.count = count;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		if (count == null) {this.count = (long) 0;} else {this.count = count;}
		
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getSampleStandardDeviation() {
		return sampleStandardDeviation;
	}

	public void setSampleStandardDeviation(double sampleStandardDeviation) {
		this.sampleStandardDeviation = sampleStandardDeviation;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}


	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}



	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public Short getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(Short columnOrder) {
		this.columnOrder = columnOrder;
	}

	public Short getRowOrder() {
		return rowOrder;
	}

	public void setRowOrder(Short rowOrder) {
		this.rowOrder = rowOrder;
	}

	
	

	
	public Short getOptionOrder() {
		return optionOrder;
	}

	public void setOptionOrder(Short optionOrder) {
		this.optionOrder = optionOrder;
	}

	public double getFrequency() {
		double percentage = 0;
		if (totalCount != 0) {percentage = ((double)count/(double)totalCount);}
		return Double.valueOf(new DecimalFormat("#.##").format(percentage));

	}



}
