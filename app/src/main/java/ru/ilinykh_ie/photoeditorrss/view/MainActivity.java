package ru.ilinykh_ie.photoeditorrss.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SketchFilterTransformation;
import ru.ilinykh_ie.photoeditorrss.R;
import ru.ilinykh_ie.photoeditorrss.controller.Controller;
import ru.ilinykh_ie.photoeditorrss.model.Filter;
import ru.ilinykh_ie.photoeditorrss.model.News;

public class MainActivity extends AppCompatActivity {
    private Controller controller;
    private ArrayList<ImageView> imageViews;
    private ArrayList<News> news;
    private LinearLayout linearLayout;
    private int latestNewsNumber = 0;
    private boolean isNewsOver;
    private Filter filter = Filter.ORIGINAL;
    private Transformation blur;
    private Transformation blackWhite;
    private Transformation contrast;
    private Transformation sketch;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new Controller();
        news = controller.getNews();
        imageViews = new ArrayList<>(news.size());

        blur = new BlurTransformation(this);
        blackWhite = new GrayscaleTransformation();
        contrast = new InvertFilterTransformation(this);
        sketch = new SketchFilterTransformation(this);

        linearLayout = findViewById(R.id.linearLayout);
        addNews();

        ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = ((ScrollView) v).getChildAt(((ScrollView) v).getChildCount() - 1);

            if ((view.getBottom() - v.getHeight() - v.getScrollY()) == 0) {
                ArrayList<News> extraNews = controller.getNews();

                if (extraNews == null) {
                    if (!isNewsOver) {
                        News end = new News();
                        end.setTitle("Новостей больше нет");
                        news.add(end);
                        addNews();

                        isNewsOver = true;
                    }

                    return;
                }

                news.addAll(extraNews);
                addNews();
            }
        });
    }

    public void setMinSize(ImageView imageView) {
        if (imageView.getMinimumHeight() == 0) {
            imageView.setMinimumHeight(imageView.getHeight());
        }

    }

    public void addNews() {
        for (int i = latestNewsNumber; i < news.size(); i++) {
            TextView textView = new TextView(this);
            textView.append(news.get(i).getTitle() + "\n");
            linearLayout.addView(textView);

            ImageView imageView = new ImageView(this);

            if (filter == Filter.ORIGINAL) {
                Picasso.get().load(news.get(i)
                        .getImageUrl()).
                        into(imageView);
            } else {
                Transformation transformation;

                if (filter == Filter.BLACK_WHITE) {
                    transformation = blackWhite;
                } else if (filter == Filter.BLUR) {
                    transformation = blur;
                } else if (filter == Filter.CONTRAST) {
                    transformation = contrast;
                } else {
                    transformation = sketch;
                }

                Picasso.get().load(news.get(i).getImageUrl())
                        .transform(transformation)
                        .into(imageView);
            }

            imageViews.add(imageView);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 15);
            lp.gravity = Gravity.CENTER;
            imageView.setLayoutParams(lp);
            linearLayout.addView(imageView);

            ImageView newsSeparator = new ImageView(this);
            newsSeparator.setMinimumHeight(20);
            newsSeparator.setBackgroundColor(Color.rgb(80, 80, 80));
            linearLayout.addView(newsSeparator);

            latestNewsNumber++;
        }
    }

    public void setBlurImages(View view) {
        filter = Filter.BLUR;

        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);

            setMinSize(imageView);

            Picasso.get().load(news.get(i).getImageUrl())
                    .transform(blur)
                    .into(imageView);
        }
    }

    public void setOriginalImages(View view) {
        filter = Filter.ORIGINAL;

        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);

            setMinSize(imageView);

            Picasso.get().load(news.get(i).getImageUrl())
                    .into(imageView);
        }
    }

    public void setBlackWhiteImages(View view) {
        filter = Filter.BLACK_WHITE;

        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);

            setMinSize(imageView);

            Picasso.get().load(news.get(i).getImageUrl())
                    .transform(blackWhite)
                    .into(imageView);
        }
    }

    public void setSketchImages(View view) {
        filter = Filter.SKETCH;

        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);

            setMinSize(imageView);

            Picasso.get().load(news.get(i).getImageUrl())
                    .transform(sketch)
                    .into(imageView);
        }
    }

    public void setContrastImages(View view) {
        filter = Filter.CONTRAST;

        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);

            setMinSize(imageView);

            Picasso.get().load(news.get(i).getImageUrl())
                    .transform(contrast)
                    .into(imageView);
        }
    }
}