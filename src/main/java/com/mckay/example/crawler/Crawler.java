package com.mckay.example.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Crawler {
    ParserToJson parserToJson=new ParserToJson();

    public Crawler() throws FileNotFoundException {
    }

    /*
        --Method process CIK Numbers and returns json string of all subsidiaries associated with that number
        @params String an String of CIK numbers separated by commas: e.g., "875045,318154,320193,816284,804055,882095,875320"
        @returns a string of json objects containing the companies subsidiaries--one cik number string per line
     */



    public String processCIKs(String inputCikNums) throws IOException {
        String urlTemp="";
        String[] cikNums=inputCikNums.split(",");
        String cikNumTemp="";
        for (int i=0;i<cikNums.length;i++){
            urlTemp+= processPage("https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK="+cikNums[i].trim()+"&type=10-K")+"\n";
        }
        String[] current10KUrls=urlTemp.split("\n");
        String jsonOut="";
        for(String s:current10KUrls){
            String urlToEx21="https://www.sec.gov/"+s;
            cikNumTemp=urlToEx21.substring(urlToEx21.indexOf("/data/")+6);
            cikNumTemp=cikNumTemp.substring(0,cikNumTemp.indexOf("/"));
            HashMap<String, String>subsidiaryMap=parserToJson.getJsonObjectFromUrl(urlToEx21);



            jsonOut="{\""+cikNumTemp+"\": [";
            String kTemp="";
            for(Map.Entry e: subsidiaryMap.entrySet()){

                kTemp=e.getKey().toString();
                //replace all weird encoded double quotes with single quotes to be json friendly (alternatively, could replace with unicode double quotes and escape, but not necessary as none of these are quoted material)
                kTemp=kTemp.replaceAll("","'").replaceAll("","'");

                jsonOut+="\""+kTemp+"\", ";
            }
            jsonOut=jsonOut.trim();
            jsonOut=jsonOut.substring(0,jsonOut.length()-1);
            jsonOut+="]}";

            //todo fix lazy cik number checker cause it's awful
            if(jsonOut.length()<20){
                System.out.println("Invalid CKI Number "+cikNumTemp);
            }
            else {
                System.out.println(jsonOut+"\n");
            }

        }
        return jsonOut;

    }
    /*
        --Method used by processCIKs (See above).
        --Method processes a sex url recursively to seek out the latest 10k
        --latest 10k is always first in feed, so use simple i checker to get 10k link
        --returns the proper url for the most current 10k link
        @Params: url for a specific company's 10k page
        @Returns: url for latest 10k
     */

    public static String processPage(String URL) throws  IOException{
        //check if the given URL is already in database
        String urlOut="";
        //get useful information
        Document doc = Jsoup.connect(URL).get();

        //get all links and recursively call the processPage method
        Elements links = doc.select("a[href]");
            //to check for first link in fee, which is most up to date
        int i = 0;
        for (Element link : links) {
            if (link.attr("href").contains("sec.gov"))
                processPage(link.attr("abs:href"));
                //processPage(link.attr(""));
            String tempLink = link.toString();
            if (tempLink.contains("Documents</a>") && i == 0) {
                i++;
                urlOut = tempLink.substring(tempLink.indexOf("/Archives"));
                    //todo:investigate weird thing here won't catch index\\.htm but catches +8 for some reason?
                urlOut = urlOut.substring(0, urlOut.indexOf("index.htm") + 9);
            }
        }
        return urlOut;
    }
}
