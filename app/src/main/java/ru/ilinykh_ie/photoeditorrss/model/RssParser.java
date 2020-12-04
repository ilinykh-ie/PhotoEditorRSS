package ru.ilinykh_ie.photoeditorrss.model;

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
    public final static String ITEM = "item";
    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String ENCLOSURE = "enclosure";
    private URL url;

    public RssParser() {
        try {
            url = new URL("https://lenta.ru/rss/articles");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public RssParser(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<News> getNews() throws ParserConfigurationException, IOException, SAXException {
        ArrayList<News> news = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(url.openConnection().getInputStream());

        Element element = dom.getDocumentElement();
        NodeList items = element.getElementsByTagName(ITEM);

        for (int i = 0; i < items.getLength(); i++) {
            News newsElement = new News();

            Node item = items.item(i);
            NodeList properties = item.getChildNodes();

            for (int j = 0; j < properties.getLength(); j++) {
                Node property = properties.item(j);
                String name = property.getNodeName();

                if (name.equalsIgnoreCase(TITLE)) {
                    newsElement.setTitle("Новость №" + i + ":" + property.getFirstChild().getNodeValue());
                } else if (name.equalsIgnoreCase(ENCLOSURE)) {
                    NamedNodeMap nodeMap = property.getAttributes();

                    newsElement.setImageUrl(nodeMap.getNamedItem("url").getNodeValue());
                }
            }

            news.add(newsElement);
        }

        return news;
    }
}

