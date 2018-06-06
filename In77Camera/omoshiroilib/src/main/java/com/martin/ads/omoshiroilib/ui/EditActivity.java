package com.martin.ads.omoshiroilib.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeData;
import com.martin.ads.omoshiroilib.glessential.GLRootView;
import com.martin.ads.omoshiroilib.imgeditor.gl.GLWrapper;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private RecyclerView filterListView;
    private GLRootView glRootView;
    private GLWrapper glWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit);
        GlobalConfig.context=this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        setFilterList();

        setUpImage();
    }

    private void setUpImage() {
        String path=getIntent().getStringExtra(HardCodeData.IMAGE_PATH);

        final Bitmap bitmap= BitmapUtils.loadBitmapFromFile(path);
        glRootView.setAspectRatio(bitmap.getWidth(),bitmap.getHeight());
        bitmap.recycle();

        glWrapper.setFilePath(path);
    }

    private void setFilterList() {
        glRootView= (GLRootView) findViewById(R.id.camera_view);
        glWrapper= GLWrapper.newInstance()
                .setGlImageView(glRootView)
                .setContext(this)
                .init();

        FileUtils.upZipFile(this,"filter/thumbs/thumbs.zip",getFilesDir().getAbsolutePath());

        filterListView= (RecyclerView) findViewById(com.martin.ads.omoshiroilib.R.id.filter_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterListView.setLayoutManager(linearLayoutManager);

        List<FilterType> filterTypeList=new ArrayList<>();
        for(int i = 0; i< FilterType.values().length; i++){
            filterTypeList.add(FilterType.values()[i]);
            if(i==0) filterTypeList.add(FilterType.NONE);
        }
        FilterAdapter filterAdapter=new FilterAdapter(this,filterTypeList);
        filterListView.setAdapter(filterAdapter);
        filterAdapter.setOnFilterChangeListener(new FilterAdapter.OnFilterChangeListener() {
            @Override
            public void onFilterChanged(FilterType filterType) {
                glWrapper.switchLastFilterOfCustomizedFilters(filterType);
            }
        });
    }

}
