package org.eclipse.epsilon.emc.astahgsn;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
import org.w3c.dom.Element;

public class GsnPropertyGetter extends JavaPropertyGetter {
	
	protected GsnModel model;
	
	public GsnPropertyGetter(GsnModel model) {
		this.model = model;
	}

	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
		System.out.println("GSNPropertyGetter - invoke function");
		
		Element element = (Element) object;
		
		//ME
		//a_id_G1
		/*if (property.contains("_id_")) {
			//property = property.replace("id_", "");
			return element.getElementById(property.substring(5)).attr("content");
		}*/
		//ME
		
		//PlainXmlProperty p = PlainXmlProperty.parse(property);
		GsnProperty g = GsnProperty.parse(property);
		
		if (g == null) return super.invoke(object, property, context);
		
		System.out.println("Element: " + element);
		
		return element.getElementById(property).attr("content");
		
		/*if (p.isAttribute()) {
			return p.cast(element.attr(p.getProperty()));
		}
		else {
			Elements elements = element.getElementsByTag(p.getProperty());
			if (p.isMany()) {
				return elements;
			}
			else {
				if (elements.size() > 0) return elements.get(0);
				else return null;
			}
		}*/
	}

}
