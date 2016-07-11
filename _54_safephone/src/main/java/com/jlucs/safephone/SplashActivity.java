package com.jlucs.safephone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		TitanicTextView tv = (TitanicTextView) findViewById(R.id.titanic_tv);
        // set fancy typeface
        tv.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
        // start animation
        final Titanic titanic=new Titanic();
        titanic.start(tv);
        startAnimation();
        copyDb();
	}

	private void copyDb() {
		File desFile =new File(getFilesDir(), "address.db");
		if (!desFile.exists()) {
			InputStream inputStream = null;
			FileOutputStream outputStream=null;
			try {
				outputStream = new FileOutputStream(desFile);
				inputStream = getAssets().open("address.db");
				int len=0;
				byte[] bs=new byte[1024];
				while((len=inputStream.read(bs))!=-1){
					outputStream.write(bs, 0, len);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					inputStream.close();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

	private void startAnimation() {
		LinearLayout linearLayout=(LinearLayout) findViewById(R.id.ll_splash);
		AlphaAnimation animation=new AlphaAnimation(0.3f, 1);
		animation.setDuration(10000);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				startActivity(new Intent(SplashActivity.this,LoginActivity.class));
				finish();
			}
		});
		linearLayout.startAnimation(animation);
	}
}
