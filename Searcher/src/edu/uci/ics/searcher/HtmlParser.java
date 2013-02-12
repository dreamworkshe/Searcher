package edu.uci.ics.searcher;

import org.jsoup.*;
import org.jsoup.nodes.*;

public class HtmlParser {

  /**
   * @param args
   */
  public static void main(String[] args) {
    String html = "<html><head><title>First parse</title></head>"
        + "<body><p>Parsed HTML into a doc.</p></body></html>";
      Document doc = Jsoup.parse(html);
      System.out.println(doc.text());
      System.out.println(doc.title());
  }
  
  String Text;
  
  public void parseFile(String filepath) {
    Document doc = Jsoup.parse(filepath, "UTF-8");
    this.Text = doc.text();
  }

  public String getText() {
    return this.Text;
  }
}
