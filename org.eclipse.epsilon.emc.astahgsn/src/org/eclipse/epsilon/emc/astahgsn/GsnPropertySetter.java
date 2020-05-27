package org.eclipse.epsilon.emc.astahgsn;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;
import org.jsoup.nodes.Element;

public class GsnPropertySetter extends JavaPropertySetter {
	
	protected GsnModel model;
	
	public GsnPropertySetter(GsnModel model) {
		this.model = model;
	}
	
	@Override
	public void invoke(Object target, String property, Object value, IEolContext context) throws EolRuntimeException {
		GsnProperty g = GsnProperty.parse(property);
		if (g != null /*&& p.isAttribute()*/) {
			Element element = (Element) target;
			//element.attr(g.getProperty(), String.valueOf(value));
			element.getElementById(property).attr("content", String.valueOf(value));
			return;
		}
		super.invoke(target, property, value, context);
	}
	
}
