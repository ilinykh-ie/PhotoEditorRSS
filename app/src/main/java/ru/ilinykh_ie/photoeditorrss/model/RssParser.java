package ru.ilinykh_ie.photoeditorrss.model;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RssParser {
    private final static String ITEM = "item";
    private final static String TITLE = "title";
    private final static String LINK = "link";
    private final static String DESCRIPTION = "description";
    private final static String ENCLOSURE = "enclosure";
    private final static String MEDIA = "media:group";
    private final static String MEDIA_CONTENT = "media:content";
    private URL url;
    private int latestSendNews = 0;
    private ArrayList<News> news;
    private NodeList items;

    public RssParser() {
        this("https://www.kommersant.ru/RSS/main.xml");
    }

    public RssParser(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        news = new ArrayList<>();
        setItems();
    }

    public void setItems() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(url.openConnection().getInputStream());

            Element element = dom.getDocumentElement();
            items = element.getElementsByTagName(ITEM);

        } catch (ParserConfigurationException | IOException | SAXException | NullPointerException e) {
            e.printStackTrace();

            News exception = new News();
            exception.setTitle("Не удалось подключиться по этой ссылке");
            news.add(exception);
        }
    }

    public ArrayList<News> getNews() {
        if (items == null) {
            return news;
        }

        news = new ArrayList<>();

        if (latestSendNews == items.getLength()) {
            return news;
        }

        int newsCount = 10;
        int limit = Math.min(latestSendNews + newsCount, items.getLength());

        for (int i = latestSendNews; i < limit; i++) {
            News newsElement = new News();

            String imageLink = null;

            Node item = items.item(i);
            NodeList properties = item.getChildNodes();

            for (int j = 0; j < properties.getLength(); j++) {
                Node property = properties.item(j);
                String name = property.getNodeName();

                if (name.equalsIgnoreCase(TITLE)) {
                    newsElement.setTitle("Новость №" + (i + 1) + ": " + property.getFirstChild().getNodeValue());
                } else if (name.equalsIgnoreCase(ENCLOSURE)) {
                    if (newsElement.getImageUrl() == null || newsElement.getImageUrl().equalsIgnoreCase("")) {
                        NamedNodeMap nodeMap = property.getAttributes();

                        newsElement.setImageUrl(nodeMap.getNamedItem("url").getNodeValue());
                    }
                } else if (name.equalsIgnoreCase(LINK)) {
                    imageLink = property.getFirstChild().getNodeValue();
                } else if (name.equalsIgnoreCase(DESCRIPTION)) {
                    if (newsElement.getImageUrl() == null || newsElement.getImageUrl().equalsIgnoreCase("")) {
                        NodeList description = property.getChildNodes();
                        org.jsoup.nodes.Document document = Jsoup.parse(description.item(0).getTextContent());
                        String imageUrl = document.getElementsByTag("img").attr("src");

                        newsElement.setImageUrl(imageUrl);
                    }
                } else if (name.equalsIgnoreCase(MEDIA)) {
                    if (newsElement.getImageUrl() == null || newsElement.getImageUrl().equalsIgnoreCase("")) {
                        NodeList mediaGroup = property.getChildNodes();

                        for (int k = 0; k < mediaGroup.getLength(); k++) {
                            Node mediaContent = mediaGroup.item(k);

                            if (mediaContent.getNodeName().equalsIgnoreCase(MEDIA_CONTENT)) {
                                newsElement.setImageUrl(mediaContent.getAttributes().getNamedItem("url").getNodeValue());
                                break;
                            }
                        }
                    }
                } else if (name.equalsIgnoreCase(MEDIA_CONTENT)) {
                    if (newsElement.getImageUrl() == null || newsElement.getImageUrl().equalsIgnoreCase("")) {
                        newsElement.setImageUrl(property.getAttributes().getNamedItem("url").getNodeValue());
                    }
                }
            }

            if (newsElement.getImageUrl() == null || newsElement.getImageUrl().equalsIgnoreCase("")) {
                try {
                    org.jsoup.nodes.Document document = Jsoup.connect(imageLink).get();
                    Elements elements = document.select("head meta[property='og:image']");
                    String imageUrl = elements.attr("content");

                    newsElement.setImageUrl(imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            news.add(newsElement);
        }

        latestSendNews = limit;

        return news;
    }
}