package org.eclipse.epsilon.emc.astahgsn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
	protected int timeout = 60_000;
	
	protected ArrayList<Element> createdElements = new ArrayList<>();
	protected static final String ELEMENT_TYPE = "Element";
	protected static final String DEFAULT_NEW_TAG_NAME = "element";
	public static final String PROPERTY_FILE = "file";
	public static final String PROPERTY_URI = "uri";
	public static final String PROPERTY_TIMEOUT = "timeout";
	
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
	
	public String getUri() {
		System.out.println("GSNModel - getUri function");
		
		return uri;
	}

	public void setUri(String uri) {
		System.out.println("GSNModel - setUri function");
		
		this.uri = uri;
	}
	
	public synchronized String getXml() {
		System.out.println("GSNModel - getXml function");
		
		try {
			StringWriter sw = new StringWriter();
			Source xmlSource = new DOMSource(document);
			Result result = new StreamResult(sw);
	
			// create TransformerFactory
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
			// create Transformer for transformation
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");	//Java XML Indent
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			// transform and deliver content to client
			transformer.transform(xmlSource, result);
			return sw.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
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
		
		//return createInstance(type, Collections.emptyList());
		
		//HTML MODEL USES
		//return document.createElement(PlainXmlType.parse(type).getTagName());
		return document.createElement("argumentElement");
	}
	/*
	@Override
	public synchronized Element createInstance(String type, Collection<Object> parameters) throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
		String tagName = null;
		boolean root = false;
		
		if (ELEMENT_TYPE.equals(type)) {
			if (parameters.size() == 1) {
				tagName = parameters.iterator().next() + "";
			}
			else {
				tagName = DEFAULT_NEW_TAG_NAME;
			}
		}
		else {
			//t_asdasd
			PlainXmlType plainXmlType = PlainXmlType.parse(type);
			if (plainXmlType != null) {
				tagName = plainXmlType.getTagName();
			}
			//not t_asdasd
			if (parameters.size() == 1) {
				Object param = parameters.iterator().next();
				if (param instanceof Boolean) {
					root = ((Boolean) param).booleanValue();
				}
			}
			
		}
		
		Element newElement = document.createElement(tagName);
		if (root == false) {
			createdElements.add(newElement);
		}
		else {
			document.appendChild(newElement);
		}

		return newElement;		
	}
*/
	
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
		
		// Also remove all its children
		for (Element child : DomUtil.getChildren(e)) {
			deleteElement(child);
		}

		return true;
	}
	
	public synchronized void collectAllElements(Node root, Collection<Element> elements) {
		System.out.println("GSNModel - collectAllElements function");
		
		if (root instanceof Element) {
			elements.add((Element) root);
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
			GsnType gsnType = GsnType.parse(type);
			
			if (gsnType == null) {
				throw new EolModelElementTypeNotFoundException(this.getName(), type);
			}
			
			if (allOfType == null) {
				allOfType = new ArrayList<>();
				for (Element e : allContents()) {
					if (nodeTypeMatches(e, gsnType.getGsnType())) {
						allOfType.add(e);
					}
				}
			}
			
			return allOfType;
		}
	}
	
	public synchronized boolean nodeTypeMatches(Element element, String name) {
		System.out.println("GSNModel - nodeTypeMatches function");
		
		// Compare elements' xsi:type attributes
		if (element.getAttribute("xsi:type").equalsIgnoreCase(name)) {
			return true;
		}
		// Get main XML tag, it contains all elements as children tags
		else if(element.getTagName().equals(name)) {
			return true;
		}
		else {
			return false;
		}
		/*else {
			int colonIndex = element.getTagName().indexOf(":");
			if (colonIndex >= 0) {
				return element.getTagName().substring(colonIndex + 1).equalsIgnoreCase(name);
			}
			else {
				return false;
			}
		}*/
	}
	
	@Override
	protected void disposeModel() {
		System.out.println("GSNModel - disposeModel function");
		
		document = null;
		xml = null;
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
			return "t_" + ((Element) instance).getTagName();
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
		
		//return Collections.singleton(getTypeNameOf(instance));
		// Types are fixed and cannot change
		return new ArrayList<String>(Arrays.asList("n_goal", "n_strategy", "n_solution", "n_context", "n_assumption", "n_justification", "l_context", "l_inference", "l_evidence"));
	}

	
	@Override
	public Object getTypeOf(Object instance) {
		System.out.println("GSNModel - getTypeOf function");
		
		return instance.getClass();
	}

	
	@Override
	public boolean hasType(String type) {
		System.out.println("GSNModel - hasType function, type: " + type);
		
		return ELEMENT_TYPE.equals(type) || (GsnType.parse(type) != null);
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
	public boolean owns(Object instance) {
		System.out.println("GSNModel - owns function, instance: " + instance.toString());
		
		if (instance instanceof Element) synchronized (this) {
			Element e = (Element) instance;
			/*Node parent = e.getParentNode();
			
			if (parent == null) {
				return createdElements.contains(instance);
			}
			else {
				while (parent.getParentNode() != null) {
					parent = parent.getParentNode();
				}
				return parent == document || createdElements.contains(parent);
			}*/
			// Might be unnecessary and wrong!!!!!!!!!!!
			if(e.getOwnerDocument() == document) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
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
	
/*
	protected HttpStatusException httpException;
 	protected Document document;
	protected final String ELEMENT_TYPE = "Element";
	public static final String PROPERTY_FILE = "file";
	public static final String PROPERTY_URI = "uri";
	public static final String PROPERTY_TIMEOUT = "timeout";
	
	protected String uri = null;
	protected File file = null;
	protected int timeout = 60_000;

	public GsnModel() {
		propertyGetter = new GsnPropertyGetter();
		propertySetter = new GsnPropertySetter();
	}
	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		System.out.println("GSNModel - Load function");
		super.load(properties, resolver);
		String fileProperty = properties.getProperty(PROPERTY_FILE);
		
		if (fileProperty != null && fileProperty.length() > 0) {
			file = new File(resolver.resolve(fileProperty));
		}
		else {
			uri = properties.getProperty(PROPERTY_URI);
			if (uri.startsWith("file:")) {
				try {
					file = new File(new URI(uri));
				} catch (URISyntaxException e) {
					throw new EolModelLoadingException(e, this);
				}
			}
		}
		load();
	}
	
	public String getUri() {
		System.out.println("GSNModel - GetUri function");
		return uri;
	}
	
	public void setUri(String uri) {
		System.out.println("GSNModel - SetUri function");
		this.uri = uri;
	}
	
	@Override
	public boolean isLoaded() {
		System.out.println("GSNModel - isLoaded function");
		return file != null;
	}
	
	@Override
	public Object getEnumerationValue(String enumeration, String label)
			throws EolEnumerationValueNotFoundException {
		System.out.println("GSNModel - getEnumerationValue function");
		return null;
	}

	@Override
	public String getTypeNameOf(Object instance) {
		System.out.println("GSNModel - getTypeNameOf function");
		return "t_" + ((Element) instance).tagName();
	}

	@Override
	public Object getElementById(String id) {
		System.out.println("GSNModel - getElementById-id function");
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		System.out.println("GSNModel - getElementId-instance function");
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		
	}

	@Override
	public boolean owns(Object instance) {
		System.out.println("GSNModel - owns function");
		if (instance instanceof Element) {
			return ((Element) instance).ownerDocument() == document;
		}
		else return false;
	}

	@Override
	public boolean isInstantiable(String type) {
		System.out.println("GSNModel - isInstantiable function");
		return true;
	}

	@Override
	public boolean hasType(String type) {
		System.out.println("GSNModel - hasType function");
		return ELEMENT_TYPE.equals(type) || PlainXmlType.parse(type) != null;
	}

	@Override
	public boolean store(String location) {
		System.out.println("GSNModel - store-location function");
		try {
			FileUtil.setFileContents(document.html(), new File(location));
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean store() {
		System.out.println("GSNModel - store function");
		if (file != null) {
			store(file.getAbsolutePath());
			return true;
		}
		else {
			throw new UnsupportedOperationException("Cannot save to " + uri);
		}
	}

	@Override
	protected Collection<Element> allContentsFromModel() {
		System.out.println("GSNModel - allContentsFromModel function");
		return document.getAllElements();
	}

	@Override
	protected Collection<Element> getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getAllOfTypeFromModel function");
		return document.select(PlainXmlType.parse(type).getTagName());
	}

	@Override
	protected Collection<Element> getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getAllOfKindFromModel function");
		return getAllOfTypeFromModel(kind);
	}

	@Override
	protected Element createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		System.out.println("GSNModel - createInstanceInModel function");
		return document.createElement(PlainXmlType.parse(type).getTagName());
	}

	@Override
	protected void loadModel() throws EolModelLoadingException {
		System.out.println("GSNModel - loadModel function");
		
		if (readOnLoad) {
			try {
				if (file != null) {
					document = Jsoup.parse(file, null);
				}
				else {
					Connection connection = Jsoup.connect(uri);
					connection.timeout(timeout);
					document = connection.get();
				}
			} 
			catch (HttpStatusException ex) {
				document = Document.createShell(uri);
				httpException = ex;
			}
			catch (IOException e) {
				throw new EolModelLoadingException(e, this);
			}
		}
		else {
			String baseUri = null;
			if (file != null) {
				baseUri = file.toURI().toString();
			}
			else if (uri != null) {
				baseUri = uri;
			}
			else {
				baseUri = "";
			}
			document = Document.createShell(baseUri);
		}
	}

	@Override
	protected void disposeModel() {
		System.out.println("GSNModel - disposeModel function");
		httpException = null;
	}

	@Override
	protected boolean deleteElementInModel(Object instance)
			throws EolRuntimeException {
		System.out.println("GSNModel - deleteElementInModel function");
		((Element) instance).remove();
		return false;
	}

	@Override
	protected Object getCacheKeyForType(String type)
			throws EolModelElementTypeNotFoundException {
		System.out.println("GSNModel - getCacheKeyForType function");
		return type;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		System.out.println("GSNModel - getAllTypeNamesOf function");
		return Collections.singleton(getTypeNameOf(instance));
	}
	
	public Document getDocument() {
		System.out.println("GSNModel - getDocument function");
		return document;
	}
	
	public HttpStatusException getHttpException() {
		System.out.println("GSNModel - getHttpException function");
		return httpException;
	}
*/
}
