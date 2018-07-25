package tw.idv.fy.widget.staticpreview.convert;

public interface IConverter {

    IConverter DEFAULT = String::valueOf;

    String convert(long ms);
}
