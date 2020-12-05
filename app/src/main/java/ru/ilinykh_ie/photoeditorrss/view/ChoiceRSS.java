package ru.ilinykh_ie.photoeditorrss.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import ru.ilinykh_ie.photoeditorrss.R;

public class ChoiceRSS extends AppCompatActivity {
    private String urlRSS;
    public final static String CHOSEN_URL_RSS = "ru.ilinykh_ie.photoeditorrss.view.ChoiceRSS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_r_s_s);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickOkButton(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.kommersant:
                urlRSS = "https://www.kommersant.ru/RSS/main.xml";
                break;
            case R.id.aif:
                urlRSS = "https://aif.ru/rss/paper.php";
                break;
            case R.id.lenta:
                urlRSS = "https://lenta.ru/rss/articles";
                break;
            case R.id.habr:
                urlRSS = "https://habr.com/ru/rss/hubs/all/";
                break;
            case R.id.yahoo:
                urlRSS = "https://news.yahoo.com/rss/";
                break;
            case -1:
                urlRSS = ((EditText) findViewById(R.id.enteredURL)).getText().toString();
                break;
        }

        if (urlRSS != null && !urlRSS.equalsIgnoreCase("")) {
            Intent intent = new Intent();
            intent.putExtra(CHOSEN_URL_RSS, urlRSS);
            setResult(RESULT_OK, intent);
        }

        finish();
    }
}