package tw.idv.fy.widget.staticpreview.imp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import tw.idv.fy.widget.staticpreview.R;

@SuppressWarnings("unused")
public class ImagePreviewManager extends BasePreviewManager {

    private static ImagePreviewManager singleton;

    public static ImagePreviewManager getInstance() {
        if (singleton == null) {
            synchronized (ImagePreviewManager.class) {
                if (singleton == null) {
                    singleton = new ImagePreviewManager();
                }
            }
        }
        return singleton;
    }

    /**
     * 取得影片縮圖 API
     */
    private Uri mThumbUri;

    /**
     * Glide 請求建構子
     */
    private RequestBuilder<Drawable> requestBuilder;

    /**
     * preview thumb 呈現窗口
     */
    private ImageViewTarget preview_target;

    /**
     *  設定 影片縮圖 API
     */
    public void setBaseUri(Uri thumbUri) {
        mThumbUri = thumbUri;
    }

    @Override
    public void show(ViewGroup host) {
        super.show(host);
        if (host == null) return;
        View preview_layout = host.findViewById(preview_layout_id);
        View preview_container;
        if (preview_layout != null && (preview_container = preview_layout.findViewById(R.id.preview_container)) instanceof ViewGroup) {
            ImageView preview_image = (ImageView) LayoutInflater.from(host.getContext()).inflate(R.layout.layout_image_preview, (ViewGroup) preview_container, false);
            ((ViewGroup) preview_container).addView(preview_image);
            preview_target = new ImageViewTarget(preview_image);
        }
        // 建立 Glide 請求建構子
        requestBuilder = Glide.with(host).asDrawable();
    }

    @Override
    public void hide(ViewGroup host) {
        Glide.with(host).clear(preview_target);
        preview_target = null;
        requestBuilder = null;
        super.hide(host);
    }

    @Override
    public void seekTo(ViewGroup host, float percent) {
        super.seekTo(host, percent);
        if (host == null || preview_target == null) return;
        String seekToSecond = String.valueOf((int) (getVideoDuration() * percent));
        Uri nowUri = mThumbUri.buildUpon()
                .appendQueryParameter("second", seekToSecond)
                .build();
        // 載入 preview thumb
        requestBuilder.load(nowUri).into(preview_target);
    }

    @Override
    public void dispose() {
        requestBuilder = null;
        preview_target = null;
        mThumbUri = null;
    }

    private static class ImageViewTarget extends com.bumptech.glide.request.target.ImageViewTarget<Drawable> {

        private ImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void setDrawable(Drawable drawable) {
            if (drawable == null) return;
            super.setDrawable(drawable);
        }

        @Override
        protected void setResource(@Nullable Drawable resource) {
            setDrawable(resource);
        }
    }
}
