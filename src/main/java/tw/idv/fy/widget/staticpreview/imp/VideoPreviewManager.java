package tw.idv.fy.widget.staticpreview.imp;

import android.media.MediaPlayer;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import tw.idv.fy.widget.staticpreview.IPreviewManager;
import tw.idv.fy.widget.staticpreview.R;

public class VideoPreviewManager implements IPreviewManager, SurfaceHolder.Callback {

    private static VideoPreviewManager singleton;

    public static VideoPreviewManager getInstance(String uri) {
        if (singleton == null) {
            synchronized (VideoPreviewManager.class) {
                if (singleton == null) {
                    singleton = new VideoPreviewManager(uri);
                }
            }
        }
        return singleton;
    }

    private VideoPreviewManager(String uri) {
        mUri = uri;
        preview_id = View.generateViewId();
    }

    /**
     * 顯示器 id
     */
    @IdRes
    private final int preview_id;

    /**
     * 預覽資料來源
     */
    private final String mUri;

    /**
     * 預覽資料是否載入完畢
     */
    private boolean isPrepared = false;

    /**
     * 預覽資料播放器
     */
    private MediaPlayer mMediaPlayer = null;

    /**
     * 預覽資料長度(單位:毫秒)
     */
    private int mDuration = -1;
    /**
     * 載入預覽資料
     */
    @Override
    public void load() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        //mediaPlayer.setOnSeekCompleteListener(MediaPlayer::pause);
        mediaPlayer.setOnPreparedListener(mp -> {
            mDuration = mp.getDuration();
            mMediaPlayer = mp;
            mMediaPlayer.start();
            mMediaPlayer.pause();
            isPrepared = true;
        });
        try {
            mediaPlayer.setDataSource(mUri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 顯示 Preview
     */
    @Override
    public void show(ViewGroup host) {
        if (!isPrepared || host == null) return;
        if (host.findViewById(preview_id) == null) {
            View preview = LayoutInflater.from(host.getContext()).inflate(R.layout.layout_video_preview, host, false);
            preview.setId(preview_id);
            host.addView(preview);
            if (preview instanceof SurfaceView) {
                ((SurfaceView) preview).getHolder().addCallback(this);
                ((SurfaceView) preview).setZOrderOnTop(true);
            }
        }
    }

    /**
     * 隱藏 Preview
     */
    @Override
    public void hide(ViewGroup host) {
        if (!isPrepared || host == null) return;
        if (mMediaPlayer != null) mMediaPlayer.setDisplay(null);
        View preview = host.findViewById(preview_id);
        host.removeView(preview);
        if (preview instanceof SurfaceView) {
            ((SurfaceView) preview).getHolder().removeCallback(this);
        }
    }

    /**
     * 顯使指定百分比的縮圖
     */
    @Override
    public void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent) {
        if (!isPrepared || mMediaPlayer == null || mDuration < 0) return;
        mMediaPlayer.seekTo((int) (percent * mDuration));
    }

    /**
     * 釋放預覽資料
     */
    @Override
    public void dispose() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        isPrepared = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(null);
    }
}
