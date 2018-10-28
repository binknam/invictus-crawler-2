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
  private Frontier frontier;
  private String regex = "(?:^|)((ht|f)tp(s?):\\/\\/|www\\.)"
      + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
      + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
  private int number;
  private List<WebUrl> notMarkedUrls = new ArrayList<WebUrl>();
  private Thread invitctusThread;

  public InvictusWebCrawler(int number, long depthOfCrawler, InvictusWebCrawlerControler invictusWebCrawlerControler) {
    this.number = number;
    this.depthOfCrawler = depthOfCrawler;
    this.frontier = invictusWebCrawlerControler.getFrontier();
    System.out.println("Web crawler " + this.number + " was created!");
  }

  public void run() {
    while (true) {
      boolean stoppedCrawler = ProcessCrawl();
      if (stoppedCrawler) {
        System.out.println("======= Crawler number " + this.number + " is stopping!!! ===========");
        return;
      }
    }
  }

  public boolean ProcessCrawl() {
    BufferedReader br = null;
    this.notMarkedUrls = frontier.getWebUrl(10);
    Set<String> cralwerMarkedUrls = new HashSet<>();
    if (notMarkedUrls.isEmpty()) {
      if (frontier.isFinished()) {
        return true;
      }
      try {
        System.out.println("======= Crawler number " + this.number + " is waiting for new urls!!! ===========");
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        frontier.returnUrlsToFrontier(this.notMarkedUrls);
        System.out.println("Error occurred ===== " + e);
        return true;
      }
    } else {
      try {
        for (WebUrl crawledUrl : notMarkedUrls) {
          System.out.println("============= The crawler number " + this.number + " is running ============");
          System.out.println("============= We are in the depth " + crawledUrl.getDepth() + " ============");
          if (crawledUrl.getDepth() >= depthOfCrawler) {
            System.out.println(
                "site: " + crawledUrl.getUrl() + " will not be crawled because its depth match max depth ====");
            return false;
          }
          cralwerMarkedUrls.add(crawledUrl.getUrl());
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
              ok = false;
            } catch (IOException e) {
              System.out.println("IOException url" + crawledUrl + "====");
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
            if (!frontier.getMarked().contains(w) && shouldVisit(w)) {
              System.out.println("site add " + w);
              frontier.addUrlToQueue(new WebUrl(crawledUrl.getDepth() + 1, w));
            } else {
              System.out.println(
                  "site: " + w + " won't be crawled because of your policy should visit or it's marked ====");
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
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean shouldVisit(String url) {
    return !FILTERS.matcher(url).matches()
        && url.startsWith("https://vnexpress.net");
  }

  public void setThread(Thread thread) {
    this.invitctusThread = thread;
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

  public Thread getInvitctusThread() {
    return invitctusThread;
  }

  public void setInvitctusThread(Thread invitctusThread) {
    this.invitctusThread = invitctusThread;
  }

  public List<WebUrl> getNotMarkedUrls() {
    return notMarkedUrls;
  }

  public int getNumber() {
    return number;
  }
}
