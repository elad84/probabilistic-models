package com.idc.model;

/**
 * Model for Max argument of message passing
 * 
 * @author eladcohen
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
