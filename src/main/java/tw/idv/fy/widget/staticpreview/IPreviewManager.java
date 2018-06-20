package tw.idv.fy.widget.staticpreview;

import android.support.annotation.FloatRange;
import android.view.ViewGroup;

public interface IPreviewManager {
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
    void dispose();
}
