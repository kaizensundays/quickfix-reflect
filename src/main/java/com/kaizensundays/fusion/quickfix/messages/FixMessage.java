package com.kaizensundays.fusion.quickfix.messages;

/**
 * Created: Saturday 6/25/2022, 12:11 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
public abstract class FixMessage {

    public final String msgType;

    public String beginString;
    public String senderCompID;
    public String targetCompID;

    public FixMessage(String msgType) {
        this.msgType = msgType;
    }

}
