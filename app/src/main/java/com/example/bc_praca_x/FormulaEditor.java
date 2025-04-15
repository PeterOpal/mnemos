package com.example.bc_praca_x;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class FormulaEditor extends ActivitySetup {
    private WebView webView;
    private int blockPosition;
    private int position;
    private boolean isFront;
    private String editorContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        blockPosition = intent.getIntExtra("blockPosition", -1);
        position = intent.getIntExtra("position", -1);
        isFront = intent.getBooleanExtra("isFront", true);
        editorContent = intent.getStringExtra("formulaContent");

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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (editorContent != null && !editorContent.isEmpty()) {
                    String escapedLatex = editorContent.replace("\\", "\\\\");
                    webView.evaluateJavascript("mathField.setValue(\"" + escapedLatex + "\");", null);
                }
            }
        });


        webView.loadUrl("file:///android_asset/formula-editor.html");
    }

    private void getLatexAndSave() {
        webView.evaluateJavascript("getLatex();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if(value != null && !value.isEmpty() && !value.contains("placeholder")) {
                    String cleanedValue = value.replaceAll("^\"|\"$", "").replace("\\\\", "\\");

                    Log.d("FormulaEditorr", "Received value: " + cleanedValue);
                    if(cleanedValue.equals("\\n    ")) cleanedValue = null;

                    Intent intent = new Intent();
                    intent.putExtra("updatedContent", cleanedValue);
                    intent.putExtra("blockPosition", blockPosition);
                    intent.putExtra("position", position);
                    intent.putExtra("isFront", isFront);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(FormulaEditor.this, getString(R.string.formula_error), Toast.LENGTH_SHORT).show();
                }
            }
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
            getLatexAndSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}