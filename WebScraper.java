import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {
    public static void main(String[] args) throws Exception {
        final String url = "https://bbc.com";

        BBCScraping scraper = new BBCScraping();
        scraper.scrape(url);
        System.out.println(scraper);
    }

    static class BBCScraping {
        private String title;
        private String headings;
        private String links;
        private String authors;
        private String publicationDates;
        private String headlines;

        public void scrape(String url) throws IOException {
            Document doc = Jsoup.connect(url).get();

            this.title = doc.title();

            StringBuilder headingBuilder = new StringBuilder();
            for (int i = 1; i <= 6; i++) {
                Elements headingElements = doc.select("h" + i);
                for (Element heading : headingElements) {
                    headingBuilder.append("H").append(i).append(": ").append(heading.text()).append("\n");
                }
            }
            this.headings = headingBuilder.toString();

            StringBuilder linkBuilder = new StringBuilder();
            Elements linkElements = doc.select("a[href]");
            for (Element link : linkElements) {
                linkBuilder.append(link.text()).append(" -> ").append(link.absUrl("href")).append("\n");
            }
            this.links = linkBuilder.toString();

            StringBuilder headlinesBuilder = new StringBuilder();
            StringBuilder datesBuilder = new StringBuilder();
            StringBuilder authorsBuilder = new StringBuilder();

            Elements promos = doc.select("div.gs-c-promo");
            for (Element promo : promos) {
                Element headline = promo.selectFirst("h3.gs-c-promo-heading__title");
                if (headline != null) {
                    headlinesBuilder.append(headline.text()).append("\n");
                }

                Element dateElement = promo.selectFirst("time[datetime]");
                if (dateElement != null) {
                    datesBuilder.append(dateElement.attr("datetime")).append("\n");
                }

                Element authorElement = promo.selectFirst("div.gs-c-promo-meta__author");
                if (authorElement != null) {
                    authorsBuilder.append(authorElement.text()).append("\n");
                }
            }

            this.headlines = headlinesBuilder.toString();
            this.publicationDates = datesBuilder.toString();
            this.authors = authorsBuilder.toString();
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getHeadings() { return headings; }
        public void setHeadings(String headings) { this.headings = headings; }
        public String getLinks() { return links; }
        public void setLinks(String links) { this.links = links; }
        public String getAuthors() { return authors; }
        public void setAuthors(String authors) { this.authors = authors; }
        public String getPublicationDates() { return publicationDates; }
        public void setPublicationDates(String publicationDates) { this.publicationDates = publicationDates; }
        public String getHeadlines() { return headlines; }
        public void setHeadlines(String headlines) { this.headlines = headlines; }

        @Override
        public String toString() {
            return String.format(
                "Title:\n%s\n\nHeadings:\n%s\nLinks:\n%s\nHeadlines:\n%s\nPublication Dates:\n%s\nAuthors:\n%s",
                title, headings, links, headlines, publicationDates, authors
            );
        }
    }
}