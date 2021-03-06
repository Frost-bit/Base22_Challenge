package com.base22.challenge;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;
import java.util.List;
import java.util.ArrayList;
 
class Args {
  @Parameter
  private List<String> parameters = new ArrayList<>();
  
  @Parameter(names = "-inputFileName", description = "Name of the file containing the urls to parse")
  private String inputFileName;
  
  @Parameter(names = "-outputFileName", description = "Name of the output file to create")
  private String outputFileName;
  
  @Parameter(names = "-cleanHTML", description = "If included, inline styles in the HTML will be removed")
  private boolean cleanHTML = false;
  
  String getInputFileName() {
    return inputFileName;
  }
  
  String getOutputFileName() {
    return outputFileName;
  }
  
  boolean getCleanHTML() {
    return cleanHTML;
  }
}
  
public class Base22Challenge {
  
  private PrintWriter pw;
  private Base22Challenge(String outputFileName){
    try {
      pw = new PrintWriter(new File(outputFileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    pw.write("url  title  body  links\n");
  }
  
  private void writeUrl(String url, StringBuilder builder) {
    builder.append(url + "  ");
  }
  
  private void writeTitle(Document doc, StringBuilder builder) {
    builder.append(doc.title() + "  ");
  }
  
  private void writeBody(Document doc, StringBuilder builder, boolean clean) {
    if (clean) {
      Elements elements = doc.body().select("*");
      for (Element element : elements) {
        element.removeAttr("style");
      }
    }
    String docString = doc.toString().replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("  +", " ");
    if (docString.charAt(docString.length()-1) == ' ') {
      builder.append(docString + " ");
    }
    else {
      builder.append(docString + "  ");
    }
  }
  
  private void writeUrls(Document doc, StringBuilder builder, String baseURL) {
    Elements links = doc.select("a");
    for (Element link : links) {
      builder.append("[" + link.text() + "]");
      if (link.attr("href").length() > 0 && link.attr("href").charAt(0) == '/') {
        builder.append("(" + baseURL + link.attr("href") + ")");
      } else {
         builder.append("(" + link.attr("href") + ")");
      }
    }
    builder.append("\n");
  }
  
  private void builderToFile(StringBuilder builder) {
    pw.write(builder.toString());
  }
  
  private void close() {
    pw.close();
  }
  
  public static void main(String[] args){
    Args argParser = new Args();
    JCommander.newBuilder()
            .addObject(argParser)
            .build()
            .parse(args);
    if (argParser.getInputFileName() == null) {
      System.out.println("You must provide a file with urls");
      System.exit(0);
    }else if (argParser.getOutputFileName() == null) {
      System.out.println("You must provide an output file");
      System.exit(0);
    }
    Base22Challenge csvCreator = new Base22Challenge(argParser.getOutputFileName());
    
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(argParser.getInputFileName()));
      String url = reader.readLine();
      while (url != null) {
        try {
          Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
          StringBuilder builder = new StringBuilder();
          csvCreator.writeUrl(url, builder);
          csvCreator.writeTitle(doc, builder);
          csvCreator.writeBody(doc, builder, argParser.getCleanHTML());
          csvCreator.writeUrls(doc, builder, url);
          csvCreator.builderToFile(builder);
        } catch (IOException e) {
          System.out.println(e);
        }
        // read next line
        url = reader.readLine();
      }
      csvCreator.close();
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}