package tw.idv.fy.widget.staticpreview.time;

import android.support.annotation.Nullable;

import tw.idv.fy.widget.staticpreview.convert.IConverter;

public interface IStamp {

    /**
     *  浮點數 轉換 長整數 再轉換 字串
     */
    default String convert(float f) {
        return convert((long) f);
    }

    /**
     *  整數 轉換 字串
     */
    default String convert(int d) {
        return convert((long) d);
    }

    /**
     *  長整數 轉換 字串
     */
    default String convert(long ms) {
        return Converter.Get().convert(ms);
    }

    /**
     *  設定毫秒樣式轉換器
     */
    default void setConverter(@Nullable IConverter converter) {
        Converter.Set(converter);
    }

    class Converter {
        /*
            TODO: 思考如何不要共用 IConverter
         */
        private static IConverter mConverter = IConverter.DEFAULT;

        private static IConverter Get() {
            return mConverter;
        }

        private static void Set(@Nullable IConverter converter){
            mConverter = converter != null ? converter : IConverter.DEFAULT;
        }
    }
}
