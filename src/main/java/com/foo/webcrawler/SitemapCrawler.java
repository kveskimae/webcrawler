package com.foo.webcrawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class SitemapCrawler extends WebCrawler {

	private static String crawlDomain;
	private static Set<String> foundLResources = new ConcurrentSkipListSet(); // TODO should be non-static

	public static Set<String> getFoundLResources() {
		return foundLResources;
	}

	public static void configure(final String url) throws URISyntaxException, MalformedURLException {
		URL uri = new URL(url);
		crawlDomain = uri.getProtocol() + "://" + uri.getAuthority();
	}

	@Override
	public boolean shouldVisit(final Page referringPage, final WebURL webURL) {
		String href = webURL.getURL().toLowerCase();
		String url = webURL.getURL();
		foundLResources.add(url);
		if (href.startsWith(crawlDomain)) {
			return true;
		}
		return false;
	}

}