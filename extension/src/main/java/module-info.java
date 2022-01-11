import io.xpipe.extension.DataSourceProvider;
import io.xpipe.extension.SupportedApplicationProvider;

module io.xpipe.extension {
    requires io.xpipe.core;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires io.xpipe.fxcomps;
    requires org.apache.commons.collections4;
    requires static lombok;

    exports io.xpipe.extension;
    exports io.xpipe.extension.comp;
    exports io.xpipe.extension.event;

    uses DataSourceProvider;
    uses SupportedApplicationProvider;
    uses io.xpipe.extension.I18n;
    uses io.xpipe.extension.event.EventHandler;
}