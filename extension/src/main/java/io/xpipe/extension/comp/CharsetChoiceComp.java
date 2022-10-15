package io.xpipe.extension.comp;

import io.xpipe.core.charsetter.StreamCharset;
import io.xpipe.extension.I18n;
import io.xpipe.extension.util.CustomComboBoxBuilder;
import io.xpipe.fxcomps.SimpleComp;
import javafx.beans.property.Property;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class CharsetChoiceComp extends SimpleComp {

    private final Property<StreamCharset> charset;

    public CharsetChoiceComp(Property<StreamCharset> charset) {
        this.charset = charset;
    }

    @Override
    protected Region createSimple() {
        var builder = new CustomComboBoxBuilder<>(
                charset,
                streamCharset -> {
                    return new Label(streamCharset.getCharset().displayName()
                            + (streamCharset.hasByteOrderMark() ? " (BOM)" : ""));
                },
                new Label(I18n.get("extension.none")),
                null);
        builder.addHeader(I18n.get("extension.common"));
        for (var e : StreamCharset.COMMON) {
            builder.add(e);
        }

        builder.addHeader(I18n.get("extension.other"));
        builder.addFilter((charset, filter) -> {
            return charset.getCharset().displayName().contains(filter);
        });
        for (var e : StreamCharset.RARE) {
            builder.add(e);
        }
        return builder.build();
    }
}
