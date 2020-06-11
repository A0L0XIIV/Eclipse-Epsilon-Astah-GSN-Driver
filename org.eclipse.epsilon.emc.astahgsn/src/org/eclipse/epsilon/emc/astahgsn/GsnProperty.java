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

public class GsnProperty {
	
	protected GsnPropertyType gsnPropertyType;
	protected String idPrefix;
	protected String xsiType;
	protected String property;
	protected boolean isNode = false;
	protected boolean isLink = false;
	protected boolean isRoot = false;
	
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
			p.gsnPropertyType = GsnPropertyType.Inference;
			p.idPrefix = "";
			p.xsiType = "ARM:AssertedInference";
			p.isLink = true;
			break;
		case "evidence":
		case "assertedevidence":
			p.gsnPropertyType = GsnPropertyType.Evidence;
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
	
}
