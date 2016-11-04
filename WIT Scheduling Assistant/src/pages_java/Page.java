package pages_java;


import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;

public abstract class Page {
	private static WebEngine webEngine;
	public static void setWebEngine(WebEngine webEngine) {
		Page.webEngine = webEngine;
	}
	
	protected static interface PageLoaded {
		public void pageLoaded(Document doc);
	}
	
	protected static void addTemperaryDocListner(PageLoaded loaded) {
		ChangeListener<Document> listener = new ChangeListener<Document>() {
			public void changed(ObservableValue<? extends Document> o, Document old, Document doc) {
				if(doc == null) return;
				
				webEngine.documentProperty().removeListener(this);
				if(loaded != null) loaded.pageLoaded(doc);
		}};
		
		webEngine.documentProperty().addListener(listener);
	}
	
	protected Document document;
	
	protected Page(String url, final Object... args) {
		Platform.runLater(() -> {
			webEngine.load(url);
			
			addTemperaryDocListner(doc -> {
				Page.this.document = doc;
				init(args);
			});
		});
		
		synchronized (this) {
			try { this.wait(); } catch (InterruptedException e) {}
		}
	}
	
	protected Page(Document doc, Object... args) {
//		Platform.runLater(() -> {
			this.document = doc; 
			init(args);
//		});
	}
	
	public void printDocument() {
		try {
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
		    transformer.transform(new DOMSource(document), 
		         new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
		} catch(IOException | TransformerException e) {}
	}
	
	protected abstract void init(Object[] args);
	
	protected synchronized void doneLoading() {
		this.notify();
	}
	
	protected void submitForm(String formXPath, PageLoaded callBack) {
		addTemperaryDocListner(callBack);
		callJavaScript("getElementByXpath(\"" + formXPath + "\").submit()");
	}
	
	protected void setValue(String xPath, String value) {
		getByXPath(xPath).setAttribute("value", value);
	}
	
	protected void back() {
		webEngine.getHistory().go(-1);
	}
	
	protected Element getByXPath(String xPath) {
		return (Element) callJavaScript("getElementByXpath(\"" + xPath + "\");");
	}
	
	protected Object callJavaScript(String call) {
		return webEngine.executeScript(
			  "window.$ = function(selector) {"
			+ "		var selectorType = 'querySelectorAll';"
		    
			+ "		if(selector.indexOf('#') === 0) {"
			+ "			selectorType = 'getElementById';"
			+ "			selector = selector.substr(1, selector.length);"
			+ "		}"
		    
			+ "		return document[selectorType](selector);"
			+ "};"
			
			+ "function getElementByXpath(path) {"
			+ "		return document.evaluate(path, document, null, "
			+ "			XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
			+ "}" 
			
			+ call);
	}
}
//window.$ = function(selector) {
//var selectorType = 'querySelectorAll';
//
//if(selector.indexOf('#') === 0) {
//	selectorType = 'getElementById';
//	selector = selector.substr(1, selector.length);
//}
//
//return document[selectorType](selector);
//};
//
//function getElementByXpath(path) {
//return document.evaluate(path, document, null, 
//	XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
//} 
