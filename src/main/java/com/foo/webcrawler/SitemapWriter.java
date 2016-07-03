package com.foo.webcrawler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

class SitemapWriter {

	private final File targetDirectory;

	public SitemapWriter(final String rootFolder) {
		this.targetDirectory = new File(rootFolder);
		if (!targetDirectory.exists()) {
			targetDirectory.mkdirs();
		}
	}

	public void writeSitemap(final String filename, final Collection<String> urls) throws IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElementNS("http://www.sitemaps.org/schemas/sitemap/0.9", "urlset");
			doc.appendChild(rootElement);
			for (String url : urls) {
				rootElement.appendChild(getUrlElement(doc, url));
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			File file = new File(targetDirectory, filename);
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(source, streamResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
