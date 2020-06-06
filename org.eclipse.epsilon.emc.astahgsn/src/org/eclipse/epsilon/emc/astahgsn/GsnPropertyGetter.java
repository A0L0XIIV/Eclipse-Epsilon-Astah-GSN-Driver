package org.eclipse.epsilon.emc.astahgsn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.eclipse.epsilon.eol.types.EolModelElementType;
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
		
		if (object instanceof Element
			|| object instanceof EolModelElementType
			&& ((EolModelElementType) object).isInstantiable()
			&& ((EolModelElementType) object).getTypeName().equalsIgnoreCase("gsn")) synchronized (model) {
				
			// Get all elements (gsn.all)
			if ("all".equals(property)) {
				System.out.println("GSNPropertyGetter - invoke function - ALL");
				// Invoke JavaPropertyGetter, calls GsnModel's getAllOfTypeFromModel function and fills model
				if(((EolModelElementType) object).getAll().isEmpty()) {
					super.invoke(object, property, context);
					return null;
				}
				// Return root tag. It includes all argumentElement tags as well
				else {
					// Object has both root tag and its children. Get the first element which is root tag
					return ((EolModelElementType) object).getAll().toArray()[0];
				}
			}
			
			// gsn.ID access, object is EolModelElementType not Element nor [argumentElement]
			// In order to cast object to Element, get models first element
			if(object instanceof EolModelElementType
				&& !((EolModelElementType) object).getAll().isEmpty()) {
				System.out.println("GSNPropertyGetter - invoke function - GSN ID");
				// Get first element of the model which is root tag (It includes child tags)
				object = ((EolModelElementType) object).getAll().toArray()[0];
			}
				
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
			
			// Get all link elements
			if ("links".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - links");
				// Link elements' content attributes are empty, could use this while searching
				// However, nodes can have empty content too thus, 3 link xsi:types used in search
			}
			
			// Get all link elements
			if ("nodes".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - nodes");
			}
			
			// Get specific link element by target and source node IDs
			if(property.startsWith("t_")) {
				// Example t_G2_s_S3
				// Parse property string for target and source node IDs
		        String targetId = property.substring(2, property.indexOf("_s_"));
		        String sourceId = property.substring(property.indexOf("_s_") + 3);
		        
		        // Found target and source nodes with parsed Ids
		        Object target = findElementByAttribute(element, "id", targetId);
		        Object source = findElementByAttribute(element, "id", sourceId);
		        
		        // If nodes aren't empty, find link element
		        if(target != null && source != null) {
		        	return findLinkByNodeIDs(element, ((Element) target).getAttribute("xmi:id"), ((Element) source).getAttribute("xmi:id"));
		        }
		        else {
		        	return null;
		        }
			}
			
			
			GsnProperty gsnProperty = GsnProperty.parse(property);
			
			// Get element by id --> G1, Sn14, J4
			// Or by type name --> goal, solution
			if(gsnProperty != null) {
				System.out.println("GSNPropertyGetter - invoke function - ID");
				
				// Call findElementByAttribute with element and use id attribute
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
				Object childNode = childNodes.item(i);
				// Find the given attributeNane in all nodes with loop because NodeList doesn't have find function
				if (childNode instanceof Element) {
					// If childNode is an instance of the Element class, cast it to the Element
					Element e = (Element) childNode;
					// Node class doesn't have getAttribute function thus casted it to Element
					if(e.getAttribute(attributeName).equalsIgnoreCase(attributeValue)) {
						// Add all matches into result list
						result.add(e);
					}
					// For getting one type of elements, such as all goal elements. "goal" doesn't have any digits
					else if(!hasDigit(attributeValue)) {
						// ID (G1, J4) and target-source values (_4VWzZ5CNEeqaz4qJsgFt4g) have digits so they won't get in there
						GsnProperty g = GsnProperty.parse(e.getAttribute(attributeName));
						if(g != null && attributeValue.equalsIgnoreCase(g.getType().toString())) {
							result.add(e);
						}
					}
				}
			}
		}
		// One argumentElement
		else if (node instanceof Element 
				&& ((Element) node).getAttribute(attributeName).equalsIgnoreCase(attributeValue)) {
			//result.add((Element) node);
			return (Element) node;
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
	
	
	public Object findLinkByNodeIDs(Node node, String targetXmiId, String sourceXmiId) {
        
        if(node instanceof Element && node.hasChildNodes()) {
			NodeList childNodes = node.getChildNodes();
			
			// Loop over root's child tags
			for (int i=0; i<childNodes.getLength(); i++) {
				// Get root's child
				Object childNode = childNodes.item(i);
				// Find the given attributeNane in all nodes with loop because NodeList doesn't have find function
				if (childNode instanceof Element) {
					// If childNode is an instance of the Element class, cast it to the Element
					Element e = (Element) childNode;
					// Node class doesn't have getAttribute function thus casted it to Element
					if(e.getAttribute("target").equalsIgnoreCase(targetXmiId)
						&& e.getAttribute("source").equalsIgnoreCase(sourceXmiId)) {
						// Found link element with given target and source xmi IDs
						return e;
					}
				}
			}
		}

		return null;
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
