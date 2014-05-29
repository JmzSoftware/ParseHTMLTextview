package com.jmz.software.parsehtml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

@SuppressLint("NewApi") public class MainActivity extends Activity {

	private TextView textView;
	String html = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		html = "<p> Heading 1</p> <p> Heading 2</p> <p><a href=\"http://www.jmzsoftware.com\">"
				+ "<img src=\"http://jmzsoftware.com/wp-content/uploads/2014/05/jmzsoftware1-e1400169340524.png\" >"
				+ "</img></a></p>";
        
        textView = (TextView)this.findViewById(R.id.textView1);
        URLImageParser p = new URLImageParser(textView);
        Spanned htmlSpan = Html.fromHtml(html, p, null);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setClickable(true);
        textView.setText(htmlSpan);

	}

	@SuppressWarnings("deprecation")
	public class URLDrawable extends BitmapDrawable {
	    protected Drawable drawable;

	    @Override
	    public void draw(Canvas canvas) {
	        if(drawable != null) {
	            drawable.draw(canvas);
	        }
	    }
	}
	
	public class URLImageParser implements ImageGetter {
	    TextView c;
	    TextView container = textView;
	    URLDrawable urlDrawable;
	   
	    public URLImageParser(TextView c) {
	        this.c = c;
	    }

	    public Drawable getDrawable(String source) {
	    	urlDrawable = new URLDrawable();
	    	
	        ImageGetterAsyncTask asyncTask = 
	            new ImageGetterAsyncTask( urlDrawable);

	        asyncTask.execute(source);

	        return urlDrawable;
	    }

	    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
	        URLDrawable urlDrawable;

	        public ImageGetterAsyncTask(URLDrawable d) {
	            this.urlDrawable = d;
	        }

	        @Override
	        protected Drawable doInBackground(String... params) {
	            String source = params[0];
	            return fetchDrawable(source);
	        }

	        @Override
	        protected void onPostExecute(Drawable result) {  

	        	float multiplier = (float)200 / (float)result.getIntrinsicWidth();
	            int width = (int)(result.getIntrinsicWidth() * multiplier);
	            int height = (int)(result.getIntrinsicHeight() * multiplier);
	            urlDrawable.setBounds(0, 0, width, height);
	            urlDrawable.drawable = result;  
	            URLImageParser.this.container.invalidate();
	            URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight() 
	            		+ result.getIntrinsicHeight()));	            
	        }
	        
	        public Drawable fetchDrawable(String urlString) {
	            try {
	                InputStream is = fetch(urlString);
	                Drawable drawable = Drawable.createFromStream(is, "src");
	                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
	                        + drawable.getIntrinsicHeight()); 
	                return drawable;
	            } catch (Exception e) {
	                return null;
	            } 
	        }

	        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet request = new HttpGet(urlString);
	            HttpResponse response = httpClient.execute(request);
	            return response.getEntity().getContent();
	        }
	    }
	}
}