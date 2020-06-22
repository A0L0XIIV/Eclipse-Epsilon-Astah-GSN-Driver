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
			&& ((EolModelElementType) object).getTypeName().equalsIgnoreCase("gsn")
			|| object instanceof ArrayList) synchronized (model) {
				
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
			
			// gsn.goal.last or gsn.solution.first return ArrayList because, they are subset of the Root Element 
			// However gsn.all.last doesn't return ArrayList, it returns Root Tag element
			if(object instanceof ArrayList
				&& ((ArrayList<?>) object).get(0) instanceof Element) {
				ArrayList<Element> list = (ArrayList<Element>) object;
				if("last".equals(property)) {
					return list.get(list.size() - 1);
				}
				else if("first".equals(property)) {
					return list.get(0);
				}
			}
				
			Element element = (Element) object;
			
						
			// Get element's type: goal, solution, evidence, ...
			if ("gsntype".equals(property)) {
				System.out.println("GSNPropertyGetter - invoke function - type");
				// Get element's id
				String elementId = element.getAttribute("id");
				// ID isn't empty ==> node element, returns its type
				if(elementId != "") {
					// Get element's gsn property type
					GsnProperty g = GsnProperty.parse(elementId);
					// Return it's type
					if(g != null)
						return g.getType().toString();
					else
						return null;
				}
				// ID is empty ==> link element, find type by xsi:type
				else{
					// Get link's xsi:type, split it from ':' and get second part
					String elementXsiType = element.getAttribute("xsi:type").split(":")[1];
					// Get element's gsn property type
					GsnProperty g = GsnProperty.parse(elementXsiType);
					// Return it's type
					if(g != null)
						return g.getType().toString();
					else
						return null;
				}
			}
			
			// Get link's target element
			if ("target".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - target");
				
				// Element is node or link, if element has target attribute = it's a link
				if(element.hasAttribute("target")) {
					// Get link element's SOURCE* value and find the node element with xmi:id = targetId 
					/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
					* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
					* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
					* .target gets element's 'source' attribute and .source gets element's 'target' attribute for easy understanding
					*/
					String targetId = element.getAttribute("source");

					// Element is child node, call findElementByAttribute with root node in order to find another child
					return findElementByAttribute(element.getParentNode(), "xmi:id", targetId);	
				}
				// Element is a node
				else {
					// Get node element's xmi:id value and find all link elements that targets the given node element
					String targetId = element.getAttribute("xmi:id");
					
					// findElementByAttribute with root node returns all link elements with SOURCE* = targetId
					/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
					* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
					* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
					* .target gets element's 'source' attribute and .source gets element's 'target' attribute for easy understanding
					*/
					return findElementByAttribute(element.getParentNode(), "source", targetId);		
				}		
			}
			
			// Get link's source element
			if ("source".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - source");
				
				// Element is node or link, if element has source attribute = it's a link
				if(element.hasAttribute("source")) {	
					// Get element's TARGET* value and find the node element with xmi:id = sourceId
					/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
					* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
					* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
					* .target gets element's 'source' attribute and .source gets element's 'target' attribute for easy understanding
					*/
					String sourceId= element.getAttribute("target");

					// Element is child node, call findElementByAttribute with root node in order to find another child
					return findElementByAttribute(element.getParentNode(), "xmi:id", sourceId);
				}
				// Element is a node
				else {	
					// Get node element's xmi:id value and find all link elements that starts from the given node element
					String sourceId= element.getAttribute("xmi:id");

					// findElementByAttribute with root node returns all link elements with TARGET* = sourceId
					/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
					* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
					* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
					* .target gets element's 'source' attribute and .source gets element's 'target' attribute for easy understanding
					*/
					return findElementByAttribute(element.getParentNode(), "target", sourceId);	
				}
			}
			
			// Get node's content
			if("content".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "content");
			}
			
			// Get element's id
			if("id".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "id");
			}
			
			// Get element's xmi:id
			if("xmiid".equalsIgnoreCase(property) || "xmi_id".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "xmi:id");
			}
			
			// Get element's xsi:type
			if("xsitype".equalsIgnoreCase(property) || "xsi_type".equalsIgnoreCase(property)) {
				return getElementAttribute(element, "xsi:type");
			}
			
			// Get all link elements
			if ("links".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - links");
				// Link elements' content attributes are empty, could use this while searching
				// However, nodes can have empty content too thus, id attribute used in search
				// Links' id attribute is empty, find elements with empty id attribute and return them
				return findElementByAttribute(element, "id", "");
			}
			
			// Get all node elements
			if ("nodes".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertyGetter - invoke function - nodes");
				// Nodes have non-empty id attributes, find and return them
				List<Element> result = new ArrayList<Element>();
				
				// Root element and it's children
				if(element.hasChildNodes()) {
					NodeList childNodes = element.getChildNodes();
					
					// Loop over root's child tags
					for (int i=0; i<childNodes.getLength(); i++) {
						// Get root's child
						Object childNode = childNodes.item(i);
						// Find the given attributeNane in all nodes with loop because NodeList doesn't have find function
						if (childNode instanceof Element) {
							// If childNode is an instance of the Element class, cast it to the Element
							Element e = (Element) childNode;
							// Node class doesn't have getAttribute function thus casted it to Element
							if(!e.getAttribute("id").equalsIgnoreCase("")) {
								// Add all matches into result list
								result.add(e);
							}
						}
					}
					return result;
				}
				else {
					return null;
				}
			}
			
			// Get specific link element by target and source node IDs
			if(property.startsWith("t_")) {
				// Example t_G2_s_G3
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
			
			// Get specific link element by target and source node IDs V2
			if(property.startsWith("s_")) {
				// Example s_G2_t_S3
				// Parse property string for target and source node IDs
		        String sourceId = property.substring(2, property.indexOf("_t_"));
		        String targetId = property.substring(property.indexOf("_t_") + 3);
		        
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
			
			
			// Get last element, useful for new elements
			if("last".equalsIgnoreCase(property) && element.hasChildNodes()) {
				Node lastChild = element.getLastChild();
				// If the last child is NOT an Element instance, get the previous one
				while(! (lastChild instanceof Element)) {
					lastChild = lastChild.getPreviousSibling();
				}
				return lastChild;
			}
			
			// Get first element
			if("first".equalsIgnoreCase(property) && element.hasChildNodes()) {
				Node firstChild = element.getFirstChild();
				// If the first child is NOT an Element instance, get the next one
				while(! (firstChild instanceof Element)) {
					firstChild = firstChild.getNextSibling();
				}
				return firstChild;
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
				// If childNode is an instance of the Element class, cast it to the Element
				if (childNode instanceof Element) {
					// Node class doesn't have getAttribute function thus casted it to Element
					Element e = (Element) childNode;
					// If link element's SOURCE* attribute equals to targetXmiId and TARGET* equals to sourceXmiId
					/* WARNING: Astah-GSN has reversed source/target attributes for link elements in XMI file.
					* That means link's source attribute stores the targeted (the end of the arrow) node element's xmi:id
					* same for link's target attribute stores the sourced (the beginning of the arrow) node element's xmi:id
					* That's why target and source attribute getters are reversed in the code below (for better usability)
					*/
					if(e.getAttribute("source").equalsIgnoreCase(targetXmiId)
						&& e.getAttribute("target").equalsIgnoreCase(sourceXmiId)) {
						// Found link element with given target and source xmi:IDs
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
