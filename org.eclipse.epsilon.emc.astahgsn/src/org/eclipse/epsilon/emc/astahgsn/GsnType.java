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

public class GsnType {
	
	protected String gsnType = null;
	
	public String getGsnType() {
		return gsnType;
	}

	
	// User input n_goal, n_solution, ...
	// Convert these to xsi:type format such as ARM:Claim
	public static GsnType parse(String type) {
		GsnType g = null;
		g = new GsnType();
		
		// Nodes
		if (type.startsWith("n_")) {
			switch(type.substring(2).toLowerCase()) {
				case "goal":
				case "assumption":
				case "justification":
					g.gsnType = "ARM:Claim";
					break;
				case "strategy":
					g.gsnType = "ARM:ArgumentReasoning";
					break;
				case "solution":
				case "context":
					g.gsnType = "ARM:InformationElement";
					break;
				default:
					g.gsnType = null;
			}
		}
		
		// Links
		else if (type.startsWith("l_")) {
			switch(type.substring(2).toLowerCase()) {
				case "context":
					g.gsnType = "ARM:AssertedContext";
					break;
				case "inference":
					g.gsnType = "ARM:AssertedInference";
					break;
				case "evidence":
					g.gsnType = "ARM:AssertedEvidence";
					break;
				default:
					g.gsnType = null;
			}
		}
		return g;
	}
	
}