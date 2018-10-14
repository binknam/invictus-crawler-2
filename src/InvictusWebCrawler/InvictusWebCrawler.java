package InvictusWebCrawler;

import InvictusFileIO.InvictusFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvictusWebCrawler implements Runnable {
  private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js"
      + "|zip|gz))$");

  public static Queue<String> queue = new LinkedList<>();
  private static Set<String> marked = new HashSet<>();
  private String regex = "(?:^|)((ht|f)tp(s?):\\/\\/|www\\.)"
      + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
      + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
  private int number;

  public InvictusWebCrawler(int number) {
    this.number = number;
    System.out.println("Web crawler " + this.number + " was created!");
  }

  @Override
  public void run() {
    BufferedReader br = null;
    System.out.println("Web crawler " + this.number + " is running!");
    try {
      while (!queue.isEmpty()) {
        String crawledUrl = queue.poll();
        marked.add(crawledUrl);
        if (shouldVisit(crawledUrl)) {
          System.out.println("site: " + crawledUrl + " is crawling ====");

          boolean ok = false;
          URL url;

          while (!ok) {
            try {
              url = new URL(crawledUrl);
              br = new BufferedReader(new InputStreamReader(url.openStream()));
              ok = true;
            } catch (MalformedURLException e) {
              System.out.println("MalformedURL" + crawledUrl + "====");
              crawledUrl = queue.poll();
              ok = false;
            } catch (IOException e) {
              System.out.println("IOException url" + crawledUrl + "====");
              crawledUrl = queue.poll();
              ok = false;
            }
          }

          StringBuilder sb = new StringBuilder();
          String tmp;

          while ((tmp = br.readLine()) != null) {
            sb.append(tmp);
          }

          tmp = sb.toString();
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(tmp);

          while (matcher.find()) {
            String w = matcher.group();
            if (!marked.contains(w)) {
              marked.add(w);
              System.out.println("site add " + w);
              queue.add(w);
            }
          }
        } else {
          System.out.println("site: " + crawledUrl + " won't be crawled because of your policy should visit ====");
        }

        if (br != null) {
          br.close();
        }
      }
    } catch (Exception e) {
      System.out.println("Error when running job");
    }

  }

  public boolean shouldVisit(String url) {
    return !FILTERS.matcher(url).matches()
        && url.startsWith("https://vnexpress.net");
  }

  public Queue<String> getQueue() {
    return queue;
  }

  public void setQueue(Queue<String> queue) {
    this.queue = queue;
  }

  public Set<String> getMarked() {
    return marked;
  }

  public void setMarked(Set<String> marked) {
    this.marked = marked;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

}
