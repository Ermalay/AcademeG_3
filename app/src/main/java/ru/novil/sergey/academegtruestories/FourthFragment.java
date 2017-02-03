package ru.novil.sergey.academegtruestories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import ru.novil.sergey.navigationdraweractivity.R;

public class FourthFragment extends Fragment {

    private WebView webView;
    LinearLayout ll_pb_tv_4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_fourth_fragment, container, false);

        ll_pb_tv_4 = (LinearLayout) v.findViewById(R.id.ll_pb_tv_4);
        webView = (WebView) v.findViewById(R.id.webViewFrag);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("https://m.vk.com/academeg_reviews");
//        webView.loadUrl("https://www.instagram.com/academeg/");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
                    webView.goBack();
                    return false;
                } else {
                    return false;
                }

            }
        });

        return v;
    }

    private class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageFinished(WebView view, String url) {
            //you might need this
            ll_pb_tv_4.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            if(Uri.parse(url).getHost().length() == 0) {
//            if(Uri.parse(url).getHost().endsWith("m.vk.com/")) {
//            if(Uri.parse(url).getHost().startsWith("https://m.vk.com/")) {
            if(Uri.parse(url).getHost().contains("vk.com")) {
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }

//            webView.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
//                        webView.goBack();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            return false;
        }

    }
//    public boolean goBack() {
//        if(webView.canGoBack()) {
//            webView.goBack();
//            return true;
//        }
//        return false;
//    }
}
