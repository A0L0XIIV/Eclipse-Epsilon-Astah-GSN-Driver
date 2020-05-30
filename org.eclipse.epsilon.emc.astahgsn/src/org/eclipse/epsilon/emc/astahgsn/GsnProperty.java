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
	protected String property;;
	
	public static GsnProperty parse(String property) {
		GsnProperty p = new GsnProperty();

		//G1 turn into g, Sn13 turns into sn
		switch(property.replaceAll("([0-9])", "").toLowerCase()) {
		case "g":
			p.gsnPropertyType = GsnPropertyType.Goal;
			break;
		case "s":
			p.gsnPropertyType = GsnPropertyType.Stratagy;
			break;
		case "sn":
			p.gsnPropertyType = GsnPropertyType.Solution;
			break;
		case "c":
			p.gsnPropertyType = GsnPropertyType.Context;
			break;
		case "a":
			p.gsnPropertyType = GsnPropertyType.Assumption;
			break;
		case "j":
			p.gsnPropertyType = GsnPropertyType.Justification;
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
	
}
