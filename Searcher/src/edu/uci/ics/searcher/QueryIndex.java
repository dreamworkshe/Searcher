package edu.uci.ics.searcher;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

public class QueryIndex {
  
  private static Hashtable<String, QueryResults> OracleResults;
  private static String ProjectPath = "/Users/yaocheng/git/edu.uci.ics.searcher/Searcher/";
  private static String[] Queries = {"mondego", "machine learning", "software engineering", "security", "student affairs", "graduate courses",
     "Crista Lopes", "REST", "computer games", "information retrieval"};
  
  static class QueryResults {
    private String[] urlList; // Search results
    private double[] DCGList; // List of DCG
    private Hashtable<String, Integer> relList; // List of relevance
    
    // Constructor for oracle 
    public QueryResults(String query) throws Exception {
      this.urlList = new String[5];
      this.DCGList = new double[5];
      this.relList = new Hashtable<String, Integer>();
      
      // Read oracle results from file
      BufferedReader br = new BufferedReader(new FileReader(new File(ProjectPath+"query_results/"+query+".txt")));
      for (int i = 0; i < 5; i++) {
        String url = br.readLine().trim();
        this.urlList[i] = url;
        this.relList.put(url, 5-i); // The relevance is the ordering of results
        this.DCGList[i] = this.computeDCG(i);
      }
      br.close();
    }
    
    // Constructor for our results
    public QueryResults(String query, QueryResults oracle) throws Exception {
      this.urlList = SearchFiles.getTopSearchResults(query, 5);
      this.DCGList = new double[5];
      this.relList = new Hashtable<String, Integer>();
      
      for (int i = 0; i < 5; i++) {
        String url = this.urlList[i];
        int score = oracle.getRelOfUrl(url);
        if (score >= 0) {
          this.relList.put(url, score);
        } else {
          this.relList.put(url, 0);
        }
        this.DCGList[i] = this.computeDCG(i);
      }
      
    }
    
    // Compute the DCG at a given position
    public double computeDCG(int pos) {
      double score = (double)this.relList.get(this.urlList[pos]);
      //System.out.println("Score: "+Math.log(pos) / Math.log(2));
      if (pos == 0) {
        return score;
      } else {
        return this.DCGList[pos-1] + score / (Math.log(pos+1) / Math.log(2));
      }
    }
    
    // Get the DCG at a given position
    public double getDCGAtPos(int pos) {
      assert pos < this.DCGList.length;
      return this.DCGList[pos];
    }
    
    // If the url is not in search results, return -1
    public int getRelOfUrl(String url) {
      //System.out.println(url);
      if (this.relList.containsKey(url)) {
        return this.relList.get(url);
      } else {
        return -1;
      }
    }
    
    // For testing
    public void printUrls() {
      for (int i = 0; i < this.urlList.length; i++) {
        System.out.println("Url: "+this.urlList[i]+"\nRel: "+this.getRelOfUrl(this.urlList[i]));
      }
    }

  }
  
  public static void initGoogleResults() throws Exception {
    OracleResults = new Hashtable<String, QueryResults>();
    for (String query: Queries) {
      QueryResults qr = new QueryResults(query);
      OracleResults.put(query, qr);
    }
    
    // For testing
//    Enumeration<String> e = OracleResults.keys();
//    while (e.hasMoreElements()) {
//      String query = e.nextElement();
//      QueryResults qr = OracleResults.get(query);
//      System.out.println(query);
//      qr.printUrls();
//    }
  }
  
  
  
  public static double getNDCG5() throws Exception {
    //initGoogleResults();
    for (String query: Queries) {
      System.out.print("NDCG@5 of "+query+": ");
      QueryResults oracle = OracleResults.get(query);
      QueryResults ours = new QueryResults(query, oracle);
      for (int i = 0; i < 5; i++) {
        //System.out.print(ours.getDCGAtPos(i) + "/" + oracle.getDCGAtPos(i) + "=");
        System.out.print(ours.getDCGAtPos(i) / oracle.getDCGAtPos(i));
        System.out.print(" ");
      }
      System.out.println();
    }
    return 1.0;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    //SearchFiles.getTopSearchResults("student affairs", 100);
    initGoogleResults();
    getNDCG5();
  }
}
