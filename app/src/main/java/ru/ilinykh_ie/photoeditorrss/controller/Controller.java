package ru.ilinykh_ie.photoeditorrss.controller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.util.ArrayList;

import ru.ilinykh_ie.photoeditorrss.model.News;
import ru.ilinykh_ie.photoeditorrss.model.RssParser;

public class Controller {
    private final AsyncRequest asyncRequest;
    private RssParser rssParser;

    public Controller() {
        asyncRequest = new AsyncRequest();
        execute();
    }

    public Controller(String url) {
        asyncRequest = new AsyncRequest(url);
        execute();
    }

    private void execute() {
        asyncRequest.execute();

        while (rssParser == null) {
            Thread t = Thread.currentThread();

            try {
                t.join(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<News> getNews() {
        ArrayList<News> news = null;

        AsyncNews asyncNews = new AsyncNews();
        asyncNews.execute();

        while (news == null) {
            Thread t = Thread.currentThread();
            try {
                t.join(500);
                news = asyncNews.getList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (news.size() == 0) {
            return null;
        }

        return news;
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    class AsyncRequest extends AsyncTask<Void, Void, Void> {
        private final boolean isWithoutArguments;
        private String url;

        public AsyncRequest() {
            isWithoutArguments = true;
        }

        public AsyncRequest(String url) {
            this.url = url;
            isWithoutArguments = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isWithoutArguments) {
                rssParser = new RssParser();
            } else {
                rssParser = new RssParser(url);
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    class AsyncNews extends AsyncTask<Void, Void, Void> {
        ArrayList<News> list;

        @Override
        protected Void doInBackground(Void... voids) {
            list = rssParser.getNews();
            return null;
        }

        public ArrayList<News> getList() {
            return list;
        }
    }
}