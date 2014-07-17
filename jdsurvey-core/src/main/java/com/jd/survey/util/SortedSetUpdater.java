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
package com.jd.survey.util;

import java.util.Iterator;
import java.util.SortedSet;


public class  SortedSetUpdater <T extends SortedSetUpdater.InrementableCompartator>{
	public interface InrementableCompartator {
		public Long getId();
		public Short getOrder();
		public void setOrder(Short order);
	}

	public T getElement (SortedSet<T> elements, Short order) {
		Iterator<T> it;
		T element = null;
		it = elements.iterator();
		while (it.hasNext()) {
			element= it.next();
			if (element.getOrder().equals(order)) {
				break;
			}
		}
		return element; 
	}
	
	
	public T updateSet (SortedSet<T> elements, T newElement) {
		if (elements.isEmpty()){
			newElement.setOrder((short)1);
			elements.add(newElement);

		}
		else {
			//new element
			if (newElement.getId() == null) {
				newElement = insertElement (elements,newElement);
			}
			else
				//existing element	
			{
				removeElement (elements,newElement.getId());
				removeGaps(elements);
				newElement = insertElement (elements,newElement);
				removeGaps(elements);
			}
		}
		return newElement; 
	}





	private T insertElement (SortedSet<T> elements, T newElement) {
		Iterator<T> it;
		T element;
		
		if (elements.isEmpty()){
			newElement.setOrder((short)1);
			elements.add(newElement);

		}
		else { 
				//element order exists in the set
				if (elements.last().getOrder() >= newElement.getOrder()) {
					it = elements.iterator();
					//increment order for element after target position  
					while (it.hasNext()) {
						element= it.next();
						if (element.getOrder() >= newElement.getOrder()) {
							System.out.println("--->o:" + element.getOrder() +" incremented");
							element.setOrder((short)(element.getOrder() +1));	
						}
					}
					elements.add(newElement);
				}
				//element order outside the set add it to the end
				else {
					newElement.setOrder((short) (elements.last().getOrder() + 1));
					elements.add(newElement);	
				}
		}
		return newElement;
	}


	private void removeElement (SortedSet<T> elements, Long id) {
		T element = null;
		Iterator<T> it = elements.iterator();
		while (it.hasNext()) {
			element = it.next();
			if (element.getId().equals(id)) {
				break; 
			}
		}

		if (element != null) {
			System.out.println("element removed o:" + element.getOrder() +" id" +  element.getId() +" passedid:" + id ); 
			elements.remove(element);
		}

	}


	public void removeGaps (SortedSet<T> elements) {
		Short order = 1;
		Iterator<T> it = elements.iterator();
		while (it.hasNext()) {
			it.next().setOrder(order);
			order++;
		}
	}

}