package net.xxhong.rosclient.util;

import android.support.annotation.Nullable;
import android.util.Log;

import com.jilk.ros.ROSClient;
import com.jilk.ros.rosapi.message.TypeDef;
import com.jilk.ros.rosbridge.ROSBridgeClient;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hoyn on 17/3/3.
 */

public class RosUtil {
    private static ROSBridgeClient client;
    private static TypeDef[] typeDef;
    private static TreeNode root;

    public static void rosInit(final String ip, final String port, final @Nullable ROSClient.ConnectionStatusListener listener) {
        //如果这里不开子线程，如果连接失败，比如没有网络的时候连接，会造成主线程堵塞崩溃
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    client.connect(listener);
                } else {
                    client.connect();
                }
            }
        }).start();
    }
    public static void rosInit(final String wsUrl, final @Nullable ROSClient.ConnectionStatusListener listener) {
        //如果这里不开子线程，如果连接失败，比如没有网络的时候连接，会造成主线程堵塞崩溃
        client = new ROSBridgeClient(wsUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    client.connect(listener);
                } else {
                    client.connect();
                }
            }
        }).start();
    }
    public static boolean isConnect() {
        if (client != null) {
            return client.isConnect();
        } else {
            return false;
        }
    }

    /**
     * 发送service消息给机器人
     *
     * @param serviceName service 名
     * @param params      可变参数
     */
    public static void sendServiceMessage(String serviceName, Object... params) {
        String msg = "";
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof String) {
                msg += "\"" + param + "\"";
            } else if (param instanceof Integer) {
                msg += param;
            }
            if (i != params.length - 1) {
                msg += ",";
            }
        }
        msg = "{\"op\":\"call_service\",\"service\":\"" + serviceName + "\",\"args\":[" + msg + "]}";
        Log.e("RosUtil", msg);
        client.send(msg);
    }

    /**
     * 发送Topic消息给机器人
     *
     * @param topicName topic 名
     * @param data      参数
     */
    public static void sendTopicMessage(String topicName, String data) {
        String msg = "{\"op\":\"publish\",\"topic\":\"" + topicName + "\",\"msg\":{" + data + "}}";
        Log.e("RosUtil", msg);
        client.send(msg);
    }

    /**
     * 订阅一个topic
     * @param detailName
     */
    public static void subscribeTopic(String detailName){
        client.send("{\"op\":\"subscribe\",\"topic\":\"" + detailName + "\"}");
    }

    /**
     * 取消订阅一个topic
     * @param detailName
     */
    public static void unsubscribeTopic(String detailName){
        client.send("{\"op\":\"unsubscribe\",\"topic\":\"" + detailName + "\"}");
    }

    /**
     * 获取节点
     *
     * @return
     * @throws InterruptedException
     */
    public static ArrayList<String> getNodeList() throws InterruptedException {
        String[] nodes = client.getNodes();
        return (ArrayList<String>) Arrays.asList(nodes);
    }

    /**
     * 获取services
     *
     * @return
     * @throws InterruptedException
     */
    public static ArrayList<String> getServiceList() throws InterruptedException {
        String[] services = client.getServices();
        return (ArrayList<String>) Arrays.asList(services);
    }

    /**
     * 获取Topics
     *
     * @return
     * @throws InterruptedException
     */
    public static ArrayList<String> getTopicList() throws InterruptedException {
        String[] topics = client.getTopics();
        return (ArrayList<String>) Arrays.asList(topics);
    }

}
