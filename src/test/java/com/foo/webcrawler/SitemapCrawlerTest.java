package com.foo.webcrawler;

import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SitemapCrawlerTest {

	static final String INDEX_PAGE_URL = "https://github.com/yasserg/crawler4j/index.html",
			CSS_FILE_URL = "https://github.com/yasserg/crawler4j/styles.css",
			DIFFERENT_DOMAIN_URL = "https://example.com/";

	WebURL cssFileWebUrl, differentDomainWebUrl;

	@Before
	public void init() throws URISyntaxException, MalformedURLException {
		SitemapCrawler.configure(INDEX_PAGE_URL);
		cssFileWebUrl = new WebURL();
		cssFileWebUrl.setURL(CSS_FILE_URL);
		differentDomainWebUrl = new WebURL();
		differentDomainWebUrl.setURL(DIFFERENT_DOMAIN_URL);
	}

	@Test
	public void testShouldVisitWithDifferentPage() {
		assertTrue(new SitemapCrawler().shouldVisit(null, cssFileWebUrl));
	}

	@Test
	public void testShouldVisitWithDifferentDomain() {
		assertFalse(new SitemapCrawler().shouldVisit(null, differentDomainWebUrl));
	}

}
