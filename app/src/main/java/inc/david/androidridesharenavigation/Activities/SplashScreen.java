package inc.david.androidridesharenavigation.Activities;



import inc.david.androidridesharenavigation.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;




public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_splash_screen);


        //just lets the screen sleep for 3 seconds adn then launches the main acitivity.  This is the first activity to launch
        Thread sleepThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        sleepThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}