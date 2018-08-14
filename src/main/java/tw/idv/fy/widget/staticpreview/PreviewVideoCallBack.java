package tw.idv.fy.widget.staticpreview;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import tw.idv.fy.widget.staticpreview.convert.IPreviewUriAdapter;
import tw.idv.fy.widget.staticpreview.imp.ImagePreviewManager;
import tw.idv.fy.widget.staticpreview.imp.VideoPreviewManager;

@SuppressWarnings("WeakerAccess")
public class PreviewVideoCallBack extends GSYSampleCallBack implements SeekBar.OnSeekBarChangeListener {

    @Nullable
    private final IPreviewUriAdapter mPreviewUriAdapter;
    @NonNull
    private final IPreviewManager mPreviewManager;
    @Nullable
    private GSYBaseVideoPlayer mPlayer;
    @Nullable
    private SeekBar mPlayerProgressBar;

    private boolean isPrepared = false;

    public static void init(@NonNull IPreviewManager previewManager, @NonNull GSYBaseVideoPlayer player, @NonNull IPreviewUriAdapter previewUriAdapter) {
        new PreviewVideoCallBack(previewManager, player, previewUriAdapter);
    }

    @Deprecated
    public static void init(@NonNull IPreviewManager previewManager, @NonNull GSYBaseVideoPlayer player) {
        new PreviewVideoCallBack(previewManager, player);
    }

    private PreviewVideoCallBack(@NonNull IPreviewManager previewManager, @NonNull GSYBaseVideoPlayer player, @Nullable IPreviewUriAdapter previewUriAdapter) {
        mPreviewUriAdapter = previewUriAdapter;
        mPreviewManager = previewManager;
        setPlayer(player);
    }

    private PreviewVideoCallBack(@NonNull IPreviewManager previewManager, @NonNull GSYBaseVideoPlayer player) {
        this(previewManager, player, null);
    }

    protected void loadPreviewUri(@Nullable String url) {
        if (url == null || mPreviewUriAdapter == null) return;
        Uri previewUri = mPreviewUriAdapter.apply(url);
        if (mPreviewManager instanceof VideoPreviewManager) {
            ((VideoPreviewManager) mPreviewManager).setDataSource(previewUri);
        } else if (mPreviewManager instanceof ImagePreviewManager) {
            ((ImagePreviewManager) mPreviewManager).setBaseUri(previewUri);
        }
        mPreviewManager.load();
    }

    @Override
    public void onPrepared(String url, Object... objects) {
        loadPreviewUri(url);
        if (mPlayer == null || mPlayerProgressBar == null) return;
        mPreviewManager.setVideoDuration(mPlayer.getDuration());
        mPlayerProgressBar.setEnabled(true);
        isPrepared = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPlayer == null) return;
        mPlayer.onProgressChanged(seekBar, progress, fromUser);
        mPreviewManager.seekTo(mPlayer, progress / 100f);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mPlayer == null) return;
        mPlayer.onStartTrackingTouch(seekBar);
        mPreviewManager.show(mPlayer);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlayer == null) return;
        mPlayer.onStopTrackingTouch(seekBar);
        mPreviewManager.hide(mPlayer);
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {
        loadPreviewUri(url);
        if (objects.length > 1 && objects[1] instanceof GSYBaseVideoPlayer) {
            setPlayer((GSYBaseVideoPlayer) objects[1]);
        }
    }

    @Override
    public void onQuitFullscreen(String url, Object... objects) {
        loadPreviewUri(url);
        if (objects.length > 1 && objects[1] instanceof GSYBaseVideoPlayer) {
            setPlayer((GSYBaseVideoPlayer) objects[1]);
        }
    }

    private void setPlayer(@NonNull GSYBaseVideoPlayer newPlayer) {
        mPlayer = newPlayer;
        mPlayer.setVideoAllCallBack(this);
        mPlayerProgressBar = newPlayer.findViewById(R.id.progress);
        mPlayerProgressBar.setOnSeekBarChangeListener(this);
        mPlayerProgressBar.setEnabled(isPrepared);
    }
}
