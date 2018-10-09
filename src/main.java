import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {

  public static Queue<String> queue = new LinkedList<>();
  public static Set<String> marked = new HashSet<>();
  public static String regex ="http[s]*://(\\w+\\.)*(\\w+)";

  public static void crawler(String root ) throws IOException {

  }

  public static void showResult() {
    System.out.println("\n\nResult:");
    System.out.println("Number Website crawled:" + marked.size());
    for (String s : marked) {
      System.out.println("site: "+s);
   }
  }
  public static void main(String args[]) {
    try {
      crawler("https://vnexpress.net/");
      showResult();
    } catch (IOException e) {
    }
  }
}
