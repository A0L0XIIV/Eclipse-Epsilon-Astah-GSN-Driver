package org.eclipse.epsilon.emc.astahgsn;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;
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
			// Cast target object to element type
			Element element = (Element) target;
			
				
			// Set node's content
			if("content".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - content");
				
				element.setAttribute("content", String.valueOf(value) + "");
				return;
			}
			
			// Set element's id
			if("id".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - id");
				
				element.setAttribute("id", String.valueOf(value) + "");
				return;
			}
			
			// Set element's xmi:id
			if("xmiid".equalsIgnoreCase(property) || "xmi_id".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - xmi:id");
				
				element.setAttribute("xmi:id", String.valueOf(value) + "");
				return;
			}
			
			// Set element's xsi:type
			if("xsitype".equalsIgnoreCase(property) || "xsi_type".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - xsi:type");
				
				element.setAttribute("xsi:type", String.valueOf(value) + "");
				return;
			}
			
			// Set link's source
			if("source".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - source");
				
				Element sourceElement = (Element) value;
				// Get value's xmi:id and set it to element's TARGET* attribute
				/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
				* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
				* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
				* .target sets element's 'source' attribute and .source sets element's 'target' attribute for easy understanding
				*/
				element.setAttribute("target", sourceElement.getAttribute("xmi:id"));
				return;
			}
			
			// Set link's target
			if("target".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - target");
				
				Element targetElement = (Element) value;
				// Get value's xmi:id and set it to element's SOURCE* attribute
				/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
				* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
				* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
				* .target sets element's 'source' attribute and .source sets element's 'target' attribute for easy understanding
				*/
				element.setAttribute("source", targetElement.getAttribute("xmi:id"));
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
				// Parse id to find element's type
				GsnProperty g = GsnProperty.parse(newElementId);
				
				// Check new element's id attribute. If it doesn't have any digit, assign a new id number
				if(g != null && !hasDigit(newElementId)) {
					// Get element's type highest id number and +1 it
					int newIdNumber = getTypesHighestIdNumber(g.getType().toString(), (Element) target) + 1;
					// Assign new id to the newElement
					newElement.setAttribute("id", newElementId + newIdNumber);
				}
				
				// Append new element into Root element
				((Element) target).appendChild(newElement);
				
				return;
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
				Object childNode = childNodes.item(i);
				// Object is the instance of the Element
				if (childNode instanceof Element) {
					Element childElement = (Element) childNode;
					String elementId = childElement.getAttribute("id");
					// Check id's prefix and get its number value
					if(childElement.hasAttribute("id") && !elementId.equals("")) {
						// Parse element's id attribute
						GsnProperty gElement = GsnProperty.parse(elementId);
						// If parsed 'type' and parsed 'element id attribute' are the same
						if(gElement.getType() == g.getType() ) {
							// Get element's id attribute's number, remove non-digit characters and parse it to int
							currentId = Integer.parseInt(elementId.replaceAll("[^\\d.]", ""));
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
