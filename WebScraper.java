import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class WebScraper {
    static class NewsArticle {
        String headline;
        String publicationDate;
        String authorName;
        public NewsArticle(String headline, String publicationDate, String authorName) {
            this.headline = headline;
            this.publicationDate = publicationDate;
            this.authorName = authorName;
        }
        @Override
        public String toString() {
            return "Headline: " + headline + "\nDate: " + publicationDate + "\nAuthor: " + authorName + "\n";
        }
    }
    public static void main(String[] args) {
        String url = "https://www.bbc.com";
        List<NewsArticle> articles = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            System.out.println("Page Title: " + document.title());
            System.out.println("\nHeadings:");
            for (int i = 1; i <= 6; i++) {
                Elements headings = document.select("h" + i);
                for (Element heading : headings) {
                    System.out.println("h" + i + ": " + heading.text());
                }
            }
            System.out.println("\nLinks:");
            Elements links = document.select("a[href]");
            for (Element link : links) {
                System.out.println("Text: " + link.text());
                System.out.println("Href: " + link.absUrl("href"));
                System.out.println("---");
            }
            System.out.println("\nScraped News Articles:\n");
            Elements headlineElements = document.select("a:has(h3)");

            for (Element element : headlineElements) {
                String headline = element.text();
                String link = element.absUrl("href");
                String date = "N/A";
                String author = "N/A";
                try {
                    Document articleDoc = Jsoup.connect(link).get();

                    Element dateElement = articleDoc.selectFirst("time");
                    if (dateElement != null) {
                        date = dateElement.attr("datetime");
                    }

                    Element authorElement = articleDoc.selectFirst("[rel=author]");
                    if (authorElement != null) {
                        author = authorElement.text();
                    }
                } catch (Exception ignored) {
                }
                NewsArticle article = new NewsArticle(headline, date, author);
                articles.add(article);
            }

            for (NewsArticle article : articles) {
                System.out.println(article);
                System.out.println("------");
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

