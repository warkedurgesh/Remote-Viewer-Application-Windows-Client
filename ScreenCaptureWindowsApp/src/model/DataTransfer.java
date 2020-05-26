package model;

import java.io.Serializable;
import java.util.Arrays;

public class DataTransfer implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private int clientId = 0;
    private int seqNo = 0;
    private final int transmissionType = 3;
    private int isLastPacket = 0;
    private int isLastPacketOfImageBlock= 0;
    private int isFirstPacketOfImageBlock= 0;
    private int currentImageSeqNo = 1;
    private byte[] arrImage = new byte[65000];

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getTransmissionType() {
        return transmissionType;
    }

    public int getIsLastPacket() {
        return isLastPacket;
    }

    public void setIsLastPacket(int isLastPacket) {
        this.isLastPacket = isLastPacket;
    }

    public int getIsLastPacketOfImageBlock() {
        return isLastPacketOfImageBlock;
    }

    public void setIsLastPacketOfImageBlock(int isLastPacketOfImageBlock) {
        this.isLastPacketOfImageBlock = isLastPacketOfImageBlock;
    }

    public int getIsFirstPacketOfImageBlock() {
        return isFirstPacketOfImageBlock;
    }

    public void setIsFirstPacketOfImageBlock(int isFirstPacketOfImageBlock) {
        this.isFirstPacketOfImageBlock = isFirstPacketOfImageBlock;
    }

    public int getCurrentImageSeqNo() {
        return currentImageSeqNo;
    }

    public void setCurrentImageSeqNo(int currentImageSeqNo) {
        this.currentImageSeqNo = currentImageSeqNo;
    }

    public byte[] getArrImage() {
        return arrImage;
    }

    public void setArrImage(byte[] arrImage) {
        this.arrImage = arrImage;
    }

    @Override
    public String toString() {
        return new StringBuffer("client_id = ")
                .append(getClientId())
                .append("\n")
                .append("seq_no = ")
                .append(getSeqNo())
                .append("\n")
                .append("transmission_type = ")
                .append(getTransmissionType())
                .append("\n")
                .append("is_last_packet = ")
                .append(getIsLastPacket())
                .append("\n")
                .append("is_last_packet_of_image_block = ")
                .append(getIsLastPacketOfImageBlock())
                .append("\n")
                .append("is_first_packet_of_image_block = ")
                .append(getIsFirstPacketOfImageBlock())
                .append("\n")
                .append("current_image_seq_no = ")
                .append(getCurrentImageSeqNo())
                .append("\n")
                .append(Arrays.toString(getArrImage()))
                .append("\n")
                .toString();
    }
}
