package tw.idv.fy.widget.staticpreview.imp;

import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tw.idv.fy.widget.staticpreview.IPreviewManager;
import tw.idv.fy.widget.staticpreview.R;
import tw.idv.fy.widget.staticpreview.time.IStamp;

public class BasePreviewManager implements IPreviewManager, IStamp {

    private static BasePreviewManager singleton;

    @SuppressWarnings("unused")
    public static BasePreviewManager getInstance() {
        if (singleton == null) {
            synchronized (BasePreviewManager.class) {
                if (singleton == null) {
                    singleton = new BasePreviewManager();
                }
            }
        }
        return singleton;
    }

    /*package*/ BasePreviewManager() {
        preview_layout_id = View.generateViewId();
    }

    /**
     * 顯示器 id
     */
    @IdRes
    /*package*/ final int preview_layout_id;

    /**
     * 原始影片長度(單位:秒)
     */
    private int mOriginVideoDuration = -1;

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
    public void load() {/* nothing to do */}

    /**
     * 顯示 Preview
     */
    @Override
    public void show(ViewGroup host) {
        if (host == null) return;
        if (host.findViewById(preview_layout_id) == null) {
            View preview_layout = LayoutInflater.from(host.getContext()).inflate(R.layout.layout_base_preview, host, false);
            preview_layout.setId(preview_layout_id);
            host.addView(preview_layout);
            View duration = preview_layout.findViewById(R.id.preview_duration);
            if (duration instanceof TextView) {
                ((TextView) duration).setText(convert(mOriginVideoDuration));
            }
        }
    }

    /**
     * 隱藏 Preview
     */
    @Override
    public void hide(ViewGroup host) {
        if (host == null) return;
        View preview_layout = host.findViewById(preview_layout_id);
        host.removeView(preview_layout);
    }

    /**
     * 顯使指定百分比的縮圖
     */
    @Override
    public void seekTo(ViewGroup host, @FloatRange(from = 0.0, to = 1.0) float percent) {
        if (host == null) return;
        View seek = host.findViewById(R.id.preview_seek);
        if (seek instanceof TextView) {
            ((TextView) seek).setText(convert(mOriginVideoDuration * percent));
        }
    }

    /**
     * 釋放預覽資料
     */
    @Override
    public void dispose() {/* nothing to do */}

}
