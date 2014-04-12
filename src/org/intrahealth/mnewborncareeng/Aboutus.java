package org.intrahealth.mnewborncareeng;

import org.intrahealth.mnewborncareeng.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;



public class Aboutus extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aboutus);
        WebView mywv=(WebView)findViewById(R.id.mywv);
        mywv.loadUrl("file:///android_asset/aboutus.html");
    }
}
