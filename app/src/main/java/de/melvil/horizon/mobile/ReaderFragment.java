package de.melvil.horizon.mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ReaderFragment extends Fragment {

    public ReaderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reader, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void loadText(String path) {
        try {
            String html = FileUtils.readFileToString(new File(path + ".html"));
            WebView webView = (WebView) getView().findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setSupportZoom(true);
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html",
                    "UTF-8", null);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(this, "Android");
        } catch(Exception e){
            Toast.makeText(getView().getContext(), "Error loading text...", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @JavascriptInterface
    public void showMeaning(String meaning) {
        if (meaning == null || meaning.trim().equals(""))
            return;

        Toast t = Toast.makeText(getActivity().getApplicationContext(), meaning.trim(),
                Toast.LENGTH_LONG);
        ViewGroup group = (ViewGroup) t.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(21);
        t.getView().setBackgroundColor(Color.rgb(200, 220, 230));
        t.show();
    }
}
