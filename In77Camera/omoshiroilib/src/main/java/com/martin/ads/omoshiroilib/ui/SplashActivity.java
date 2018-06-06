package com.martin.ads.omoshiroilib.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeData;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static int REQUEST_CAMERA = 1;
    private static int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.content_splash);

        int pos=(int)(Math.random()*Integer.MAX_VALUE)%3;
        ImageView bg= (ImageView) findViewById(R.id.back_ground);
        if(pos==0) bg.setImageResource(R.drawable.landing1);
        else if(pos==1) bg.setImageResource(R.drawable.landing2);
        else if(pos==2) bg.setImageResource(R.drawable.landing3);

        setLsnForImageView(R.id.take, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,CameraPreviewActivity.class));
            }
        });

        setLsnForImageView(R.id.choose, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_GALLERY);
            }
        });

        setLsnForImageView(R.id.collage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SplashActivity.this,"不存在的~",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLsnForImageView(int id, View.OnClickListener listener){
        ImageView view= (ImageView) findViewById(id);
        view.setOnClickListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                Log.d(TAG, "onActivityResult: "+bitmap.getWidth()+" "+bitmap.getHeight());
            }else if(requestCode==REQUEST_GALLERY){
                Uri selectImageUri  = data.getData();
                String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectImageUri,filePathColumn,null,null,null);
                String pirPath = null;
                while(cursor.moveToNext()){
                    pirPath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                }
                cursor.close();
                Log.d(TAG, "onActivityResult: "+pirPath);
                Toast.makeText(SplashActivity.this,"This function is not quiet ready.",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(SplashActivity.this,EditActivity.class);
                intent.putExtra(HardCodeData.IMAGE_PATH,pirPath);
                startActivity(intent);
            }
        }
    }
}
