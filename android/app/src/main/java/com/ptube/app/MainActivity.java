package com.ptube.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PREFS = "ptube_preferences";
    private static final String KEY_SERVER_URL = "server_url";

    private WebView webView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(webView);

        configureWebView();

        String serverUrl = preferences.getString(KEY_SERVER_URL, null);
        if (serverUrl == null || serverUrl.isBlank()) {
            showServerDialog();
        } else {
            loadServer(serverUrl);
        }
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    private void showServerDialog() {
        EditText input = new EditText(this);
        input.setHint(getString(R.string.server_hint));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.server_prompt_title)
                .setMessage(R.string.server_prompt_message)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton(R.string.connect, null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String url = normalizeUrl(input.getText().toString().trim());

                    if (!isValidHttpsUrl(url)) {
                        Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    preferences.edit().putString(KEY_SERVER_URL, url).apply();
                    dialog.dismiss();
                    loadServer(url);
                })
        );

        dialog.show();
    }

    private String normalizeUrl(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    private boolean isValidHttpsUrl(String value) {
        try {
            Uri uri = Uri.parse(value);
            return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void loadServer(String serverUrl) {
        webView.loadUrl(serverUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
