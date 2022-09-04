package io.xpipe.core.data.generic;

import io.xpipe.core.data.node.DataStructureNode;
import io.xpipe.core.data.node.ValueNode;

import java.util.Map;

public class GenericDataStructureNodeReader implements GenericDataStreamCallback {

    private DataStructureNode node;
    private GenericAbstractReader reader;

    public DataStructureNode create() {
        if (node == null) {
            throw new IllegalStateException("No node has been created yet");
        }

        reader = null;
        return node;
    }

    private boolean hasReader() {
        return reader != null;
    }

    @Override
    public void onName(String name) {
        if (hasReader()) {
            reader.onName(name);
            return;
        }

        throw new IllegalStateException("Expected node start but got name");
    }

    @Override
    public void onArrayStart(int length) {
        if (hasReader()) {
            reader.onArrayStart(length);
            return;
        }

        reader = GenericArrayReader.newReader(length);
    }

    @Override
    public void onArrayEnd() {
        if (!hasReader()) {
            throw new IllegalStateException("No array to close");
        }

        reader.onArrayEnd();
        if (reader.isDone()) {
            node = reader.create();
            reader = null;
        }
    }

    @Override
    public void onTupleStart(int length) {
        if (hasReader()) {
            reader.onTupleStart(length);
            return;
        }

        reader = GenericTupleReader.newReader(length);
    }

    @Override
    public void onTupleEnd() {
        if (!hasReader()) {
            throw new IllegalStateException("No tuple to close");
        }

        reader.onTupleEnd();
        if (reader.isDone()) {
            node = reader.create();
            reader = null;
        }
    }

    @Override
    public void onValue(byte[] value, Map<Integer, String> metaAttributes) {
        if (hasReader()) {
            reader.onValue(value, metaAttributes);
            return;
        }


        node = ValueNode.of(value).tag(metaAttributes);
    }
}
