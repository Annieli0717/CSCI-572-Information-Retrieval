package com;
import java.io.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaExtractor {
    public static void main(final String[] args) throws IOException, SAXException, TikaException {
        //input directory
        File dir = new File("/Users/lidunxuan/Desktop/CS 572/HW5/solr-7.7.3/latimes/");

        //output file
        PrintWriter pw = new PrintWriter(new File("big.txt"));

        Metadata metadata = new Metadata();
        FileInputStream inputstream;
        ParseContext pcontext = new ParseContext();
        HtmlParser htmlparser = new HtmlParser();

        for (File file : dir.listFiles()) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            inputstream = new FileInputStream(file);
            htmlparser.parse(inputstream, handler, metadata, pcontext);
            String content = handler.toString().replaceAll("[^A-Za-z]+", " ");
            pw.println(content);
        }
        pw.close();
    }
}

