package com.idc.model;

/**
 * Created by eladcohen on 5/12/16.
 */
public class ArgMaxBinaryMessage {

    public BinaryMessage getBinaryMessage() {
        return binaryMessage;
    }

    public ArgMax getArgMax() {
        return argMax;
    }

    private BinaryMessage binaryMessage;
    private ArgMax argMax;

    public ArgMaxBinaryMessage(BinaryMessage binaryMessage, ArgMax argMax) {
        this.binaryMessage = binaryMessage;
        this.argMax = argMax;
    }
}
