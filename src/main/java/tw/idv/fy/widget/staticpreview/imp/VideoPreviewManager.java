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
import android.widget.TextView;

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
     * 原始影片長度(單位:秒)
     */
    private int mOriginVideoDuration = -1;

    /**
     *  設定播放器
     */
    public void setDataSource(Context context, Uri mUri) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mp -> {
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
     * 原始影片長度(單位:秒)
     */
    @Override
    public void setVideoDuration(int videoDuration){
        mOriginVideoDuration = videoDuration / 1000;
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
            View duration = preview_container.findViewById(R.id.preview_video_duration);
            if (duration instanceof TextView) {
                ((TextView) duration).setText(convert(mOriginVideoDuration));
            }
            View surface = preview_container.findViewById(R.id.preview_video_surface);
            if (surface instanceof SurfaceView) {
                ((SurfaceView) surface).getHolder().addCallback(this);
                ((SurfaceView) surface).setZOrderOnTop(true);
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
        View preview_container = host.findViewById(preview_container_id);
        host.removeView(preview_container);
        View surface = preview_container.findViewById(R.id.preview_video_surface);
        if (surface instanceof SurfaceView) {
            ((SurfaceView) surface).getHolder().removeCallback(this);
        }
    }

    /**
     * 顯使指定百分比的縮圖
     */
    @Override
    public void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent) {
        if (!isPrepared || mMediaPlayer == null) return;
        mMediaPlayer.seekTo((int) (mMediaPlayer.getDuration() * percent));
        View seek = host.findViewById(R.id.preview_video_seek);
        if (seek instanceof TextView) {
            ((TextView) seek).setText(convert(mOriginVideoDuration * percent));
        }
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

    /**
     *  浮點數 轉換 整數 再轉換 字串
     */
    protected String convert(float f) {
        return convert((long) f);
    }

    /**
     *  整數 轉換 字串
     */
    protected String convert(int d) {
        return convert((long) d);
    }

    /**
     *  長整數 轉換 字串
     */
    protected String convert(long l) {
        return String.valueOf(l);
    }

}
