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
import java.util.Map;
import java.util.TreeMap;


public class PageLogic implements Serializable{
	private static final long serialVersionUID = -2551152960729569977L;

	private Short jumpToPageOrder;
	private GroupingOperator groupingOperator =GroupingOperator.AND ;
	private Map<String,LogicalCondition> logicalConditions = new TreeMap<String,LogicalCondition>();
	public PageLogic() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Short getJumpToPageOrder() {
		return jumpToPageOrder;
	}
	public void setJumpToPageOrder(Short jumpToPageOrder) {
		this.jumpToPageOrder = jumpToPageOrder;
	}
	public GroupingOperator getGroupingOperator() {
		return groupingOperator;
	}
	public void setGroupingOperator(GroupingOperator groupingOperator) {
		this.groupingOperator = groupingOperator;
	}
	public Map<String, LogicalCondition> getLogicalConditions() {
		return logicalConditions;
	}
	public void setLogicalConditions(Map<String, LogicalCondition> logicalConditions) {
		this.logicalConditions = logicalConditions;
	}
}
