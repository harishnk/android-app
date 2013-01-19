package com.buggycoder.hogsocial;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MainActivity extends Activity implements JSCallbackReceiver {

	TextView tvInfo;
	WebView wv;
	AQuery aq;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        aq = new AQuery(this); //awesome external lib
        
        wv = (WebView) findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return false;
           }
        });
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        // Hide profile text placeholder
        tvInfo.setVisibility(View.GONE);
        
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new JSBridgeHandler(this), "bridge");
        wv.loadUrl("http://buggycoder.com:3000/external/foursquare/login");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public class JSBridgeHandler {
    	JSCallbackReceiver callerActivity;
        public JSBridgeHandler(JSCallbackReceiver activity) {
        	callerActivity = activity;
        }
     
        public void loginCallback(String accessToken){
            callerActivity.onJSFinish(accessToken);
        }
     
    }

	@Override
	public void onJSStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJSFinish(String result) {
        String url = "http://www.buggycoder.com:3000/users/me?accessToken=" + result;
        Toast.makeText(aq.getContext(), "Requesting URL: " + url, Toast.LENGTH_LONG).show();
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                        if(json != null){
                                Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
                        }else{
                                Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
                        }
                }
        });
	}
}

interface JSCallbackReceiver {
	void onJSStart();
	void onJSFinish(String result);
}
