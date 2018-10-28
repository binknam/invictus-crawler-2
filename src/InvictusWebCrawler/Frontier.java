package InvictusWebCrawler;

import java.util.*;
import java.util.stream.Collectors;

public class Frontier {

  private List<WebUrl> urls;
  private Set<String> marked;

  private boolean isFinished;

  public void setQueue(List<WebUrl> queue) {
    this.urls = queue;
  }

  public void setMarked(Set<String> marked) {
    this.marked = marked;
  }

  public Frontier(List<WebUrl> queue, Set<String> marked) {
    this.urls = queue;
    this.marked = marked;
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
    int i = 0;
    while (urls.iterator().hasNext() && i < maxNumber) {
      WebUrl webUrl = urls.iterator().next();
      results.add(webUrl);
      urls.remove(webUrl);
      i++;
    }
    if (!results.isEmpty())
      return results;
    else
      return Collections.EMPTY_LIST;
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
}