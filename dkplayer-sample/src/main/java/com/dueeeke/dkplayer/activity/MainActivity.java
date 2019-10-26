package com.dueeeke.dkplayer.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.dkplayer.R;
import com.dueeeke.dkplayer.activity.api.ApiActivity;
import com.dueeeke.dkplayer.activity.api.PlayerActivity;
import com.dueeeke.dkplayer.activity.extend.ExtendActivity;
import com.dueeeke.dkplayer.activity.list.ListActivity;
import com.dueeeke.dkplayer.activity.pip.PIPDemoActivity;
import com.dueeeke.dkplayer.util.PIPManager;
import com.dueeeke.dkplayer.util.Utils;
import com.dueeeke.dkplayer.util.cache.ProxyVideoCacheManager;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;

import java.lang.reflect.Field;

public class MainActivity extends BaseActivity {

    private EditText editText;
    private boolean isLive;

    private TextView mCurrentPlayer;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean enableBack() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        editText = findViewById(R.id.et);
        mCurrentPlayer = findViewById(R.id.curr_player);

        Object playerFactory = Utils.getCurrentPlayerFactory();
        String msg = getString(R.string.str_current_player);
        if (playerFactory instanceof IjkPlayerFactory) {
            mCurrentPlayer.setText(msg + "IjkPlayer");
        } else {
            mCurrentPlayer.setText(msg + "MediaPlayer");
        }

        ((RadioGroup) findViewById(R.id.rg)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.vod:
                    isLive = false;
                    break;
                case R.id.live:
                    isLive = true;
                    break;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.close_float_window:
                PIPManager.getInstance().stopFloatWindow();
                PIPManager.getInstance().reset();
                break;
            case R.id.clear_cache:
                if (ProxyVideoCacheManager.clearAllCache(this)) {
                    Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (itemId == R.id.ijk || itemId == R.id.exo || itemId == R.id.media) {
            //切换播放核心，不推荐这么做，我这么写只是为了方便测试
            VideoViewConfig config = VideoViewManager.getConfig();
            try {
                Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
                mPlayerFactoryField.setAccessible(true);
                PlayerFactory playerFactory = null;
                String msg = getString(R.string.str_current_player);
                switch (itemId) {
                    case R.id.ijk:
                        playerFactory = IjkPlayerFactory.create();
                        mCurrentPlayer.setText(msg + "IjkPlayer");
                        break;
                    case R.id.exo:
                        //playerFactory = ExoMediaPlayerFactory.create();
                        //mCurrentPlayer.setText(msg + "ExoPlayer");
                        break;
                    case R.id.media:
                        playerFactory = AndroidMediaPlayerFactory.create();
                        mCurrentPlayer.setText(msg + "MediaPlayer");
                        break;
                }
                mPlayerFactoryField.set(config, playerFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playOther(View view) {
        String url = editText.getText().toString();
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", isLive);
        startActivity(intent);
    }

    public void clearUrl(View view) {
        editText.setText("");
    }

    public void api(View view) {
        startActivity(new Intent(this, ApiActivity.class));
    }

    public void extend(View view) {
        startActivity(new Intent(this, ExtendActivity.class));
    }

    public void list(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void pip(View view) {
        startActivity(new Intent(this, PIPDemoActivity.class));
    }
}
