package ru.ilinykh_ie.photoeditorrss.controller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import ru.ilinykh_ie.photoeditorrss.model.News;
import ru.ilinykh_ie.photoeditorrss.model.RssParser;

public class Controller {
    private ArrayList<News> news = null;
    private int latestSendNews = 0;
    private final RssParser rssParser = new RssParser();

    public Controller() {
        AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.execute();

        while (news == null) {
            Thread t = Thread.currentThread();
            try {
                t.join(500);
                news = asyncRequest.getList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<News> getNews() {
        if (latestSendNews == news.size()) {
            return null;
        }

        ArrayList<News> newsSubList;

        int newsCount = 10;

        if (latestSendNews + newsCount > news.size()) {
            newsSubList = new ArrayList<>(news.subList(latestSendNews, news.size()));
            latestSendNews = news.size();

            return newsSubList;
        }

        newsSubList = new ArrayList<>(news.subList(latestSendNews, latestSendNews + newsCount));
        latestSendNews += newsCount;

        return newsSubList;
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    class AsyncRequest extends AsyncTask<Void, Void, ArrayList<News>> {
        private ArrayList<News> list = null;

        @Override
        protected ArrayList<News> doInBackground(Void... voids) {
            try {
                list = rssParser.getNews();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            }

            return list;
        }

        public ArrayList<News> getList() {
            return list;
        }
    }
}