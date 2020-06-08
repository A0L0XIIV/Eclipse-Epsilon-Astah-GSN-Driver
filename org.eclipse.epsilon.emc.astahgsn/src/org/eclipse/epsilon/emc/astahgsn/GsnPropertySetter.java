package org.eclipse.epsilon.emc.astahgsn;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;
import org.eclipse.epsilon.eol.types.EolSequence;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GsnPropertySetter extends JavaPropertySetter {
	
	protected GsnModel model;
	
	public GsnPropertySetter(GsnModel model) {
		this.model = model;
	}
	
	@Override
	public void invoke(Object target, String property, Object value, IEolContext context) throws EolRuntimeException {

		System.out.println("GSNPropertySetter - invoke function");
		
		if (target instanceof Element) synchronized (model) {
			Element element = (Element) target;
			
				
			// Set node's content
			if("content".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - content");
				
				element.setAttribute("content", String.valueOf(value) + "");
				return;
			}
			
			// Set link's source
			if("source".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - source");
				
				Element sourceElement = (Element) value;
				// Get value's xmi:id and set it to element's source attribute
				element.setAttribute("source", sourceElement.getAttribute("xmi:id"));
				return;
			}
			
			// Set link's target
			if("target".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - target");
				
				Element targetElement = (Element) value;
				// Get value's xmi:id and set it to element's target attribute
				element.setAttribute("target", targetElement.getAttribute("xmi:id"));
				return;
			}
			
			// Set node's type: goal, solution, evidence, ...
			if ("gsntype".equals(property)) {
				System.out.println("GSNPropertySetter - invoke function - type");
				
				// Take new type as a string and convert it to GsnPropertyType
				GsnProperty g = GsnProperty.parse(String.valueOf(value));
				if(g != null) {
					// Change element's xsi:type attribute according to new type
					element.setAttribute("xsi:type", g.getXsiType());
					// Find new type's highest ID number e.g. G5 and make it new one G6
					String newXmiId = null;
					// Find current highest id number +1 and create new id
					newXmiId = g.getIdPrefix() + (getTypesHighestIdNumber(String.valueOf(value), element.getParentNode()) + 1);
					// Change element's id attribute to new type id
					element.setAttribute("id", newXmiId);
				}
				return;
			}
			
			// Append argumentElement into root element tag
			if("append".equals(property) && value instanceof Element && ((Element) target).hasChildNodes()) {
				Element newElement = (Element) value;
				String newElementId = newElement.getAttribute("id");
				
				// Check new element's id attribute. If it doesn't have any digit, assign a new id number
				if(newElementId != "" && !hasDigit(newElementId)) {
					// Parse id to find element's type
					GsnProperty g = GsnProperty.parse(newElementId);
					// Get element's type highest id number and +1 it
					int newIdNumber = getTypesHighestIdNumber(g.getType().toString(), (Element) target) + 1;
					// Assign new id to the newElement
					newElement.setAttribute("id", newElementId + newIdNumber);
				}
				
			}
			
			return;
			
		}
		super.invoke(target, property, value, context);
	}

	public int getTypesHighestIdNumber(String type, Node root) {
		// If it's root tag, loop over its children and return all attribute values
		if(root.hasChildNodes()) {
			
			NodeList childNodes = root.getChildNodes();
			int maxId = 0;
			int currentId = 0;
			GsnProperty g = GsnProperty.parse(type);
			
			for (int i=0; i<childNodes.getLength(); i++) {
				// Get root's children
				Object obj = childNodes.item(i);
				// Object is the instance of the Element
				if (obj instanceof Element) {
					Element element = (Element) obj;
					// Check id's prefix and get its number value
					if(element.hasAttribute("id") && !element.getAttribute("id").equals("")) {
						// Parse element's id attribute
						GsnProperty gElement = GsnProperty.parse(element.getAttribute("id"));
						// If parsed 'type' and parsed 'element id attribute' are the same
						if(gElement.getType() == g.getType() ) {
							// Get element's id attribute's number, remove non-digit characters and parse it to int
							currentId = Integer.parseInt(element.getAttribute("id").replaceAll("[^\\d.]", ""));
							if(currentId > maxId)
								maxId = currentId;
						}
					}
				}
			}

			return maxId;
		}
		return 0;	
	}
	
	public boolean hasDigit(String input) {
	    for (int i = 0; i < input.length(); ++i) {
	        if (Character.isDigit(input.charAt(i))) {
	        	return true;
	        }
	    }
	    return false;
	}
}
