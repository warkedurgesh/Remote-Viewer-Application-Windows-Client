package model;

import java.io.Serializable;

public class PacketAck implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private int client_id = -1;
    private int seq_no = -1;
    private int transmission_type = -1;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(int seq_no) {
        this.seq_no = seq_no;
    }

    public int getTransmissionType() {
        return transmission_type;
    }

    public void setTransmissionType(int transmission_type) {
        this.transmission_type = transmission_type;
    }

    @Override
    public String toString() {
        return new StringBuffer("client_id = ")
                .append(client_id)
                .append("\n")
                .append("seq_no = ")
                .append(seq_no)
                .append("\n")
                .append("transmission_type = ")
                .append(transmission_type)
                .append("\n")
                .toString();
    }
}
