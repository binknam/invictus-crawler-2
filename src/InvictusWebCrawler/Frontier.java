package InvictusWebCrawler;

import java.util.*;

public class Frontier {

  private List<WebUrl> urls;
  private Set<String> marked;
  private long numberOfCrawlers;

  private boolean isFinished;

  public Frontier(List<WebUrl> queue, Set<String> marked, long numberOfCrawlers) {
    this.urls = queue;
    this.marked = marked;
    this.numberOfCrawlers = numberOfCrawlers;
  }

  public Set<String> getMarked() {
    return marked;
  }

  public void addUrlToQueue(WebUrl url) {
    urls.add(url);
  }

  public List<WebUrl> getWebUrl(long maxNumber)
  {
    List<WebUrl> results = new ArrayList<WebUrl>();
    long size  = Math.min(maxNumber, urls.size());
    for (long i = 0; i < size; i++) {
      WebUrl webUrl = urls.iterator().next();
      results.add(webUrl);
      urls.remove(webUrl);
    }
    return results;
  }

  public void setFinished(boolean finished) {
    isFinished = finished;
  }

  public boolean isFinished() {
    return isFinished;
  }

  public boolean isListWebUrlEmpty() {
    return this.urls.isEmpty();
  }

  public void returnUrlsToFrontier(List<WebUrl> urls)
  {
    this.urls.addAll(urls);
  }

  public void markUrl(String url) {
    this.marked.add(url);
  }

  public WebUrl isContainedUrl(String url) {
    for (WebUrl webUrl: urls) {
      if (webUrl.getUrl().equals(url)) {
        return webUrl;
      }
    }
    return null;
  }
}
