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
	protected String property;;
	
	public static GsnProperty parse(String property) {
		GsnProperty p = new GsnProperty();

		// ID: G1 turn into g, Sn13 turns into sn
		// Type set: goal, context, ...
		switch(property.replaceAll("([0-9])", "").toLowerCase()) {
		case "g":
		case "goal":
			p.gsnPropertyType = GsnPropertyType.Goal;
			p.idPrefix = "G";
			p.xsiType = "ARM:Claim";
			break;
		case "s":
		case "stratagy":
			p.gsnPropertyType = GsnPropertyType.Stratagy;
			p.idPrefix = "S";
			p.xsiType = "ARM:ArgumentReasoning";
			break;
		case "sn":
		case "solution":
			p.gsnPropertyType = GsnPropertyType.Solution;
			p.idPrefix = "Sn";
			p.xsiType = "ARM:InformationElement";
			break;
		case "c":
		case "context":
			p.gsnPropertyType = GsnPropertyType.Context;
			p.idPrefix = "C";
			p.xsiType = "ARM:InformationElement";
			break;
		case "a":
		case "assumption":
			p.gsnPropertyType = GsnPropertyType.Assumption;
			p.idPrefix = "A";
			p.xsiType = "ARM:Claim";
			break;
		case "j":
		case "justification":
			p.gsnPropertyType = GsnPropertyType.Justification;
			p.idPrefix = "J";
			p.xsiType = "ARM:Claim";
			break;
		default:
			p = null;
			break;
		}
		
		if (p!=null) {
			p.property = property;//.substring(2);
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
	
}
