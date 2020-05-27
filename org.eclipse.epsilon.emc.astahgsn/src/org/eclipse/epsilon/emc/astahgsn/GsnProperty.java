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
	
	protected GsnPropertyType type;
	protected String property;
	
	public static GsnProperty parse(String property) {
		GsnProperty p = new GsnProperty();
		
		if (property.startsWith("G")) {
			
			p.type = GsnPropertyType.Goal;
			
		} else if (property.startsWith("S")) {
			
			p.type = GsnPropertyType.Stratagy;
			
		} else if (property.startsWith("Sn")) {
			
			p.type = GsnPropertyType.Solution;
		
		} else if (property.startsWith("C")) {
			
			p.type = GsnPropertyType.Context;
			
		} else if (property.startsWith("A")) {
			
			p.type = GsnPropertyType.Assumption;
			
		} else if (property.startsWith("J")) {
			
			p.type = GsnPropertyType.Justification;
			
		} else {
			p = null;
		}
		
		if (p!=null) {
			p.property = property;//property.substring(2);
		}
		
		return p;
	}
	
	/*public Object cast(String value) {
		value = value.trim();
		
		if (dataType == GsnPropertyDataType.BOOLEAN) {
			return Boolean.parseBoolean(value);
		}
		else if (dataType == GsnPropertyDataType.INTEGER) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException ex) {
				return 0;
			}
		}
		else if (dataType == GsnPropertyDataType.FLOAT) {
			try {
				return Float.parseFloat(value);
			}
			catch (NumberFormatException ex) {
				return 0.0f;
			}
		}
		else if (dataType == GsnPropertyDataType.DOUBLE) {
			try {
				return Double.parseDouble(value);
			}
			catch (NumberFormatException ex) {
				return 0.0d;
			}
		}
		else {
			return value;
		}
	
	}
	
	private GsnPropertyDataType dataTypeFor(String letter) {
		if (letter.equals("b")) {
			return GsnPropertyDataType.BOOLEAN;
		}
		else if (letter.equals("f")) {
			return GsnPropertyDataType.FLOAT;
		}
		else if (letter.equals("d")) {
			return GsnPropertyDataType.DOUBLE;
		}
		else if (letter.equals("i")){
			return GsnPropertyDataType.INTEGER;
		}
		else {
			return GsnPropertyDataType.STRING;
		}
	}*/
	
	public String getProperty() {
		return property;
	}
	
	public GsnPropertyType getType() {
		return type;
	}

	/*public boolean isAttribute() {
		return type == GsnPropertyType.Attribute;
	}

	public boolean isElement() {
		return type == GsnPropertyType.Element;
	}
	
	public boolean isText() {
		return text;
	}
	
	public boolean isMany() {
		return many;
	}

	public boolean isReference() {
		return type == GsnPropertyType.Reference;
	}*/
	
}
