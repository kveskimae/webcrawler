package com.foo.webcrawler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class SitemapWriter {

	public SitemapWriter() {
	}

	public void writeSitemap(final File file, final Collection<String> urls) throws IOException, ParserConfigurationException, TransformerException {
		Source source  = createDom(urls);
		StreamResult streamResult = new StreamResult(file);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, streamResult);
	}

	DOMSource createDom(final Collection<String> urls) throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElementNS("http://www.sitemaps.org/schemas/sitemap/0.9", "urlset");
		doc.appendChild(rootElement);
		for (String url : urls) {
			rootElement.appendChild(getUrlElement(doc, url));
		}
		DOMSource source = new DOMSource(doc);
		return source;
	}

	private static Node getUrlElement(final Document doc, final String urlLocation) {
		Element urlElement = doc.createElement("url");
		urlElement.appendChild(getChildElement(doc, "loc", urlLocation));
		return urlElement;
	}

	private static Node getChildElement(final Document doc, final String name, final String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

}
