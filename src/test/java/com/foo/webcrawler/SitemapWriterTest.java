package com.foo.webcrawler;


import org.apache.tika.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SitemapWriterTest {

	SitemapWriter sitemapWriter;

	Transformer transformer;

	static final String url = "https://github.com/yasserg/crawler4j",
			urlForCssFile = "https://github.com/yasserg/crawler4j/styles.css",
			urlToExternalSite = "https://google.com";

	Collection<String> internalUrls;

	static final String EXPECTED_XML_FOR_INTERNAL_URLS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
			"<url>\n" +
			"<loc>" + url + "</loc>\n" +
			"</url>\n" +
			"<url>\n" +
			"<loc>" + urlForCssFile + "</loc>\n" +
			"</url>\n" +
			"</urlset>";

	@Before
	public void init() throws TransformerConfigurationException {
		sitemapWriter = new SitemapWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		internalUrls = Arrays.asList(url, urlForCssFile);
	}

	@Test
	public void testCreateDom() throws ParserConfigurationException, TransformerException, IOException, SAXException {
		String generatedXml = generateResultXml(internalUrls);
		Diff diff = new Diff(generatedXml, EXPECTED_XML_FOR_INTERNAL_URLS);
		assertTrue("Generated XML differs from expected", diff.identical());
	}

	@Test
	public void testCreateDomWithExternalLink() throws ParserConfigurationException, TransformerException, IOException, SAXException {
		Collection<String> urls = Arrays.asList(url, urlForCssFile, urlToExternalSite);
		String generatedXml = generateResultXml(urls);
		String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
				"<url>\n" +
				"<loc>" + url + "</loc>\n" +
				"</url>\n" +
				"<url>\n" +
				"<loc>" + urlForCssFile + "</loc>\n" +
				"</url>\n" +
				"<url>\n" +
				"<loc>" + urlToExternalSite + "</loc>\n" +
				"</url>\n" +
				"</urlset>";
		Diff diff = new Diff(generatedXml, expectedXml);
		assertTrue("Generated XML differs from expected", diff.identical());
	}

	@Test
	public void testWritingSitemapToDisk() throws IOException, TransformerException, ParserConfigurationException, SAXException {
		File file =new File("tmp_sitemap.xml");
		file.delete();
		sitemapWriter.writeSitemap(file, internalUrls);
		assertTrue(file.exists());
		assertFalse(file.length() < 1);
		try(FileInputStream inputStream = new FileInputStream(file)) {
			String xmlFromFile = IOUtils.toString(inputStream);
			Diff diff = new Diff(xmlFromFile, EXPECTED_XML_FOR_INTERNAL_URLS);
			assertTrue("XML read from disk differs from expected", diff.identical());
		}
		file.delete();
	}

	private String generateResultXml(Collection<String> urls) throws ParserConfigurationException, TransformerException {
		DOMSource source = sitemapWriter.createDom(urls);
		StringWriter writer = new StringWriter();
		StreamResult streamResult = new StreamResult(writer);
		transformer.transform(source, streamResult);
		String result = writer.toString();
		return result;
	}

}
