# Web Crawler

This project is a web crawler.
It is implemented in Java and limited to crawling one domain.

Crawling result is a sitemap file for that domain. Sitemap is a way of informing search engines about pages on your site.

Given a starting URL, this crawler visits and searches all the reachable pages within that domain, including static contents like cascading style sheets (css-files). It does not follow links to external sites like Facebook.

The resultant sitemap lists all the other pages in this domain, links to static content such as images, and to external URLs.

## Building and running

This is a Maven project with dependencies on third-party libraries. It is easiest to use an IDE to run this appliction. The exact procedure is IDE-specific. Building and running from terminal is explained below.

### Building executable with Maven from terminal

We will look at how to create executable jar-file with external dependencies, so that you will not need to point to third party libraries in classpath argument.
Inside project folder run Maven assembly command:

``` bash
~/webcrawler $ mvn assembly:assembly
```
Running all the tests with assembly may take a few minutes.

You will see where the created jar-file was saved from the command output in terminal window.

``` bash
[INFO] Building jar: /Users/kristjanveskimae/webcrawler/target/webcrawler-0.1-jar-with-dependencies.jar
```

This Java archive that was created will be executable with entry point specified in manifest file and will contain external dependencies.

### Running from terminal

Program entry point, i.e. the main method is in class *com.foo.webcrawler.Main* .

To start the program, provide the location of created artifact from previous together with domain to crawl as the 1st program argument:

``` bash
java -jar ~/webcrawler/target/webcrawler-0.1-jar-with-dependencies.jar http://example.com
```
Additional optional program arguments include:
 * 2nd argument is root folder to contain intermediate crawl data and the resultant sitemap file; defaults to */sitemaps* ;
 * 3rd argument is filename, which defaults to *sitemap.xml* and will contain be the resultant sitemap.

## Crawling a domain

Crawling a domain is done by *SitemapCrawlController* , which relies on external
open source Java crawler [Crawler4j](https://github.com/yasserg/crawler4j).

For future references, Crawler4j has a limitation in architecture, where crawling is started with providing crawler class. This makes implementing crawling of several sites a tricky endeavour:

``` Java
SitemapCrawler.configure(crawlDomain);
CrawlController controller = ...
controller.start(SitemapCrawler.class, numberOfCrawlers);
```
Another good alternative to Crawler4j would be JSoup, see [StackOverflow discussion](http://stackoverflow.com/questions/11282503/java-web-crawler-libraries).

### Threading

Crawling is done with several threads.
Number of threads to run the program is picked as suggested in [Java Concurrency in Practice](https://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601/),
namely number of available cores plus one:
``` Java
 Runtime.getRuntime().availableProcessors() + 1
```

##  Saving sitemap

Site map gets saved using core Java methods in class *SitemapWriter*.
Most external tools rely on base URL and can only contain URLs which start with base.
Real-world site maps for search engine crawlers would not have external links,
so saving them would probably be easier with libraries,
e.g. [SitemapGen4j](https://github.com/dfabulich/sitemapgen4j) or [jsitemapgenerator](https://github.com/jirkapinkas/jsitemapgenerator) .

Sitemap is saved by default under folder */sitemaps* by the name *sitemap.xml*, resulting in file */sitemaps/sitemap.xml* .
You can provide your own folder and file name with program arguments.
See running section above.

### Resource properties

Currently only resource locations get written down.
Other properties like resource last modified time,
change frequency and priority are omitted.
These properties are unknown in the problem at hand.




