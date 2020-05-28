package org.eclipse.epsilon.emc.astahgsn;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
			
			GsnProperty g = GsnProperty.parse(property);
			
			if (g == null) return super.invoke(object, property, context);
			
			System.out.println("Element attribute node: " + element.getAttributeNode("id"));
			// Attrbiute --> a_content, a_xmi:id
			if(g.isAttribute()) {
				// If it's main tag, loop over children
				if(element.hasChildNodes()) {
					Node firstChild = element.getFirstChild();
					while(element.getNextSibling() != null) {
						//TO DO 
					}
				}
				else {
					// Get given attribute's value
					String result = element.getAttribute(g.getProperty());
					if(g.getProperty() == "content") {
						// Content attribute has /n characters. Delete them
						result.replace("/n", "");
					}
				}
				return result;
			}
			// Id --> G1, Sn14, J4
			else if(element.getAttribute("id").equalsIgnoreCase(g.getProperty())) {
				return element;
			}
			else {
				return null;
			}
			
		}
		else return super.invoke(object, property, context);
	}

}
