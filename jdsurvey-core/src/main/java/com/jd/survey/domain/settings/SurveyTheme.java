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

public enum SurveyTheme {
	
	STANDARD("STANDARD"),
	STEEL_BLUE("STEEL_BLUE"),
	DEEP_CHESTNUT("DEEP_CHESTNUT"),
	SPRING_GREEN("SPRING_GREEN"),
	FRENCH_LILAC("FRENCH_LILAC"),
	CELESTIAL_BLUE("CELESTIAL_BLUE"),
	PEACH_ORANGE("PEACH_ORANGE");
	
	private String code;
	 
	private SurveyTheme(String c) {
	   code = c;
	}
	 
	public String getCode() {
	  return code;
	}
 
	public static SurveyTheme fromCode(String code) {
	    if (code != null) {
	      for (SurveyTheme b : SurveyTheme.values()) {
	        if (code.equalsIgnoreCase(b.code)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
	
	
	
	
}
