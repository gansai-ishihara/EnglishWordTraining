package com.example.tsuyoshi.englishwordtraining;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by tsuyoshi on 2018/03/04.
 */

public class SplashActivity extends Activity {

    private Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        //タイトル画像を背景色と合わせる
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.splash2re63);
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        int c = bmp.getPixel(0, 0);
        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),Bitmap.Config.ARGB_8888 );
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                if( pixels[x + y * bmp.getWidth()]== c){ pixels[x + y * bmp.getWidth()] = 0x0000ff00; }
            }
        }
        bitmap.eraseColor(Color.argb(0, 0, 0, 0));
        bitmap.setPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        ImageView iv = (ImageView)findViewById(R.id.img);
        iv.setImageBitmap(bitmap);

        //スプラッシュ画像を2000ミリ秒表示する。
        handler.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable{
        public void run(){
            //インテントを生成し、遷移先のアクティビティクラスを指定する。
            Intent intent = new Intent(getApplication(),MainActivity.class);
            //次のアクティビティの起動
            startActivity(intent);
            //スプラッシュの終了。
            SplashActivity.this.finish();
        }
    }
}
