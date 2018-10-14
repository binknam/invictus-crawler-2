package InvictusWebCrawler;

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

  private long depthOfCrawler = -1;

  public static Queue<WebUrl> queue = new LinkedList<>();
  private static Set<String> marked = new HashSet<>();
  private String regex = "(?:^|)((ht|f)tp(s?):\\/\\/|www\\.)"
      + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
      + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
  private int number;

  public InvictusWebCrawler(int number, long depthOfCrawler) {
    this.number = number;
    this.depthOfCrawler = depthOfCrawler;
    System.out.println("Web crawler " + this.number + " was created!");
  }

  @Override
  public void run() {
    BufferedReader br = null;
    System.out.println("Web crawler " + this.number + " is running!");
    try {
      while (!queue.isEmpty()) {
        WebUrl crawledUrl = queue.poll();
        System.out.println("============= We are in the depth " + crawledUrl.getDepth()+" ============");
        if (crawledUrl.getDepth() >= depthOfCrawler) {
          System.out.println("=============== Match depth of crawler so crawler will stop!!!!! ===================");
          return;
        }
        marked.add(crawledUrl.getUrl());
          System.out.println("site: " + crawledUrl.getUrl() + " is crawling ====");

          boolean ok = false;
          URL url;

          while (!ok) {
            try {
              url = new URL(crawledUrl.getUrl());
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
            if (!marked.contains(w) && shouldVisit(w)) {
              marked.add(w);
              System.out.println("site add " + w);
              queue.add(new WebUrl(crawledUrl.getDepth() + 1, w));
            } else {
              System.out.println("site: " + w + " won't be crawled because of your policy should visit or it's marked ====");
            }
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

  public Queue<WebUrl> getQueue() {
    return queue;
  }

  public void setQueue(Queue<WebUrl> queue) {
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

  public long getDepthOfCrawler() {
    return depthOfCrawler;
  }

  public void setDepthOfCrawler(long depthOfCrawler) {
    this.depthOfCrawler = depthOfCrawler;
  }
}
