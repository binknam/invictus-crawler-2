package InvictusWebCrawler;

import InvictusFileIO.InvictusFileWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvictusWebCrawler implements Runnable {
  private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js"
      + "|zip|gz))$");

  private long depthOfCrawler = -1;
  private Frontier frontier;
  private int number;
  private List<WebUrl> notMarkedUrls = new ArrayList<WebUrl>();
  private Thread invitctusThread;
  private String root;
  private InvictusFileWriter invictusFileWriter;
  private RobotTxt robotTxt;
  private long timeToDelay;
  private InvictusFetcher fetcher;
  private static final String regex = "(?:^|)((ht|f)tp(s?):\\/\\/|www\\.)"
      + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
      + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
  private static final Pattern TITLE_TAG =
      Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
  private static final Pattern HREF_TAG =
      Pattern.compile("href=\"(.*?)\"", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

  public InvictusWebCrawler(int number, long depthOfCrawler, String root,
      InvictusWebCrawlerControler invictusWebCrawlerControler) {
    this.number = number;
    this.depthOfCrawler = depthOfCrawler;
    this.frontier = invictusWebCrawlerControler.getFrontier();
    this.invictusFileWriter = new InvictusFileWriter();
    this.root = root;
    this.robotTxt = invictusWebCrawlerControler.getRobotTxt();
    this.timeToDelay = invictusWebCrawlerControler.getNumberOfCrawler() * 300;
    this.fetcher = invictusWebCrawlerControler.getFetcher();
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
    if (notMarkedUrls.isEmpty()) {
      if (frontier.isFinished()) {
        return true;
      }
      try {
        System.out.println("======= Crawler number " + this.number + " is waiting for new urls!!! ===========");
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        frontier.returnUrlsToFrontier(this.notMarkedUrls);
        System.out.println("Error occurred ===== " + e);
        return true;
      }
    } else {
      try {

        for (WebUrl crawledUrl : notMarkedUrls) {
          if (crawledUrl.getDepth() > depthOfCrawler) {
            System.out.println(
                this.invitctusThread.getName() + " === " + crawledUrl.getUrl()
                    + " will not be crawled because its depth matches max depth ====");
            continue;
          }
          this.notMarkedUrls.remove(crawledUrl);
          this.frontier.markUrl(crawledUrl.getUrl());

          boolean ok = false;

          while (!ok) {
            try {
              br = fetcher.getBufferedReaderFromUrl(crawledUrl.getUrl());

              ok = true;
            } catch (MalformedURLException e) {
              System.out.println(this.invitctusThread.getName() + " MalformedURL" + crawledUrl.getUrl() + "====");
              ok = false;
            } catch (IOException e) {
              System.out.println(this.invitctusThread.getName() + " IOException url " + crawledUrl.getUrl() + " ====");
              ok = true;
            }
          }

          StringBuilder sb = new StringBuilder();
          String data;

          while ((data = br.readLine()) != null) {
            sb.append(data);
          }

          data = sb.toString();

          Matcher matcher = TITLE_TAG.matcher(data);
          String title = matcher.find() ? matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim() : "Untitled";
          String htmlInbody = data.substring(data.indexOf("<body"), data.lastIndexOf("</body>"));
          String text = htmlInbody.replaceAll("\\<.*?>","");

          Matcher matcherHREF = HREF_TAG.matcher(data);

          this.searchLinks(matcherHREF, crawledUrl);
          this.visit(title, text, data, crawledUrl.getUrl());

          Thread.sleep(timeToDelay);
        }
      } catch (Exception e) {
        frontier.returnUrlsToFrontier(this.notMarkedUrls);
        System.out.println(this.invitctusThread.getName() + " Error when running job" + e);
      }
    }
    return false;
  }

  public void visit(String title, String text, String html, String url) {
    if (text.length() == 0) {
      System.out.println(
          this.invitctusThread.getName() + " " + url + " will not be stored because it doesn't have text data =====");
      return;
    }

    invictusFileWriter.writeWebPageHtml(html, title);
    invictusFileWriter.writeWebPageText(text, title);
    System.out.println(this.invitctusThread.getName() + " === visited url title: " + title );
  }

  public void searchLinks(Matcher matcher, WebUrl crawledUrl) {
    while (matcher.find()) {
      String url = matcher.group();
      url = url.substring(6, url.length() - 1);

      if (url.length() < 500 && robotTxt.allowedUrl(url)) {
        if (url.startsWith("/") && !url.startsWith("//") && url.length() != 1) {
          url = this.root + url;
        }
        boolean addedUrl = false;
        if (!frontier.getMarked().contains(url) && shouldVisit(url) && !url.equals(root + "/")) {
          WebUrl webUrl1 = new WebUrl(crawledUrl,crawledUrl.getDepth() +1, url);
          WebUrl webUrl2 = frontier.isContainedUrl(url);
          if (webUrl2 != null) {
            if (webUrl1.getParent() != null && webUrl2.getParent() != null) {
              if (webUrl1.getParent().getDepth() < webUrl2.getParent().getDepth()) {
                frontier.addUrlToQueue(webUrl1);
                this.frontier.markUrl(crawledUrl.getUrl());
                addedUrl = true;
              }
            }
          } else {
            frontier.addUrlToQueue(webUrl1);
            this.frontier.markUrl(crawledUrl.getUrl());
            addedUrl = true;
          }
        }

        if (addedUrl) {
          System.out.println(this.invitctusThread.getName() + " site add " + url);
        } else {
          System.out.println(
              this.invitctusThread.getName() + " === " + url
                  + " won't be crawled because of your policy should visit or it's marked ====");
        }
      } else {
        System.out.println("site: " + url + "won't be crawled because its length is over size ====");
      }
    }
  }

  public boolean shouldVisit(String url) {
    return !FILTERS.matcher(url).matches()
        && url.startsWith("https://vnexpress.net");
  }

  public void setThread(Thread thread) {
    this.invitctusThread = thread;
  }

  public List<WebUrl> getNotMarkedUrls() {
    return notMarkedUrls;
  }

}
