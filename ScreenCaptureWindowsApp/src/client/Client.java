/*Client class contains all the business logic and interfaces of the client module
* View Interface : The ScreenCaptureTimer will have all the overridden view methods.
* Presenter Interface : The presenterImpl calss will have all the overridden methods
* ClientPresenterImpl : This class is responsible for all the logical work*/

package client;

import javafx.application.Platform;
import javafx.stage.Stage;
import model.*;
import network.NetworkHelper;
import screencapture.ScreenCaptureHelper;
import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    public interface View {
        void inflateView(Stage primaryStage);

        void onTaskListFetched(ArrayList<String> arrTaskList);

        void setSystemTray();

        void startClientInitProcess();

        void onClientInitializedSuccessfully();

        void onConnectEstablishedSuccessfully();

        void onScreenCapturedSuccessfully(ImageChunksMetaData[] arrImageChunksMetaData);

        void onMetaDataSentSuccessfully(ImageChunksMetaData[] arrImageChunks);

        void onImageSentSuccessfully();
    }


    public interface Presenter {
        void inflateView(Stage primaryStage);

        void getRunningTaskList();

        void setSystemTray();

        void setAppRunningStatus(boolean isAppRunning);

        boolean isScreenCaptureRunning();

        void initClient();

        void startApp();

        void waitingForServerAck();

        void sendConnectionAckToServer(int noOfPartitions, String projectName, String projectPassword);

        void startScreenCapture();

        void sendMetadataToServer(ImageChunksMetaData[] arrImageChunkData);

        void sendImageFileToServer(ImageChunksMetaData[] arrImageChunkData);
    }

    public static class ClientPresenterImpl implements Presenter, ScreenCaptureHelper.Listener {
        private static final int MAX_IMAGE_DATA_ARRAY_SIZE = 65000;
        private static Object lastSentObj = null;
        private NetworkHelper networkHelper = null;
        private ScreenCaptureHelper screenCaptureHelper = null;
        private NetworkData networkData = null;
        private View view;
        private boolean isAppRunning = false;
        private Thread threadRunningTask = null;

        private int noOfPartitions;
        private String projectName;
        private String projectPassword;

        public ClientPresenterImpl(View view) {
            this.view = view;
        }

        @Override
        public void inflateView(Stage primaryStage) {
            view.inflateView(primaryStage);
        }

        /*Fetch the application list on background thread and
        * push it to the Main thread using Platform.runLater*/
        @Override
        public void getRunningTaskList() {
            threadRunningTask = new Thread(() -> {
                ArrayList<String> arrTaskList = getTask();
                Platform.runLater(() -> {
                    view.onTaskListFetched(arrTaskList);
                    threadRunningTask.interrupt();
                    threadRunningTask = null;
                });
            });
            threadRunningTask.start();
        }

        //Add testing annotation to test from test class
        private ArrayList<String> getTask() {
            ArrayList<String> arrRunningApps = new ArrayList<>();
            try {
                String line;
                HashMap<String, String> map = new HashMap<>();
                StringBuilder pidInfo = new StringBuilder();
                //The Bash script needs to update to remove some unnecessary application
//                Process p = Runtime.getRuntime().exec("tasklist /v /fo csv /nh /fi \"username eq cray \" /fi \"status eq running\"");
                Process p = Runtime.getRuntime().exec("tasklist /v /fo csv /nh /fi \"username eq " + System.getProperty("user.name").toLowerCase() + " \" /fi \"status eq running\"");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String[] arrTemp;
                while ((line = input.readLine()) != null) {
                    arrTemp = line.trim().replace("\"", "").split(",");
                    String appName = arrTemp[0].replace(".exe", "");
                    if (!map.containsKey(appName)) {
                        map.put(appName, arrTemp[1]);
                        arrRunningApps.add(appName);
                        pidInfo.append(appName).append("\n");
                    }
                }
                input.close();
//                System.out.println(pidInfo.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return arrRunningApps;

        }

        @Override
        public void setSystemTray() {
            view.setSystemTray();
        }

        /*When init client is called there is a ton of work done here
        * ScreenCaptureHelper init
        * Set the network data (port number & Address)
        * Init Udp connection
        * Callback to notify the ScreenCaptureTimer class after the work is done*/
        @Override
        public void initClient() {
            try {
                screenCaptureHelper = new ScreenCaptureHelper(this);
                networkData = setNetworkData();
                networkHelper = new NetworkHelper(networkData);
                networkHelper.initConnection();
                System.out.println("Client init successful");
                view.onClientInitializedSuccessfully();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*When startApp is called on the start/stop button click
         * lastSentObj is updated after every packet is sent to server
         * If its null means the app haven't been started*/
        @Override
        public void startApp() {
            if (isAppRunning) {
                try {
                    if (lastSentObj == null) {
                        //first time
                        view.startClientInitProcess();
                    } else {
                        //Every other time we'll send the Connection est. obj.
                        //As the username & password might change. But as we are
                        //identifying the client with the mac address so change in uname & pass
                        //won't affect much on server side.
                        view.onClientInitializedSuccessfully();
                    }
                } catch (Exception e) {

                }
            }
        }

        /*There the Model EstablishConnection is sent to the server with info to request the connection*/
        @Override
        public void sendConnectionAckToServer(int noOfPartitions, String projectName, String projectPassword) {
            this.noOfPartitions = noOfPartitions;
            EstablishConnection establishConnection = new EstablishConnection();
            establishConnection.setClientId(1);
            establishConnection.setProjectName(projectName);
            establishConnection.setProjectPassword(projectPassword);
            establishConnection.setRetransmissionTimeout(10000); //dummy value will use it later on
            byte[] objArray = Utils.convertObjToByteArray(establishConnection);
            lastSentObj = establishConnection;
            networkHelper.sendToServer(objArray);
        }


        @Override
        public void startScreenCapture() {
            screenCaptureHelper.startCapturingScreen(noOfPartitions);
        }

        /*There the Model ImageMetaData is sent to the server with Image info*/
        @Override
        public void sendMetadataToServer(ImageChunksMetaData[] arrImageChunkData) {
            ImageMetaData imageMetaData = new ImageMetaData();
            imageMetaData.setClientId(1);
            imageMetaData.setNoOfImages(noOfPartitions * noOfPartitions);
            imageMetaData.setArrImageChunks(arrImageChunkData);
            byte[] objArray = Utils.convertObjToByteArray(imageMetaData);
            lastSentObj = imageMetaData;
            networkHelper.sendToServer(objArray);
        }

        /*There the Model DataTransfer is sent to the server with Image data
        * We send a part of image and wait for the server response*/
        @Override
        public void sendImageFileToServer(ImageChunksMetaData[] arrImageChunkData) {
            int seqNo = 2;
            FileInputStream fi;
            byte[] arrImageData;
            long fileSize;
            File imageFile;

            try {
                for (int index = 0, arrSize = arrImageChunkData.length; index < arrSize; index++) {
                    int l = 0;
                    imageFile = new File(arrImageChunkData[index].getImageName());
                    fileSize = imageFile.length();
                    fi = new FileInputStream(imageFile);

                    for (int i = 0; i < fileSize; ) {
                        arrImageData = new byte[MAX_IMAGE_DATA_ARRAY_SIZE];
                        DataTransfer dataTransfer = new DataTransfer();
                        dataTransfer.setCurrentImageSeqNo(index + 1);
                        if (l == 0) {
                            dataTransfer.setIsFirstPacketOfImageBlock(1);
                        }
                        l = fi.read(arrImageData);
                        //There can be multiple datatransfer for each image block.
                        //Here we set setIsLastPacketOfImageBlock to 1 and let the server know that
                        //the particular image has ended.
                        //Here we set setIsLastPacket to let the server that this is our last packet
                        //all the image data havebeen transfered.
                        //The errors & retransmissions are not handled yet
                        if (l < MAX_IMAGE_DATA_ARRAY_SIZE)
                            dataTransfer.setIsLastPacketOfImageBlock(1);
                        if (index == arrSize - 1) {
                            dataTransfer.setIsLastPacket(1);
                        }
                        dataTransfer.setArrImage(arrImageData);
                        dataTransfer.setSeqNo(seqNo);
                        byte[] arrImageDataObj = Utils.convertObjToByteArray(dataTransfer);
                        lastSentObj = dataTransfer;
                        networkHelper.sendToServer(arrImageDataObj);

                        //Here we wait for the server request that's why 80ms sleep is used
                        Thread.sleep(80);

                        Object receivedObj = networkHelper.receiveAckFromServer();
                        if (!(receivedObj instanceof PacketAck))
                            throw new Exception();
                        i += l;
                        seqNo++;
                    }
                    fi.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendImageFileToServer(arrImageChunkData);
            }
        }

        /*Server sents ack in form of PacketAck model
        * All the required data will be in this packet.
        * If server sends data in any other format then its curroupt.
        * Once data is received then we'll take the next step accordingly */
        @Override
        public void waitingForServerAck() {
            try {
                Object receivedObj = networkHelper.receiveAckFromServer();
                //TODO Ask for/Do retransmission
                if (!(receivedObj instanceof PacketAck)) {
                    throw new Exception("The Ack is not in a proper format");
                }

                if (lastSentObj instanceof EstablishConnection) {
                    //Move with the next step with Image MetaData
                    System.out.println("Connection with Client established successfully");
                    view.onConnectEstablishedSuccessfully();
                    //                System.out.println("EstablishConnection ack received = " + receivedObj);
                } else if (lastSentObj instanceof ImageMetaData) {
                    //Move with the next step with Image Transfer
                    System.out.println("Metadata sent successfully, ready to send image");
                    view.onMetaDataSentSuccessfully(((ImageMetaData) lastSentObj).getArrImageChunks());
                    //                System.out.println("ImageMetaData ack received = " + receivedObj);
                } else if (lastSentObj instanceof DataTransfer) {
                    //Continue till the last ack packet is received.
                    if (((DataTransfer) lastSentObj).getIsLastPacket() == 1) {
                        System.out.println("Image sent successfully");
                        view.onImageSentSuccessfully();
                    }
                    //                System.out.println("DataTransfer ack received = " + receivedObj);
                } else {
                    throw new Exception("The data is null or not in the proper format");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isScreenCaptureRunning() {
            return isAppRunning;
        }

        @Override
        public void setAppRunningStatus(boolean isAppRunning) {
            this.isAppRunning = isAppRunning;
        }

        /*onScreenCaptureSuccessful & onScreenCaptureFailed are the callbacks from SCreenCapturehelper method
        * If successfull then process to metadata transfer
        * else try the capture part again */
        @Override
        public void onScreenCaptureSuccessful(ImageChunksMetaData[] arrImageChunksMetaData) {
            view.onScreenCapturedSuccessfully(arrImageChunksMetaData);
        }

        @Override
        public void onScreenCaptureFailed(int noOfPartitions) {
            screenCaptureHelper.startCapturingScreen(noOfPartitions);
        }

        /*Set basic adddress & port number*/
        private static NetworkData setNetworkData() {
            NetworkData networkData = new NetworkData();
            networkData.setHostName("localhost");
//            networkData.setHostName("76.30.19.215");
            networkData.setPortNumber(5555);
            return networkData;
        }
    }

}
