package io.xpipe.core.data.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TupleNode extends DataStructureNode {

    public static Builder builder() {
        return new Builder();
    }

    public static TupleNode of(List<DataStructureNode> nodes) {
        if (nodes == null) {
            throw new IllegalArgumentException("Nodes must be not null");
        }

        return new NoKeyTupleNode(true, nodes);
    }

    public static TupleNode of(List<String> names, List<DataStructureNode> nodes) {
        if (names == null) {
            throw new IllegalArgumentException("Names must be not null");
        }
        if (nodes == null) {
            throw new IllegalArgumentException("Nodes must be not null");
        }
        if (names.size() != nodes.size()) {
            throw new IllegalArgumentException("Names and nodes must have the same length");
        }

        return new SimpleTupleNode(true, names, nodes);
    }

    public static TupleNode of(boolean mutable, List<String> names, List<DataStructureNode> nodes) {
        if (names == null) {
            throw new IllegalArgumentException("Names must be not null");
        }
        if (nodes == null) {
            throw new IllegalArgumentException("Nodes must be not null");
        }
        return new SimpleTupleNode(mutable, names, nodes);
    }

    public final boolean isTuple() {
        return true;
    }

    protected abstract String getIdentifier();

    protected void checkMutable() {
        if (!isMutable()) {
            throw new UnsupportedOperationException("Tuple node is immutable");
        }
    }

    @Override
    public abstract TupleNode mutableCopy();

    @Override
    public abstract TupleNode immutableView();

    @Override
    public String toString(int indent) {
        var is = " ".repeat(indent);
        var start = "(" + getIdentifier() + ") {\n";
        var kvs = getKeyValuePairs().stream().map(kv -> {
            if (kv.key() == null) {
                return is + " " + kv.value().toString(indent + 1) + "\n";
            } else {
                return is + " " + kv.key() + "=" + kv.value().toString(indent + 1) + "\n";
            }
        }).collect(Collectors.joining());
        var end = is + "}";
        return start + kvs + end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleNode that)) return false;
        return getKeyNames().equals(that.getKeyNames()) && getNodes().equals(that.getNodes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyNames(), getNodes());
    }

    public static class Builder {

        private final List<KeyValue> entries = new ArrayList<>();

        public Builder add(String name, DataStructureNode node) {
            Objects.requireNonNull(node);
            entries.add(new KeyValue(name, node));
            return this;
        }

        public Builder add(DataStructureNode node) {
            Objects.requireNonNull(node);
            entries.add(new KeyValue(null, node));
            return this;
        }

        public TupleNode build() {
            boolean hasKeys = entries.stream().anyMatch(kv -> kv.key() != null);
            return hasKeys ? TupleNode.of(
                    entries.stream().map(KeyValue::key).toList(),
                    entries.stream().map(KeyValue::value).toList()) :
                    TupleNode.of(entries.stream().map(KeyValue::value).toList());
        }
    }
}
