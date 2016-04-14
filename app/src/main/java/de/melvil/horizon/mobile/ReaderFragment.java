package de.melvil.horizon.mobile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ReaderFragment extends Fragment {

    private WordManager wordManager;

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

    public void loadText(String path, String lang) {
        //wordManager = new WordManager(lang);
        try {
            StringBuilder html = new StringBuilder(2097152);
            String textHtml = FileUtils.readFileToString(new File(path + ".txt"));
            // put it into a html5 document with css and js
            html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">");
            html.append("<link href=\"reader.css\" type=\"text/css\" rel=\"stylesheet\" />");
            //html.append("<script href=\"reader.js\" type=\"text/javascript\" />");
            html.append("</head><body>");
            html.append(textHtml);
            html.append("</body></html>");
            // put html into webview
            WebView webView = (WebView) getView().findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient());
            //webView.getSettings().setJavaScriptEnabled(true);
            //webView.addJavascriptInterface(this, "Android");
            webView.getSettings().setSupportZoom(true);
            String highlightedHtml = html.toString();
            webView.loadDataWithBaseURL("file:///android_asset/", highlightedHtml, "text/html",
                    "UTF-8", null);
        } catch(Exception e){
            Toast.makeText(getView().getContext(), "Error loading text...", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @JavascriptInterface
    public String getStrengthClass(String word){
        int strength = wordManager.getStrength(word);
        if(strength == -1)
            return "blue";
        else if(strength == 0)
            return "red";
        else if(strength == 1)
            return "yellow";
        else if(strength == 2)
            return "green";
        else
            return "white";
    }

    @JavascriptInterface
    public void showMeaning(String word) {
        String meaning = TextUtils.join(", ", wordManager.getMeanings(word));
        if (!meaning.equals(""))
            Toast.makeText(getActivity().getApplicationContext(), meaning,
                    Toast.LENGTH_SHORT).show();
    }
}
