package model;

public class NetworkData {
    private String hostName;
    private int portNumber;
    private String fileName;
    private int noOfPartition;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNoOfPartition() {
        return noOfPartition;
    }

    public void setNoOfPartition(int noOfPartition) {
        this.noOfPartition = noOfPartition;
    }
}
