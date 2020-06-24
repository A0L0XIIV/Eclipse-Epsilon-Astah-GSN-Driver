package org.eclipse.epsilon.emc.astahgsn;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.plainxml.DomUtil;
import org.eclipse.epsilon.emc.plainxml.StringInputStream;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GsnModel extends CachedModel<Element> {
	
	protected String idAttributeName = "xmi:id";
	
	protected Document document;
	protected String uri = null;
	protected File file = null;
	protected String xml = null;
	
	protected ArrayList<Element> createdElements = new ArrayList<>();
	protected static final String ELEMENT_TYPE = "Element";
	protected static final String DEFAULT_NEW_TAG_NAME = "element";
	public static final String PROPERTY_FILE = "file";
	public static final String PROPERTY_URI = "uri";
	
	public static final String ROOT_TAG = "ARM:Argumentation";
	public static final String ELEMENT_TAG = "argumentElement";
	
	public GsnModel() {
		propertyGetter = new GsnPropertyGetter(this);
		propertySetter = new GsnPropertySetter(this);
	}
	
	public synchronized Node getRoot() {
		System.out.println("GSNModel - getRoot function");
		return document.getFirstChild();
	}
	
	public synchronized void setRoot(Node node) {
		System.out.println("GSNModel - setRoot function");
		
		Node oldRoot = getRoot();
		if (oldRoot != null) document.removeChild(oldRoot);
		document.appendChild(node);
	}

	
	public String getUri() {
		System.out.println("GSNModel - getUri function");
		
		return uri;
	}

	public void setUri(String uri) {
		System.out.println("GSNModel - setUri function");
		
		this.uri = uri;
	}
	
	public String getXml() {
		System.out.println("GSNModel - setXml function");
		
		return xml;
	}

	public void setXml(String xml) {
		System.out.println("GSNModel - setXml function");
		
		this.xml = xml;
	}
	
	public File getFile() {
		System.out.println("GSNModel - getFile function");
		
		return file;
	}

	public void setFile(File file) {
		System.out.println("GSNModel - setFile function");
		
		this.file = file;
	}

	public Document getDocument() {
		System.out.println("GSNModel - getDocument function");
		
		return document;
	}

	@Override
	public boolean isLoaded() {
		System.out.println("GSNModel - isLoaded function");
		
		return document != null;
	}
	
	@Override
	protected Element createInstanceInModel(String type) throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
		System.out.println("GSNModel - createInstanceInModel function");
		
		GsnProperty gsnProperty = GsnProperty.parse(type);
		
		if (gsnProperty != null) {
			Element newElement = null;
			
			// Create a root element
			// GsnType is 'gsn' and document doesn't have any root tag elements
			if (gsnProperty.isRoot()
				&& !document.hasChildNodes()) {
				newElement = document.createElement(ROOT_TAG);
				document.appendChild(newElement);
			}
			// Create a child (argumentElement) tag
			else {
				newElement = document.createElement(ELEMENT_TAG);
				newElement.setAttribute("xsi:type", gsnProperty.getXsiType());
				newElement.setAttribute("xmi:id", gsnProperty.getIdPrefix() + "MustBeUnique"); // Must be unique value!
				newElement.setAttribute("id", gsnProperty.getIdPrefix());
				newElement.setAttribute("content", "");
				newElement.setAttribute("description", "");
				createdElements.add(newElement);
			}

			return newElement;
		}
		else {
			return null;
		}
		
	}
	
	@Override
	protected synchronized boolean deleteElementInModel(Object instance) throws EolRuntimeException {
		System.out.println("GSNModel - deleteElementInModel function");
		
		if (!(instance instanceof Element))
			return false;
		
		Element e = (Element) instance;
		if (e.getParentNode() != null) {
			e.getParentNode().removeChild(e);
		}
		createdElements.remove(e);
		
		// Also remove all its children = For Root element
		for (Element child : DomUtil.getChildren(e)) {
			deleteElement(child);
		}

		return true;
	}
	
	
	
	@Override
	protected synchronized Collection<Element> allContentsFromModel() {
		System.out.println("GSNModel - allContentsFromModel function");
		
		Collection<Element> elements = new ArrayList<>();
		collectAllElements(document, elements);
		for (Element created : createdElements) {
			if (!elements.contains(created) && created.getParentNode() == null) {
				elements.add(created);
			}
		}	
		return elements;
	}
	
	
	public synchronized void collectAllElements(Node root, Collection<Element> elements) {
		//System.out.println("GSNModel - collectAllElements function");
		
		if (root instanceof Element) {
			elements.add((Element) root);
			System.out.println("GSNModel - collectAllElements function, root: " + ((Element)root).getAttribute("id") + " ,hasChild: " + root.hasChildNodes());
		}
		NodeList childNodes = root.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Object o = childNodes.item(i);
			if (o instanceof Element) {
				collectAllElements((Element) o, elements);
			}
		}
	}
	
	
	@Override
	protected Collection<Element> getAllOfKindFromModel(String type) throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getAllOfKindFromModel function");
		
		return getAllOfType(type);
	}
	

	@Override
	protected Collection<Element> getAllOfTypeFromModel(String type) throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getAllOfTypeFromModel function");
		
		if (ELEMENT_TYPE.equals(type)) {
			return allContents();
		}
		else {
			Collection<Element> allOfType = null;
			
			if (type == null) {
				throw new EolModelElementTypeNotFoundException(this.getName(), type);
			}
			
			if (allOfType == null) {
				allOfType = new ArrayList<>();
				for (Element e : allContents()) {
					System.out.println("GSNModel - getAllOfTypeFromModel function, Tag: " + e.getTagName() + " ,root: " + ROOT_TAG);
					if (e.getTagName().equalsIgnoreCase(ROOT_TAG) /*|| e.getTagName().equalsIgnoreCase(ELEMENT_TAG)*/) {
						allOfType.add(e);
					}
				}
			}
		
			return allOfType;
		}
	}
	
	
	@Override
	public boolean owns(Object instance) {
		
		// Element owner check [argumentElement] or Root element
		if (instance instanceof Element) synchronized (this) {
			Element e = (Element) instance;
			
			System.out.println("GSNModel - owns function, owns: " + (e.getOwnerDocument() == document));
			// Get element's owner which is root tag
			if(e.getOwnerDocument() == document) {
				return true;
			}
			
			Node parent = e.getParentNode();
			
			if (parent == null) {
				System.out.println("GSNModel - owns function - IF");
				return createdElements.contains(instance);
			}
			else {
				while (parent.getParentNode() != null) {
					parent = parent.getParentNode();
				}
				System.out.println("GSNModel - owns function - ELSE");
				return parent == document || createdElements.contains(parent);
			}
		}
		// Initial model check, e.g. gsn.all, gsn.G1, ...
		else if(instance instanceof EolModelElementType
				&& ((EolModelElementType) instance).getTypeName().equalsIgnoreCase("gsn")){
			return true;
		}
		// For queries that return ArrayList, e.g. gsn.goal
		else if(instance instanceof ArrayList
				&& ((ArrayList<?>) instance).get(0) instanceof Element) {
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public Object getElementById(String id) {
		System.out.println("GSNModel - getElementById function");
		
		for (Object o : allContents()) {
			Element e = ((Element) o);
			if (e.hasAttribute(idAttributeName) && e.getAttribute(idAttributeName).equals(id)) {
				return e;
			}
		}
		return null;
	}
	
	@Override
	public String getElementId(Object instance) {
		System.out.println("GSNModel - getElementId function");
		
		if (instance instanceof Element) {
			Element element = (Element) instance;
			if (element.hasAttribute(idAttributeName)) {
				return element.getAttribute(idAttributeName);
			}
		}
		return null;
	}
	
	@Override
	public void setElementId(Object instance, String newId) {
		System.out.println("GSNModel - setElementId function");
		
		// do nothing
	}

	public void setIdAttributeName(String idAttributeName) {
		System.out.println("GSNModel - setIdAttributeName function");
		
		this.idAttributeName = idAttributeName;
	}
	
	public String getIdAttributeName() {
		System.out.println("GSNModel - getIdAttributeName function");
		
		return idAttributeName;
	}
	
	@Override
	public Object getEnumerationValue(String enumeration, String label) throws EolEnumerationValueNotFoundException {
		System.out.println("GSNModel - getEnumerationValue function");
		
		return null;
	}

	
	@Override
	public String getTypeNameOf(Object instance) {
		System.out.println("GSNModel - getTypeNameOf function");
		
		if (instance instanceof Element) {
			return ((Element) instance).getTagName();
		}
		else {
			return instance.getClass().getName();
		}
	}
	
	@Override
	protected Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getCacheKeyForType function");
		
		return type;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		System.out.println("GSNModel - getAllTypeNamesOf function");
		
		return Collections.singleton(getTypeNameOf(instance));
	}

	
	@Override
	public Object getTypeOf(Object instance) {
		System.out.println("GSNModel - getTypeOf function");
		
		return instance.getClass();
	}

	
	@Override
	public boolean hasType(String type) {
		System.out.println("GSNModel - hasType function, type: " + type);
		// Only ELEMENT_TYPE and gsn would work
		return ELEMENT_TYPE.equals(type) || (GsnProperty.parse(type) != null);
	}

	
	@Override
	public boolean isInstantiable(String type) {
		System.out.println("GSNModel - isInstantiable function");
		
		return hasType(type);
	}

	
	@Override
	public boolean isModelElement(Object instance) {
		System.out.println("GSNModel - isModelElement function");
		
		return (instance instanceof Element);
	}
	
	
	
	@Override
	protected void disposeModel() {
		System.out.println("GSNModel - disposeModel function");
		
		document = null;
		xml = null;
	}
	
	
	@Override
	protected synchronized void loadModel() throws EolModelLoadingException {
		System.out.println("GSNModel - loadModel function");
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			
			if (readOnLoad) {
				if (this.file != null) {
					document = documentBuilder.parse(this.file);
				}
				else if (this.uri != null){
					document = documentBuilder.parse(this.uri);
				}
				else {
					document = documentBuilder.parse(new StringInputStream(xml));
				}
			}
			else {
				document = documentBuilder.newDocument();
			}
		}
		catch (Exception ex) {
			throw new EolModelLoadingException(ex, this);
		}
	}

	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		System.out.println("GSNModel - load function");
		
		super.load(properties, resolver);
		
		String filePath = properties.getProperty(GsnModel.PROPERTY_FILE);
		
		if (filePath != null && filePath.trim().length() > 0) {
			file = new File(resolver.resolve(filePath));
		}
		else {
			uri = properties.getProperty(GsnModel.PROPERTY_URI);
		}
		
		load();
	}
	
	
	@Override
	public synchronized boolean store(String location) {
		System.out.println("GSNModel - store-location function");
		
		try {
			Source xmlSource = new DOMSource(document);
			Result result = new StreamResult(new FileOutputStream(location));
	
			// create TransformerFactory
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
			// create Transformer for transformation
			Transformer transformer = transformerFactory.newTransformer();
			// Causes newlines every time Epsilon run
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");	//Java XML Indent
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			// transform and deliver content to client
			transformer.transform(xmlSource, result);
			return true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	
	@Override
	public boolean store() {
		System.out.println("GSNModel - store function");
		
		if (file != null) {
			return store(file.getAbsolutePath());
		}
		else if (uri != null){
			return store(uri);
		}
		else {
			throw new UnsupportedOperationException("Cannot save to " + uri);
		}
	}

}
