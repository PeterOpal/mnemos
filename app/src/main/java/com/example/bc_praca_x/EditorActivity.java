package com.example.bc_praca_x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.widget.Toolbar;

public class EditorActivity extends ActivitySetup {
    private WebView webView;
    private int blockPosition;
    private int position;
    private boolean isFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //toolbar
        Toolbar toolbar = findViewById(R.id.addTextContentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        //HTML editor
        webView = findViewById(R.id.editorWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        blockPosition = getIntent().getIntExtra("blockPosition", -1);
        position = getIntent().getIntExtra("position", -1);
        isFront = getIntent().getBooleanExtra("isFront", true);
        String editorContent = getIntent().getStringExtra("textContent");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (editorContent != null && !editorContent.isEmpty()) {
                    loadContent(editorContent);
                }
            }
        });

        webView.loadUrl("file:///android_asset/editor.html");
    }

    private void loadContent(String content) {
        if (content == null || content.isEmpty()) {
            content = "";
        }

        String decodedContent = content.replace("\\u003C", "<")
                                       .replace("\\u003E", ">")
                                       .replace("\\\"", "\"");

        String escapedContent = decodedContent.replace("\\", "\\\\")
                                              .replace("\"", "\\\"")
                                              .replace("\n", "\\n");

        String js_function = "setEditorContent(`" + escapedContent + "`);";
        webView.evaluateJavascript(js_function, value -> {
            Log.d("EditorActivityContent", "Content loaded into WebView: " + escapedContent);
        });
    }


    private void saveContent() {
        webView.evaluateJavascript("getEditorContent();", value -> {

            if (value == null || value.isEmpty() || value.equals("null")) {
                return;
            }

            runOnUiThread(() -> {
                String cleanedValue = value.replaceAll("^\"|\"$", "");
                cleanedValue = cleanedValue.replace("\\u003C", "<")
                                           .replace("\\u003E", ">")
                                           .replace("\\\"", "\"");

                if(cleanedValue.equals("<p><br></p>")) cleanedValue = "";

                Intent resultIntent = new Intent();
                resultIntent.putExtra("blockPosition", blockPosition);
                resultIntent.putExtra("position", position);
                resultIntent.putExtra("updatedContent", cleanedValue);
                resultIntent.putExtra("isFront", isFront);
                Log.d("EditorActivityContent", "Content saved: " + cleanedValue);
                Log.d("EditorActivityContent", "Content loaded into WebView: " + blockPosition);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            });
        });
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}