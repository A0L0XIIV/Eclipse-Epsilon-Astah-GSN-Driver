package org.eclipse.epsilon.emc.astahgsn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;
//import org.jsoup.nodes.Element;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GsnPropertySetter extends JavaPropertySetter {
	
	protected GsnModel model;
	
	public GsnPropertySetter(GsnModel model) {
		this.model = model;
	}
	
	@Override
	public void invoke(Object target, String property, Object value, IEolContext context) throws EolRuntimeException {
		if (target instanceof Element) synchronized (model) {
			Element element = (Element) target;
			
			System.out.println("GSNPropertySetter - invoke function");
				
			// Set node's content
			if("content".equalsIgnoreCase(property)) {
				System.out.println("GSNPropertySetter - invoke function - content");
				
				element.setAttribute("content", value.toString());
				return;
			}
			
			// Set link's source
			if("source".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - source");
				
				Element sourceElement = (Element) value;
				// Get value's xmi:id and set it to element's source attribute
				element.setAttribute("source", sourceElement.getAttribute("xmi:id"));
				return;
			}
			
			// Set link's target
			if("target".equalsIgnoreCase(property) && value instanceof Element) {
				System.out.println("GSNPropertySetter - invoke function - target");
				
				Element targetElement = (Element) value;
				// Get value's xmi:id and set it to element's target attribute
				element.setAttribute("target", targetElement.getAttribute("xmi:id"));
				return;
			}
			
			// Set node's type: goal, solution, evidence, ...
			if ("gsntype".equals(property)) {
				System.out.println("GSNPropertySetter - invoke function - type");
				
				// Take new type as a string and convert it to GsnPropertyType
				// Change element's xsi:type attribute according to new type
				// Change element's id attribute to new type id
				// Find new type's highest ID number e.g. G5 and make it new one G6
			}
			
			return;
			
		}
		super.invoke(target, property, value, context);
	}
	
}
