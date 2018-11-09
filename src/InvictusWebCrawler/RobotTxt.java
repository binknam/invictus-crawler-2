package InvictusWebCrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RobotTxt {
  public List<String> disallowedPaths = new ArrayList<>();

  public RobotTxt(URL url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    try {
      InvictusFetcher fetcher = new InvictusFetcher();
      BufferedReader br = fetcher.getBufferedReaderFromUrl(url.getProtocol()+ "://" +url.getHost() + "/robots.txt");
      StringBuilder sb = new StringBuilder();
      String data;

      while ((data = br.readLine()) != null) {
        sb.append(data);
      }

      String disallowedText = sb.toString();

      int indexUserAgent = disallowedText.contains("User-agent: *") ? disallowedText.indexOf("User-agent: *") : disallowedText.indexOf("User-Agent: *");
      disallowedText = disallowedText.substring(indexUserAgent, disallowedText.indexOf("Allow"));
      disallowedPaths = Arrays.asList(disallowedText.split(" "));
      List<String> temp = new ArrayList<>();
      for (int i = 0; i < disallowedPaths.size(); i++) {
        if (disallowedPaths.get(i).equals("Disallow:")) {
          temp.add(disallowedPaths.get(i+1));
        }
      }
      disallowedPaths = temp;

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean allowedUrl(String url) {
    for (String rule : disallowedPaths) {
      if (url.contains(rule)) {
        System.out.println("Site " + url + " will not be crawled because it's not an allowed url! =====");
        return false;
      }
    }
    return true;
  }
}
