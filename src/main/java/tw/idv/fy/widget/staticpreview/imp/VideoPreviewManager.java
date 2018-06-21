package tw.idv.fy.widget.staticpreview.imp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import tw.idv.fy.widget.staticpreview.IPreviewManager;
import tw.idv.fy.widget.staticpreview.R;

public class VideoPreviewManager implements IPreviewManager, SurfaceHolder.Callback {

    private static VideoPreviewManager singleton;

    public static VideoPreviewManager getInstance() {
        if (singleton == null) {
            synchronized (VideoPreviewManager.class) {
                if (singleton == null) {
                    singleton = new VideoPreviewManager();
                }
            }
        }
        return singleton;
    }

    private VideoPreviewManager() {
        preview_container_id = View.generateViewId();
    }

    /**
     * 顯示器 id
     */
    @IdRes
    private final int preview_container_id;

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
     *  設定播放器
     */
    public void setDataSource(Context context, Uri mUri) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mDuration = mp.getDuration();
                mMediaPlayer.start();
                mMediaPlayer.pause();
                isPrepared = true;
            });
            mMediaPlayer.setDataSource(context, mUri);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 載入預覽資料
     */
    @Override
    public void load() {
        try {
            mMediaPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 顯示 Preview
     */
    @Override
    public void show(ViewGroup host) {
        if (!isPrepared || host == null) return;
        if (host.findViewById(preview_container_id) == null) {
            View preview_container = LayoutInflater.from(host.getContext()).inflate(R.layout.layout_video_preview, host, false);
            preview_container.setId(preview_container_id);
            host.addView(preview_container);
            if (preview_container instanceof SurfaceView) {
                ((SurfaceView) preview_container).getHolder().addCallback(this);
                ((SurfaceView) preview_container).setZOrderOnTop(true);
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
        View preview = host.findViewById(preview_container_id);
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
        if (mMediaPlayer == null) return;
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
