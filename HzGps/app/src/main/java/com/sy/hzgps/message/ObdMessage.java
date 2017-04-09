package com.sy.hzgps.message;

import java.io.Serializable;

/**
 * Created by jiayang on 2016/7/14.
 */
public class ObdMessage implements Serializable {

    /**
     * 消息产生的时间
     */
    private long timeStamp = System.currentTimeMillis();

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * 消息Id
     */

    private ObdMessageID messageId = ObdMessageID.UNKOWN;

    public void setMessageId(ObdMessageID messageId) {
        this.messageId = messageId;
    }

    public ObdMessageID getMessageId() {
        return messageId;
    }

    /**
     * 如果发送未成功，是否需要保存到数据库
     */
    private boolean needSave = false;

    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }

    public boolean getNeedSave() {
        return needSave;
    }

    /**
     * 消息序列号
     */
    private short serialNo = 0;

    public void setSerialNo(short serialNo) {
        this.serialNo = serialNo;
    }

    public short getSerialNo() {
        return serialNo;
    }

    /**
     * 通信发送的次数
     */
    private short sendTimes = 0;

    public void setSendTimes(short sendTimes) {
        this.sendTimes = sendTimes;
    }

    public short getSendTimes() {
        return sendTimes;
    }

    public void incrSendTimes() {
        ++sendTimes;
    }

    
    public ObdMessage(ObdMessageID messageId, boolean needSave) {
        this.messageId = messageId;
        this.needSave = needSave;
    }

}
