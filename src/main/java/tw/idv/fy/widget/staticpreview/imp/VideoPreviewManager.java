package tw.idv.fy.widget.staticpreview.imp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.FloatRange;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import tw.idv.fy.widget.staticpreview.R;

@SuppressWarnings("unused")
public class VideoPreviewManager extends BasePreviewManager implements SurfaceHolder.Callback {

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

    /**
     * 預覽資料是否載入完畢
     */
    private boolean isPrepared = false;

    /**
     * 預覽資料播放器
     */
    private MediaPlayer mMediaPlayer = null;

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
        super.show(host);
        if (host == null || !isPrepared) return;
        View preview_layout = host.findViewById(preview_layout_id);
        View preview_container, preview_surface;
        if (preview_layout != null && (preview_container = preview_layout.findViewById(R.id.preview_container)) instanceof ViewGroup) {
            preview_surface = LayoutInflater.from(host.getContext()).inflate(R.layout.layout_video_preview, (ViewGroup) preview_container, false);
            if (!(preview_surface instanceof SurfaceView)) return;
            ((SurfaceView) preview_surface).getHolder().addCallback(this);
            ((SurfaceView) preview_surface).setZOrderOnTop(true);
            ((ViewGroup) preview_container).addView(preview_surface);
        }
    }

    /**
     * 隱藏 Preview
     */
    @Override
    public void hide(ViewGroup host) {
        if (host != null) {
            View preview_layout = host.findViewById(preview_layout_id);
            View preview_surface;
            if (preview_layout != null && (preview_surface = preview_layout.findViewById(R.id.preview_surface)) instanceof SurfaceView) {
                ((SurfaceView) preview_surface).getHolder().removeCallback(this);
            }
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
        }
        super.hide(host);
    }

    /**
     * 顯使指定百分比的縮圖
     */
    @Override
    public void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent) {
        super.seekTo(host, percent);
        if (!isPrepared || mMediaPlayer == null) return;
        mMediaPlayer.seekTo((int) (mMediaPlayer.getDuration() * percent));
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
