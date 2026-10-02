package id.apak.cekkeberangkatanhaji;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String START_URL = "https://haji.go.id/data-informasi";
    private static final String ARTICLES_URL = "https://haji.go.id/berita";
    private static final int GREEN = Color.rgb(11, 107, 58);
    private static final int MUTED = Color.rgb(105, 115, 108);

    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorView;
    private View settingsPage;
    private TextView[] navItems;
    private int selectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        root.addView(progressBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3)));

        FrameLayout pageContainer = new FrameLayout(this);
        root.addView(pageContainer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        webView = new WebView(this);
        pageContainer.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        errorView = new TextView(this);
        errorView.setText("Halaman belum dapat dimuat. Periksa koneksi internet, lalu ketuk untuk mencoba lagi.");
        errorView.setTextColor(Color.rgb(50, 50, 50));
        errorView.setTextSize(16);
        errorView.setGravity(Gravity.CENTER);
        errorView.setPadding(dp(28), dp(28), dp(28), dp(28));
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(v -> webView.loadUrl(selectedTab == 1 ? ARTICLES_URL : START_URL));
        pageContainer.addView(errorView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        settingsPage = createSettingsPage();
        settingsPage.setVisibility(View.GONE);
        pageContainer.addView(settingsPage, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        root.addView(createBottomNavigation(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(64)));
        setContentView(root);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                String host = uri.getHost();
                if ("https".equalsIgnoreCase(scheme) && host != null &&
                        (host.equalsIgnoreCase("haji.go.id") || host.endsWith(".haji.go.id"))) {
                    return false;
                }
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                applySimplifiedLayout(view);
                errorView.setVisibility(View.GONE);
                if (selectedTab != 2) webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, android.webkit.WebResourceRequest request,
                                        android.webkit.WebResourceError error) {
                if (request.isForMainFrame() && selectedTab != 2) {
                    webView.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(selectedTab == 2 || progress == 100 ? View.GONE : View.VISIBLE);
            }
        });

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt("selectedTab", 0);
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(START_URL);
        }
        showTab(selectedTab, false);
    }

    private View createBottomNavigation() {
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.setGravity(Gravity.CENTER_VERTICAL);
        navigation.setBackgroundColor(Color.WHITE);
        navigation.setElevation(dp(8));
        navigation.setPadding(dp(4), 0, dp(4), 0);

        String[] labels = {"Cek Porsi", "Artikel Haji", "Setting"};
        navItems = new TextView[labels.length];
        for (int i = 0; i < labels.length; i++) {
            final int tab = i;
            TextView item = new TextView(this);
            item.setText(labels[i]);
            item.setTextSize(12);
            item.setGravity(Gravity.CENTER);
            item.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            item.setPadding(dp(2), dp(8), dp(2), dp(8));
            item.setOnClickListener(v -> showTab(tab, true));
            navItems[i] = item;
            navigation.addView(item, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
        }
        updateNavigationStyle();
        return navigation;
    }

    private void showTab(int tab, boolean loadPage) {
        selectedTab = tab;
        updateNavigationStyle();
        errorView.setVisibility(View.GONE);
        if (tab == 2) {
            webView.setVisibility(View.GONE);
            settingsPage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            settingsPage.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            String target = tab == 1 ? ARTICLES_URL : START_URL;
            if (loadPage && !target.equals(webView.getUrl())) webView.loadUrl(target);
            progressBar.setVisibility(webView.getProgress() == 100 ? View.GONE : View.VISIBLE);
        }
    }

    private void updateNavigationStyle() {
        if (navItems == null) return;
        for (int i = 0; i < navItems.length; i++) {
            boolean active = i == selectedTab;
            navItems[i].setTextColor(active ? GREEN : MUTED);
            navItems[i].setBackground(makeRoundedBackground(
                    active ? Color.rgb(232, 245, 237) : Color.TRANSPARENT, dp(12)));
        }
    }

    private View createSettingsPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(24), dp(20), dp(24));
        content.setBackgroundColor(Color.rgb(248, 250, 248));

        TextView title = new TextView(this);
        title.setText("Setting");
        title.setTextSize(23);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(32, 42, 36));
        content.addView(title);

        TextView rateButton = createSettingsButton("Rate Us");
        rateButton.setOnClickListener(v -> openRatePage());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        buttonParams.topMargin = dp(20);
        content.addView(rateButton, buttonParams);

        TextView privacyButton = createSettingsButton("Privacy Policy");
        privacyButton.setOnClickListener(v -> showPrivacyPolicy());
        LinearLayout.LayoutParams privacyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(54));
        privacyParams.topMargin = dp(12);
        content.addView(privacyButton, privacyParams);
        scroll.addView(content);
        return scroll;
    }

    private TextView createSettingsButton(String label) {
        TextView button = new TextView(this);
        button.setText(label);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(GREEN);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setBackground(makeRoundedBackground(Color.WHITE, dp(14)));
        return button;
    }

    private void openRatePage() {
        Uri playStoreUri = Uri.parse("market://details?id=" + getPackageName());
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, playStoreUri));
        } catch (android.content.ActivityNotFoundException exception) {
            Uri webUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        }
    }

    private void showPrivacyPolicy() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage("Aplikasi Cek Keberangkatan Haji tidak meminta akun pengguna dan tidak menyimpan nomor porsi secara terpisah. Nomor porsi dan CAPTCHA dimasukkan pada layanan resmi haji.go.id melalui WebView. Pemrosesan data pada layanan tersebut mengikuti kebijakan privasi dan ketentuan Kementerian Haji dan Umrah Republik Indonesia.")
                .setPositiveButton("Tutup", null)
                .show();
    }

    private GradientDrawable makeRoundedBackground(int color, int radius) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);
        return background;
    }

    private void applySimplifiedLayout(WebView view) {
        String script = "(function(){"
                + "var id='cek-haji-simplified-layout';"
                + "var style=document.getElementById(id);"
                + "if(!style){style=document.createElement('style');style.id=id;document.head.appendChild(style);}"
                + "style.textContent='header, footer, .page-header, .tabs, #userwayAccessibilityIcon {display:none !important;} body {background:#fff !important;} .estimasi-content {display:block !important;} .btn-submit {background-color:#0B6B3A !important; border-color:#0B6B3A !important; color:#fff !important;} .btn-submit:hover, .btn-submit:focus {background-color:#08562E !important; border-color:#08562E !important;} #kodePorsi {background-color:#fff !important; border:1px solid #0B6B3A !important; color:#212529 !important;} #kodePorsi:focus {border-color:#0B6B3A !important; box-shadow:0 0 0 .2rem rgba(11,107,58,.2) !important; outline:none !important;}';"
                + "})();";
        view.evaluateJavascript(script, null);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectedTab", selectedTab);
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (selectedTab != 0) {
            showTab(0, true);
        } else if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
