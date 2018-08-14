package tw.idv.fy.widget.staticpreview.convert;

import android.net.Uri;

public interface IPreviewUriAdapter {
    Uri apply(String playerUrl);
}
