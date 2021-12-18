package io.xpipe.core.data.node;

import io.xpipe.core.data.DataStructureNode;
import io.xpipe.core.data.type.ArrayType;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
public class ArrayNode extends DataStructureNode {

    private final List<DataStructureNode> valueNodes;

    private ArrayNode(List<DataStructureNode> valueNodes) {
        this.valueNodes = valueNodes;
    }

    public static ArrayNode of(DataStructureNode... dsn) {
        return of(List.of(dsn));
    }

    public static ArrayNode of(List<DataStructureNode> valueNodes) {
        return new ArrayNode(valueNodes);
    }

    public static ArrayNode copyOf(List<DataStructureNode> valueNodes) {
        return new ArrayNode(new ArrayList<>(valueNodes));
    }

    @Override
    public DataStructureNode put(DataStructureNode node) {
        valueNodes.add(node);
        return this;
    }

    @Override
    public DataStructureNode set(int index, DataStructureNode node) {
        valueNodes.add(index, node);
        return this;
    }

    @Override
    public Stream<DataStructureNode> stream() {
        return Collections.unmodifiableList(valueNodes).stream();
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public int size() {
        return valueNodes.size();
    }

    @Override
    protected String getName() {
        return "array node";
    }

    @Override
    public String toString(int indent) {
        var content = valueNodes.stream().map(n -> n.toString(indent)).collect(Collectors.joining(", "));
        return "[" + content + "]";
    }

    @Override
    public ArrayType determineDataType() {
        return ArrayType.of(valueNodes.stream().map(DataStructureNode::determineDataType).toList());
    }

    @Override
    public DataStructureNode clear() {
        valueNodes.clear();
        return this;
    }

    @Override
    public DataStructureNode at(int index) {
        return valueNodes.get(index);
    }

    @Override
    public void forEach(Consumer<? super DataStructureNode> action) {
        valueNodes.forEach(action);
    }

    @Override
    public Spliterator<DataStructureNode> spliterator() {
        return valueNodes.spliterator();
    }

    @Override
    public Iterator<DataStructureNode> iterator() {
        return valueNodes.iterator();
    }
}
