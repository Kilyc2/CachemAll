package com.kiliancerdan.cachemall.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.kiliancerdan.cachemall.R;
import com.kiliancerdan.cachemall.cache.CacheMethod;
import com.kiliancerdan.cachemall.cache.DiskLruCacheMethod;
import com.kiliancerdan.cachemall.cache.SaveInMemoryMethod;
import com.kiliancerdan.cachemall.cache.VideoCacheMethod;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.cache_methods) Spinner cacheMethods;
    @BindView(R.id.urlVideo) TextView urlVideo;
    @BindView(R.id.play) Button playVideo;
    @BindView(R.id.video) VideoView video;
    @BindView(R.id.chronometer) Chronometer chrono;
    @BindView(R.id.message) TextView message;
    CacheMethod cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        video.setMediaController(new MediaController(this));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cache_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cacheMethods.setAdapter(adapter);
        cacheMethods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cache = getCache(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.cache)
    public void cacheVideo() {
        String url = urlVideo.getText().toString();
        message.setText(getString(R.string.start_cache));
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.start();
        new CacheAsyncTask(url).execute();
    }

    @OnClick(R.id.play)
    public void playVideo() {
        String path = cache.getVideoPath();
        if (!path.isEmpty()) {
            video.setVideoPath(path);
            video.setVisibility(View.VISIBLE);
            video.start();
        }
    }

    @OnClick(R.id.clear)
    public void clearCache() {
        cache.clearCache(getCacheDir());
        video.stopPlayback();
        video.setVisibility(View.INVISIBLE);
        playVideo.setEnabled(false);
    }

    private CacheMethod getCache(int position) {
        CacheMethod cacheMethod;
        switch (position) {
            case 0:
                cacheMethod = DiskLruCacheMethod.getInstance();
                break;
            case 1:
                cacheMethod = VideoCacheMethod.getInstance();
                break;
            default:
                cacheMethod = SaveInMemoryMethod.getInstance();
        }
        cacheMethod.setCacheDirectory(getCacheDir());
        return cacheMethod;
    }

    protected void finishCache() {
        chrono.stop();
        playVideo.setEnabled(true);
        message.setText(getString(R.string.video_cached));
    }

    protected void errorCache() {
        chrono.stop();
        message.setText(getString(R.string.error_cache));
    }

    class CacheAsyncTask extends AsyncTask<Void, Void, Boolean> {

        String url;

        CacheAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            cache.setUrlVideo(url);
            return cache.cacheFile();
        }

        @Override
        protected void onPostExecute(Boolean isVideoCached) {
            if (isVideoCached) {
                finishCache();
            } else {
                errorCache();
            }
        }
    }
}
