package InvictusFileIO;

import java.io.*;

public class InvictusFileWriter {

  public void writeWebPageText(String text, String title) {
    String fileName = "storage/storeText/" + title + ".txt";
    writeFile(text, fileName);
  }

  public void writeWebPageHtml(String html, String title) {
    String fileName = "storage/storeHtml/" + title + ".html";
    writeFile(html, fileName);
  }


  private void writeFile(String text, String fileName) {
    try {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
          FileOutputStream(fileName),"UTF-8"));
      bw.write(text);
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
