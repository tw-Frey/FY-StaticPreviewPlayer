package tw.idv.fy.widget.staticpreview.imp;

import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import tw.idv.fy.widget.staticpreview.IPreviewManager;
import tw.idv.fy.widget.staticpreview.R;

@SuppressWarnings("unused")
public class TextPreviewManager implements IPreviewManager {

    private static TextPreviewManager singleton;

    public static TextPreviewManager getInstance() {
        if (singleton == null) {
            synchronized (TextPreviewManager.class) {
                if (singleton == null) {
                    singleton = new TextPreviewManager();
                }
            }
        }
        return singleton;
    }

    private TextPreviewManager() {
        preview_id = View.generateViewId();
    }

    /**
     * 顯示器 id
     */
    @IdRes
    private final int preview_id;

    /**
     * 預覽資料是否載入完畢
     */
    private boolean isPrepared = false;

    /**
     * 原始影片長度
     */
    @Override
    public void setVideoDuration(int videoDuration){}

    /**
     * 載入預覽資料
     */
    @Override
    public void load() {
        isPrepared = true;
    }

    /**
     * 顯示 Preview
     */
    @Override
    public void show(ViewGroup host) {
        if (host == null) return;
        if (host.findViewById(preview_id) == null) {
            View preview = LayoutInflater.from(host.getContext()).inflate(R.layout.layout_text_preview, host, false);
            preview.setId(preview_id);
            host.addView(preview);
        }
    }

    /**
     * 隱藏 Preview
     */
    @Override
    public void hide(ViewGroup host) {
        if (host == null) return;
        View preview = host.findViewById(preview_id);
        host.removeView(preview);
    }

    /**
     * 顯使指定百分比的縮圖
     */
    @Override
    public void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent) {
        TextView preview = host.findViewById(preview_id);
        if (preview == null) return;
        if (!isPrepared) {
            preview.setText("準備中");
            return;
        }
        preview.setText(String.format(Locale.TAIWAN, "%.0f%%", 100 * percent));
    }

    /**
     * 釋放預覽資料
     */
    @Override
    public void dispose() {
        isPrepared = false;
    }
}
