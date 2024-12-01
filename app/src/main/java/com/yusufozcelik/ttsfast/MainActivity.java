package com.yusufozcelik.ttsfast;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private Spinner languageSpinner;
    private EditText editText;
    private Button speakButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        languageSpinner = findViewById(R.id.languageSpinner);
        editText = findViewById(R.id.editText);
        speakButton = findViewById(R.id.speakButton);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            } else {
                Toast.makeText(this, "TextToSpeech başlatılamadı!", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View view, int position, long id) {
                String selectedLanguage = (String) parentView.getItemAtPosition(position);

                if (selectedLanguage.equals("Varsayılan")) {
                    setLanguage(Locale.getDefault());
                    return;
                }

                String[] parts = selectedLanguage.split("\\(");
                if (parts.length > 1) {
                    String languageCode = parts[1].replace(")", "").trim();
                    Locale locale = new Locale(languageCode);
                    setLanguage(locale);
                } else {
                    Toast.makeText(MainActivity.this, "Geçersiz dil formatı!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setLanguage(Locale.getDefault());
            }
        });

        speakButton.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this, "Lütfen bir metin girin!", Toast.LENGTH_SHORT).show();
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void setLanguage(Locale locale) {
        int result = tts.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Dil desteklenmiyor veya eksik veri!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
