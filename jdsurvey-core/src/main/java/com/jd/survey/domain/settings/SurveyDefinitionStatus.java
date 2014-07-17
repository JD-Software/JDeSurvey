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

//I: Incomplete--> Allow edits to question definitions etc..
//P: Published--> Survey was published do not allow edits to question definitions but allow participants to fill surveys.
//D: Dissactived--> Survey dissactivated do not allow edits to question definitions and do allow participants to fill surveys. User may reactivate the survey and change the state to Published.  

public enum SurveyDefinitionStatus {
	I, P, D;
	
	public String getStringValue() {
	    return this.name();
	}
}
