/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.astahgsn;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GsnProperty {
	
	protected GsnPropertyType gsnPropertyType;
	protected String idPrefix;
	protected String xsiType;
	protected String property;
	protected boolean isNode = false;
	protected boolean isLink = false;
	protected boolean isRoot = false;
	
	// Parse given string: ID or type and find its GSN type
	public static GsnProperty parse(String property) {
		GsnProperty p = new GsnProperty();

		// ID: Remove digits & lower case --> G1 turn into g, Sn13 turns into sn
		// Type: lower case --> goal, context, ...
		switch(property.replaceAll("([0-9])", "").toLowerCase()) {
		// Root tag element
		case "gsn":
			p.isRoot = true;
			break;
		// Nodes
		case "g":
		case "goal":
			p.gsnPropertyType = GsnPropertyType.Goal;
			p.idPrefix = "G";
			p.xsiType = "ARM:Claim";
			p.isNode = true;
			break;
		case "s":
		case "stratagy":
			p.gsnPropertyType = GsnPropertyType.Stratagy;
			p.idPrefix = "S";
			p.xsiType = "ARM:ArgumentReasoning";
			p.isNode = true;
			break;
		case "sn":
		case "solution":
			p.gsnPropertyType = GsnPropertyType.Solution;
			p.idPrefix = "Sn";
			p.xsiType = "ARM:InformationElement";
			p.isNode = true;
			break;
		case "c":
		case "context":
			p.gsnPropertyType = GsnPropertyType.Context;
			p.idPrefix = "C";
			p.xsiType = "ARM:InformationElement";
			p.isNode = true;
			break;
		case "a":
		case "assumption":
			p.gsnPropertyType = GsnPropertyType.Assumption;
			p.idPrefix = "A";
			p.xsiType = "ARM:Claim";
			p.isNode = true;
			break;
		case "j":
		case "justification":
			p.gsnPropertyType = GsnPropertyType.Justification;
			p.idPrefix = "J";
			p.xsiType = "ARM:Claim";
			p.isNode = true;
			break;
		// Links
		case "inference":
		case "assertedinference":
			p.gsnPropertyType = GsnPropertyType.AssertedInference;
			p.idPrefix = "";
			p.xsiType = "ARM:AssertedInference";
			p.isLink = true;
			break;
		case "evidence":
		case "assertedevidence":
			p.gsnPropertyType = GsnPropertyType.AssertedEvidence;
			p.idPrefix = "";
			p.xsiType = "ARM:AssertedEvidence";
			p.isLink = true;
			break;
		case "assertedcontext":
			p.gsnPropertyType = GsnPropertyType.AssertedContext;
			p.idPrefix = "";
			p.xsiType = "ARM:AssertedContext";
			p.isLink = true;
			break;
		default:
			p = null;
			break;
		}
		
		if (p!=null) {
			p.property = property;
		}
		
		return p;
	}
	
	// Parse given element and find its GSN type
	public static GsnProperty parseElement(Element element) {
		GsnProperty p = new GsnProperty();
		
		// Assign xsiType to p object
		p.xsiType = element.getAttribute("xsi:type");
		
		switch(p.xsiType) {
		// NODE: Goal, assumption or justification
		case "ARM:Claim":
			p.isNode = true;
			// Assumption has assumed="true" attribute
			if(element.getAttribute("assumed").equalsIgnoreCase("true")) {
				p.gsnPropertyType = GsnPropertyType.Assumption;
				p.idPrefix = "A";
			}
			// Element is justification or goal
			else if(p.isJustificationOrContext(element)) {
				p.gsnPropertyType = GsnPropertyType.Justification;
				p.idPrefix = "J";
			}
			else {
				p.gsnPropertyType = GsnPropertyType.Goal;
				p.idPrefix = "G";
			}
			break;
		// NODE: Strategy
		case "ARM:ArgumentReasoning":
			p.gsnPropertyType = GsnPropertyType.Stratagy;
			p.idPrefix = "S";
			p.isNode = true;
			break;
		// NODE: Solution or context
		case "ARM:InformationElement":
			p.isNode = true;
			if(p.isJustificationOrContext(element)) {
				p.gsnPropertyType = GsnPropertyType.Context;
				p.idPrefix = "C";
			}
			else {
				p.gsnPropertyType = GsnPropertyType.Solution;
				p.idPrefix = "Sn";
			}
			break;
		// LINK: G-G, G-S, S-G
		case "ARM:AssertedInference":
			p.gsnPropertyType = GsnPropertyType.AssertedInference;
			p.idPrefix = "";
			p.isLink = true;
			break;
		// LINK: G-Sn
		case "ARM:AssertedEvidence":
			p.gsnPropertyType = GsnPropertyType.AssertedEvidence;
			p.idPrefix = "";
			p.isLink = true;
			break;
		// LINK: G-C, G-A, G-J, S-C, S-A, S-J
		case "ARM:AssertedContext":
			p.gsnPropertyType = GsnPropertyType.AssertedContext;
			p.idPrefix = "";
			p.isLink = true;
			break;
		default:
			p = null;
			break;
		}
		
		return p;
	}
	
	
	public String getProperty() {
		return property;
	}
	
	public GsnPropertyType getType() {
		return gsnPropertyType;
	}
	
	public String getIdPrefix() {
		return idPrefix;
	}
	
	public String getXsiType() {
		return xsiType;
	}
	
	public boolean isNode() {
		return isNode;
	}
	
	public boolean isLink() {
		return isLink;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	
	// Goal or Justification  AND  Solution or Context  FINDER
	public boolean isJustificationOrContext(Element element) {
		/* Goal-Justification and Solution-Context pair nodes have the same attributes.
		 * You cannot distinguish pairs by attributes. Only difference between these 
		 * 2 pairs are their links. If element connected to AssertedContext link's target attribute 
		 * (In this case source attribute because in XMI link elements' source-target attributes reversed)
		 * that means the element is Justification or Context. 
		 * Search AssertedContext link elements and if one of them has given element's xmi:id in 
		 * the target (*Source: reversed) attribute then return true.
		 * */
		// Get element's xmi:id
		String elementXmiId = element.getAttribute("xmi:id");
		// Get element's parent node (root) and then its child nodes (all elements)
		NodeList childNodes = element.getParentNode().getChildNodes();
		
		// Loop over root's child tags
		for (int i=0; i<childNodes.getLength(); i++) {
			// Get root's child
			Object childNode = childNodes.item(i);
			// Find the given attributeName in all nodes with loop because NodeList doesn't have find function
			if (childNode instanceof Element) {
				// If childNode is an instance of the Element class, cast it to the Element because Node class doesn't have getAttribute function
				Element e = (Element) childNode;
				// If e is a AssertedContext element and its source attribute has element's xmi:id
				if(e.getAttribute("xsi:type").equals("ARM:AssertedContext")
					&& e.getAttribute("source").equalsIgnoreCase(elementXmiId)) {
					// Found element's xmi:id, it's justification, return true
					return true;
				}
			}
		}
		return false;
	}
	
}
