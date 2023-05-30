package io.xpipe.app.browser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.xpipe.app.core.AppCache;
import io.xpipe.app.storage.DataStorage;
import io.xpipe.core.util.JacksonMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@AllArgsConstructor
@Getter
@JsonSerialize(using = OpenFileSystemSavedState.Serializer.class)
@JsonDeserialize(using = OpenFileSystemSavedState.Deserializer.class)
public class OpenFileSystemSavedState {

    public static class Serializer extends StdSerializer<OpenFileSystemSavedState> {

        protected Serializer() {
            super(OpenFileSystemSavedState.class);
        }

        @Override
        public void serialize(OpenFileSystemSavedState value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            var node = JsonNodeFactory.instance.objectNode();
            node.set("recentDirectories", JacksonMapper.getDefault().valueToTree(value.getRecentDirectories()));
            gen.writeTree(node);
        }
    }

    public static class Deserializer extends StdDeserializer<OpenFileSystemSavedState> {

        protected Deserializer() {
            super(OpenFileSystemSavedState.class);
        }

        @Override
        @SneakyThrows
        public OpenFileSystemSavedState deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JacksonException {
            var tree = (ObjectNode) JacksonMapper.getDefault().readTree(p);
            JavaType javaType = JacksonMapper.getDefault().getTypeFactory().constructCollectionLikeType(List.class, RecentEntry.class);
            List<RecentEntry> recentDirectories = JacksonMapper.getDefault().treeToValue(tree.remove("recentDirectories"), javaType);
            return new OpenFileSystemSavedState(null, FXCollections.observableList(recentDirectories));
        }
    }

    static OpenFileSystemSavedState loadForStore(OpenFileSystemModel model) {
        var storageEntry = DataStorage.get()
                .getStoreEntryIfPresent(model.getStore())
                .map(entry -> entry.getUuid())
                .orElse(UUID.randomUUID());
        var state = AppCache.get("fs-state-" + storageEntry, OpenFileSystemSavedState.class, () -> {
            return new OpenFileSystemSavedState();
        });
        state.setModel(model);
        return state;
    }

    @Value
    @Jacksonized
    @Builder
    public static class RecentEntry {

        String directory;
        Instant time;
    }

    @Setter
    private OpenFileSystemModel model;
    private String lastDirectory;
    @NonNull
    private ObservableList<RecentEntry> recentDirectories;

    public OpenFileSystemSavedState(String lastDirectory, @NonNull ObservableList<RecentEntry> recentDirectories) {
        this.lastDirectory = lastDirectory;
        this.recentDirectories = recentDirectories;
    }

    private static final Timer TIMEOUT_TIMER = new Timer(true);
    private static final int STORED = 10;

    public OpenFileSystemSavedState() {
        lastDirectory = null;
        recentDirectories = FXCollections.observableList(new ArrayList<>(STORED));
    }

    public void save() {
        if (model == null) {
            return;
        }

        var storageEntry = DataStorage.get().getStoreEntryIfPresent(model.getStore());
        storageEntry.ifPresent(entry -> AppCache.update("fs-state-" + entry.getUuid(), this));
    }

    public void cd(String dir) {
        if (dir == null) {
            return;
        }

        lastDirectory = dir;
        TIMEOUT_TIMER.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        // Synchronize with platform thread
                        Platform.runLater(() -> {
                            if (model.isClosed()) {
                                return;
                            }

                            if (Objects.equals(lastDirectory, dir)) {
                                updateRecent(dir);
                                save();
                            }
                        });
                    }
                },
                200);
    }

    private void updateRecent(String dir) {
        recentDirectories.removeIf(recentEntry -> Objects.equals(recentEntry.directory, dir));

        var o = new RecentEntry(dir, Instant.now());
        if (recentDirectories.size() < STORED) {
            recentDirectories.add(0, o);
        } else {
            recentDirectories.remove(recentDirectories.size() - 1);
            recentDirectories.add(o);
        }
    }
}
