import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/scrape")
public class ScrapeServlet extends HttpServlet {

    static class ScrapedData {
        String type;
        String content;

        public ScrapedData(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", ++visitCount);

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("option");
        List<ScrapedData> dataList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            if (options != null) {
                for (String option : options) {
                    switch (option) {
                        case "title":
                            dataList.add(new ScrapedData("Title", doc.title()));
                            break;
                        case "links":
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                dataList.add(new ScrapedData("Link", link.absUrl("href")));
                            }
                            break;
                        case "images":
                            Elements imgs = doc.select("img[src]");
                            for (Element img : imgs) {
                                dataList.add(new ScrapedData("Image", img.absUrl("src")));
                            }
                            break;
                    }
                }
            }

        } catch (Exception e) {
            dataList.add(new ScrapedData("Error", e.getMessage()));
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Scrape Results</h2>");
        out.println("<p>You have visited this page " + visitCount + " times.</p>");
        out.println("<table border='1'><tr><th>Type</th><th>Content</th></tr>");
        for (ScrapedData data : dataList) {
            out.println("<tr><td>" + data.type + "</td><td>" + data.content + "</td></tr>");
        }
        out.println("</table><br>");

        Gson gson = new Gson();
        String json = gson.toJson(dataList);
        session.setAttribute("jsonData", json);

        out.println("<form action='download' method='post'>");
        out.println("<input type='submit' value='Download CSV'>");
        out.println("</form>");

        out.println("<h3>JSON Output:</h3>");
        out.println("<pre>" + json + "</pre>");

        out.println("</body></html>");
    }
}
