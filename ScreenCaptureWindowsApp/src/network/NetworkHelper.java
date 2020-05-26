//Class for all sort of UDP network calls
package network;

import model.NetworkData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class NetworkHelper {

    private static final int MAX_BUFFER_SIZE = 65507;
    private static DatagramSocket socket = null;
    private static InetAddress address = null;
    private static DatagramPacket dataPacket = null;
    private NetworkData networkData = null;

    public NetworkHelper(NetworkData networkData) {
        this.networkData = networkData;
    }

    public void initConnection() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(networkData.getHostName());
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Object receiveAckFromServer() {
        Object object = null;
        byte[] arrData = new byte[MAX_BUFFER_SIZE];
        dataPacket = new DatagramPacket(arrData, MAX_BUFFER_SIZE);
        try {
            socket.receive(dataPacket);
            arrData = dataPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(arrData);
            ObjectInputStream is = new ObjectInputStream(in);
            object = is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    public void sendToServer(byte[] data) {
        try {
            if (dataPacket != null) {
                dataPacket = new DatagramPacket(data, data.length, dataPacket.getAddress(), dataPacket.getPort());
            } else {
                dataPacket = new DatagramPacket(data, data.length, InetAddress.getByName(networkData.getHostName()), networkData.getPortNumber());
            }
            socket.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
