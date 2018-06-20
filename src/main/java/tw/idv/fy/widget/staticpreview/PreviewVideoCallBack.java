package tw.idv.fy.widget.staticpreview;

import android.widget.SeekBar;

import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

public class PreviewVideoCallBack extends GSYSampleCallBack implements SeekBar.OnSeekBarChangeListener {

    private final IPreviewManager mPreviewManager;
    private GSYBaseVideoPlayer mPlayer;
    private SeekBar mPlayerProgressBar;
    private boolean isPrepared = false;

    public static void init(IPreviewManager previewManager, GSYBaseVideoPlayer player) {
        new PreviewVideoCallBack(previewManager, player);
    }

    private PreviewVideoCallBack(IPreviewManager previewManager, GSYBaseVideoPlayer player) {
        mPreviewManager = previewManager;
        setPlayer(player);
    }

    @Override
    public void onPrepared(String url, Object... objects) {
        if (mPlayerProgressBar == null) return;
        mPlayerProgressBar.setEnabled(true);
        isPrepared = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPlayer == null || mPreviewManager == null) return;
        mPlayer.onProgressChanged(seekBar, progress, fromUser);
        mPreviewManager.seekTo(mPlayer, progress / 100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mPlayer == null || mPreviewManager == null) return;
        mPlayer.onStartTrackingTouch(seekBar);
        mPreviewManager.show(mPlayer);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlayer == null || mPreviewManager == null) return;
        mPlayer.onStopTrackingTouch(seekBar);
        mPreviewManager.hide(mPlayer);
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {
        if (objects.length > 1 && objects[1] instanceof GSYBaseVideoPlayer) {
            setPlayer((GSYBaseVideoPlayer) objects[1]);
        }
    }

    @Override
    public void onQuitFullscreen(String url, Object... objects) {
        if (objects.length > 1 && objects[1] instanceof GSYBaseVideoPlayer) {
            setPlayer((GSYBaseVideoPlayer) objects[1]);
        }
    }

    private void setPlayer(GSYBaseVideoPlayer newPlayer) {
        mPlayer = newPlayer;
        mPlayer.setVideoAllCallBack(this);
        mPlayerProgressBar = newPlayer.findViewById(R.id.progress);
        mPlayerProgressBar.setOnSeekBarChangeListener(this);
        mPlayerProgressBar.setEnabled(isPrepared);
    }
}
