package questions;
import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

	public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
	public directedGraph internet;             // this is our internet graph



	// Constructor initializes everything to empty data structures
	// It also sets the location of the internet files
	searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String> > ();		
		internet = new directedGraph();				
	} // end of constructor2015


	// Returns a String description of a searchEngine
	public String toString () {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}


	// This does a graph traversal of the internet, starting at the given url.
	// For each new vertex seen, it updates the wordIndex, the internet graph,
	// and the set of visited vertices.

	void traverseInternet(String url) throws Exception {
		/* WRITE SOME CODE HERE */

		internet.addVertex(url);
		internet.setVisited(url, true);
		internet.setPageRank(url, 1);

		LinkedList<String> words = htmlParsing.getContent(url);
		Iterator<String> j = words.iterator();
		while ( j.hasNext() ) {
			String s = j.next();
			if (wordIndex.containsKey(s) == false){
				wordIndex.put(s, new LinkedList<String>());
			}
			if(!wordIndex.get(s).contains(url)){
				wordIndex.get(s).addLast(url);	
			}
		}

		LinkedList<String> neighbours = htmlParsing.getLinks(url);
		Iterator<String> i = neighbours.iterator();
		while ( i.hasNext() ) {
			String s = i.next();
			internet.addEdge(url, s);
			if (!internet.getVisited(s)){
				traverseInternet(s);
			}
		}

		/* Hints
	   0) This should take about 50-70 lines of code (or less)
	   1) To parse the content of the url, call
	   htmlParsing.getContent(url), which returns a LinkedList of Strings 
	   containing all the words at the given url. Also call htmlParsing.getLinks(url).
	   and assign their results to a LinkedList of Strings
	   2) To iterate over all elements of a LinkedList, use an Iterator,
	   as described in the text of the assignment
	   3) Refer to the description of the LinkedList methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods contains(String s), 
	   addLast(String s), iterator()
	   4) Refer to the description of the HashMap methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods containsKey(String s), 
	   get(String s), put(String s, LinkedList l).  
		 */
	} // end of traverseInternet


	/* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 

       This method will probably fit in about 30 lines.
	 */
	void computePageRanks() {


		// Do the same in a loop now
		for (int i = 0; i < 100; i++){
			LinkedList<String> nodes = internet.getVertices();
			//iterate over all nodes in internet
			Iterator<String> l = nodes.iterator();
			while ( l.hasNext() ) {
				String s = l.next();
				internet.setPageRank(s, .5);

				LinkedList<String> into = internet.getEdgesInto(s);

				// iterate over all vertices that have edges into s
				Iterator<String> k = into.iterator();
				while ( k.hasNext() ) {
					String t = k.next();
					internet.setPageRank(s, internet.getPageRank(s) + .5 * (internet.getPageRank(t) / internet.getOutDegree(t)) );
				}
			}
		}
	}// end of computePageRanks

	/* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
	 */
	String getBestURL(String query) {
		String besturl = null;
		if (wordIndex.containsKey(query)){
			LinkedList<String> sites = wordIndex.get(query);
			double bestrank = 0;

			Iterator<String> i = sites.iterator();
			while ( i.hasNext() ) {
				String s = i.next();
				if (internet.getPageRank(s) > bestrank){
					bestrank = internet.getPageRank(s);
					besturl = s;
				}
			}
		}
		else{
			besturl = "";
		}

		return besturl; 
	} // end of getBestURL



	public static void main(String args[]) throws Exception{		
		searchEngine mySearchEngine = new searchEngine();
		// to debug your program, start with.
		//mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

		// When your program is working on the small example, move on to
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

		// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
		//System.out.println("traverseInternet complete!");
		mySearchEngine.computePageRanks();
		//System.out.println(mySearchEngine);

		BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
		String query;
		do {
			System.out.print("Enter query: ");
			query = stndin.readLine();
			if ( query != null && query.length() > 0 ) {
				System.out.println("Best site = " + mySearchEngine.getBestURL(query));
			}
		} while (query!=null && query.length()>0);				
	} // end of main
}