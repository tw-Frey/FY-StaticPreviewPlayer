package tw.idv.fy.widget.staticpreview;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.FloatRange;
import android.view.ViewGroup;

import static android.arch.lifecycle.Lifecycle.Event.ON_DESTROY;

public interface IPreviewManager extends LifecycleObserver {
    /**
     * 原始影片長度
     */
    void setVideoDuration(int videoDuration);
    /**
     * 載入預覽資料
     */
    void load();
    /**
     * 顯示 Preview
     */
    void show(ViewGroup host);
    /**
     * 隱藏 Preview
     */
    void hide(ViewGroup host);
    /**
     *  顯使指定百分比的縮圖
     */
    void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent);
    /**
     * 釋放預覽資料
     */
    @OnLifecycleEvent(ON_DESTROY)
    void dispose();
}
