package com.mckay.example.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * JSoupCrawler Crawls Sec Gov Site to pull company subsidiaries based on
 *  Tested in ubuntu 1604 and Windows 8
 *  -MAVEN Instructions:
 *      1. in main directory (mc-crawler) run the following command: mvn clean install
 *      2. after sucessful install, run the following command verbatim to run the crawler (including quotation marks): mvn exec:java -Dexec.args="875045,318154,320193,816284,804055,882095,875320"
 *      3. output is one cik number with company subsidiaries per line in json format
 * @author Casey Scott McKay
 */
public class JSoupCrawler {

    public static void main(String... args) throws IOException {

        if (args.length != 1) {
            System.err.println("Missing cikNumbers. Please enter 1 or more CIK Numbers (e.g., 875045) separated by commas:");
            return;
        }


        Crawler crawler= new Crawler();
        crawler.processCIKs(args[0]);

    }

}
