package org.eclipse.epsilon.emc.astahgsn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.emc.plainxml.DomUtil;
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
		System.out.println("GSNPropertyGetter - invoke function");
		
		if (object instanceof Element) synchronized (model) {
			Element element = (Element) object;
			
			GsnProperty gsnProperty = GsnProperty.parse(property);
			
			if (gsnProperty == null) return super.invoke(object, property, context);
			
			// Get element's type: goal, solution, evidence, ...
			if ("type".equals(property)) {
				return gsnProperty.getType().toString();
			}
			
			// Get link's target element
			if ("target".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - target");
				// Get element's target value and find it in other tags
				String targetId = element.getAttribute("target");
				
				// Element is child node, call findElementByAttribute with root node in order to find another child
				return findElementByAttribute(element.getParentNode(), "target", targetId);
				/*
				NodeList childNodes = element.getParentNode().getChildNodes();
				
				for (int i=0; i<childNodes.getLength(); i++) {
					// Get root's children
					Object o = childNodes.item(i);
					// Find the given xmi:id in all nodes with loop because NodeList doesn't have find function
					if (o instanceof Element 
						&& ((Element) o).hasAttribute("xmi:id") 
						&& ((Element) o).getAttribute("xmi:id").equalsIgnoreCase(targetId)) {
						return (Element) o;
					}
				}
				return null;*/
				
			}
			
			// Get link's source element
			if ("source".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - source");
				// Get element's source value and find it in other tags
				String sourceId= element.getAttribute("source");

				// Element is child node, call findElementByAttribute with root node in order to find another child
				return findElementByAttribute(element.getParentNode(), "source", sourceId);
				/*
				NodeList childNodes = element.getParentNode().getChildNodes();
				
				for (int i=0; i<childNodes.getLength(); i++) {
					// Get root's children
					Object o = childNodes.item(i);
					// Find the given xmi:id in all nodes with loop because NodeList doesn't have find function
					if (o instanceof Element 
						&& ((Element) o).hasAttribute("xmi:id") 
						&& ((Element) o).getAttribute("xmi:id").equalsIgnoreCase(sourceId)) {
						return (Element) o;
					}
				}
				return null;*/
			}
			
			// Get node's content
			if("content".equalsIgnoreCase(property)) {
				// If it's main tag, loop over children and return all contents
				if(element.hasChildNodes()) {
					System.out.println("GSNPropertyGetter - invoke function - root content");
					
					List<String> attributeValues = new ArrayList<String>();
					//List<Element> result = new ArrayList<>();
					NodeList childNodes = element.getChildNodes();
					for (int i=0; i<childNodes.getLength(); i++) {
						// Get root's children
						Object o = childNodes.item(i);
						if (o instanceof Element 
							&& ((Element) o).hasAttribute("content") 
							&& !((Element) o).getAttribute("content").equals("")) {
							attributeValues.add(((Element) o).getAttribute("content").replace("&#xA;", " "));
						}
					}
					return attributeValues;
				}
				// Only one element's content
				else {
					System.out.println("GSNPropertyGetter - invoke function - element content");
					if(element instanceof Element)
						return element.getAttribute("content").replace("&#xA;", " ");
					else
						return null;
				}
			}
			
			// Get element with id --> G1, Sn14, J4
			if(gsnProperty != null && element.hasChildNodes()) {
				System.out.println("GSNPropertyGetter - invoke function - ID");
				
				// Element is root node, call findElementByAttribute with it
				return findElementByAttribute(element, "id", gsnProperty.getProperty());
				/*
				NodeList childNodes = element.getChildNodes();
				
				for (int i=0; i<childNodes.getLength(); i++) {
					// Get root's children
					Object o = childNodes.item(i);
					// Find the given ID in all nodes with loop because NodeList doesn't have find function
					if (o instanceof Element 
						&& ((Element) o).hasAttribute("id") 
						&& ((Element) o).getAttribute("id").equalsIgnoreCase(g.getProperty())) {
						return (Element) o;
					}
				}
				return null;*/
			}
			
			// None of them
			return null;
			
		}
		else return super.invoke(object, property, context);
	}
	

	public Object findElementByAttribute(Node rootElement, String attributeName, String attributeValue) {
		NodeList childNodes = rootElement.getChildNodes();
		
		// Loop over root's child tags
		for (int i=0; i<childNodes.getLength(); i++) {
			// Get root's child
			Object object = childNodes.item(i);
			// Find the given attributeNane in all nodes with loop because NodeList doesn't have find function
			if (object instanceof Element 
				&& ((Element) object).hasAttribute(attributeName) 
				&& ((Element) object).getAttribute(attributeName).equalsIgnoreCase(attributeValue)) {
				return (Element) object;
			}
		}
		return null;
	}

}
