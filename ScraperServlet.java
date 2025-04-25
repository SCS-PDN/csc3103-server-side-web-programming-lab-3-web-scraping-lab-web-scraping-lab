import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ScraperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getParameter("url");
        String action = request.getParameter("action");

        // Session Tracking
        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) {
            visitCount = 1;
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Web Scraping Results</title></head><body>");
        out.println("<h1>Web Scraping Results</h1>");
        out.println("<p>You have visited this page " + visitCount + " times.</p>");

        if (url != null && !url.isEmpty()) {
            try {
                WebScraper.BBCScraping scraper = new WebScraper.BBCScraping();
                List<WebScraper.Article> articles = scraper.scrape(url);

                if ("downloadCSV".equals(action)) {
                    // CSV Download Logic
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition", "attachment; filename=\"articles.csv\"");
                    try (PrintWriter csvWriter = response.getWriter()) {
                        csvWriter.println("Headline,Publication Date,Author"); // CSV Header
                        for (WebScraper.Article article : articles) {
                            csvWriter.println(String.format("%s,%s,%s",
                                    article.headline,
                                    article.publicationDate,
                                    article.author));
                        }
                    }
                } else {
                    // Display Results in HTML
                    Gson gson = new Gson();
                    String json = gson.toJson(articles);
                    out.println("<h2>Scraped Data:</h2>");
                    out.println("<pre>" + json + "</pre>");

                    // Add Download CSV Button
                    out.println("<form method='post'>");
                    out.println("<input type='hidden' name='url' value='" + url + "'>");
                    out.println("<input type='hidden' name='action' value='downloadCSV'>");
                    out.println("<button type='submit'>Download CSV</button>");
                    out.println("</form>");
                }

            } catch (IOException e) {
                out.println("<p>Error: " + e.getMessage() + "</p>");
            }
        } else {
            out.println("<p>Please enter a URL.</p>");
        }

        out.println("</body></html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }
}
