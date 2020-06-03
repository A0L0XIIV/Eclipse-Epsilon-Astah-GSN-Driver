package org.eclipse.epsilon.emc.astahgsn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GsnPropertyGetter extends JavaPropertyGetter {
	
	protected GsnModel model;
	
	public GsnPropertyGetter(GsnModel model) {
		this.model = model;
	}

	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
		
		if (object instanceof Element) synchronized (model) {
			Element element = (Element) object;
						
			// Get element's type: goal, solution, evidence, ...
			if ("gsntype".equals(property)) {
				System.out.println("GSNPropertyGetter - invoke function - type");
				// Get element's gsn property type
				GsnProperty g = GsnProperty.parse(element.getAttribute("id"));
				// Return it's type
				if(g != null)
					return g.getType().toString();
				else
					return null;
			}
			
			// Get link's target element
			if ("target".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - target");
				// Get element's target value and find it in other tags
				String targetId = element.getAttribute("xmi:id");
				
				// Element is child node, call findElementByAttribute with root node in order to find another child
				return findElementByAttribute(element.getParentNode(), "target", targetId);				
			}
			
			// Get link's source element
			if ("source".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - source");
				// Get element's source value and find it in other tags
				String sourceId= element.getAttribute("xmi:id");

				// Element is child node, call findElementByAttribute with root node in order to find another child
				return findElementByAttribute(element.getParentNode(), "source", sourceId);
			}
			
			// Get node's content
			if("content".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "content");
			}
			
			// Get node's links
			if("id".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "id");
			}
			
			GsnProperty gsnProperty = GsnProperty.parse(property);
			
			// Get element with id --> G1, Sn14, J4
			if(gsnProperty != null) {
				System.out.println("GSNPropertyGetter - invoke function - ID");
				
				// Call findElementByAttribute with element
				return findElementByAttribute(element, "id", gsnProperty.getProperty());
			}
			

			System.out.println("GSNPropertyGetter - invoke function - NONEOFTHEM");
			// Not source, target, content or ID
			return null;
			
		}
		else return super.invoke(object, property, context);
	}
	

	public Object findElementByAttribute(Node node, String attributeName, String attributeValue) {
		List<Element> result = new ArrayList<Element>();
		
		// Root element and it's children
		if(node instanceof Element && node.hasChildNodes()) {
			NodeList childNodes = node.getChildNodes();
			
			// Loop over root's child tags
			for (int i=0; i<childNodes.getLength(); i++) {
				// Get root's child
				Object object = childNodes.item(i);
				// Find the given attributeNane in all nodes with loop because NodeList doesn't have find function
				if (object instanceof Element) {
					Element e = (Element) object;
					if(e.getAttribute(attributeName).equalsIgnoreCase(attributeValue)) {
						
						result.add(e);
					}
				}
			}
		}
		// One argumentElement
		else if (node instanceof Element 
				&& ((Element) node).getAttribute(attributeName).equalsIgnoreCase(attributeValue)) {
					result.add((Element) node);
		}
		else {
			return null;
		}
	
		// Empty list
		if(result.isEmpty())
			return null;
		// Only one element, (e.g. id)
		else if(result.size() == 1)
			return result.get(0);
		// Return as a list
		else
			return result;
	}
	
	public Object getElementAttribute(Node node, String attributeName) {
		// If it's root tag, loop over its children and return all attribute values
		if(node.hasChildNodes()) {
			
			List<String> attributeValues = new ArrayList<String>();
			//List<Element> result = new ArrayList<>();
			NodeList childNodes = node.getChildNodes();
			for (int i=0; i<childNodes.getLength(); i++) {
				// Get root's children
				Object o = childNodes.item(i);
				if (o instanceof Element 
					&& !((Element) o).getAttribute(attributeName).equals("")) {
					attributeValues.add(((Element) o).getAttribute(attributeName));
				}
			}

			// Empty list
			if(attributeValues.isEmpty())
				return null;
			// Only one element, (e.g. id)
			else if(attributeValues.size() == 1)
				return attributeValues.get(0);
			// Return as a list
			else
				return attributeValues;
		}
		
		// Only one element's content
		else {
			if(node instanceof Element)
				return ((Element) node).getAttribute(attributeName);
			else
				return null;
		}
	}

}
