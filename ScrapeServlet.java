import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/scrape")
public class ScrapeServlet extends HttpServlet {

    static class ScrapeResult {
        String title;
        List<String> links;
        List<String> images;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("scrapeOption");

        ScrapeResult result = new ScrapeResult();
        result.links = new ArrayList<>();
        result.images = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            if (options != null) {
                for (String opt : options) {
                    switch (opt) {
                        case "title":
                            result.title = doc.title();
                            break;
                        case "links":
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                result.links.add(link.absUrl("href"));
                            }
                            break;
                        case "images":
                            Elements imgs = doc.select("img[src]");
                            for (Element img : imgs) {
                                result.images.add(img.absUrl("src"));
                            }
                            break;
                    }
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(result);

            // Output JSON and table
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h2>Scraped Data</h2>");

            if (result.title != null) {
                response.getWriter().println("<p><strong>Title:</strong> " + result.title + "</p>");
            }

            if (!result.links.isEmpty()) {
                response.getWriter().println("<h3>Links</h3><table border='1'>");
                for (String link : result.links) {
                    response.getWriter().println("<tr><td><a href='" + link + "'>" + link + "</a></td></tr>");
                }
                response.getWriter().println("</table>");
            }

            if (!result.images.isEmpty()) {
                response.getWriter().println("<h3>Images</h3><table border='1'>");
                for (String img : result.images) {
                    response.getWriter().println("<tr><td><img src='" + img + "' width='100'></td></tr>");
                }
                response.getWriter().println("</table>");
            }

            response.getWriter().println("<h3>JSON Output</h3><pre>" + json + "</pre>");
            response.getWriter().println("</body></html>");

        } catch (Exception e) {
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}