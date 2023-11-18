package com.example.ssh_linxu_demo;

import android.telecom.Call;
import android.util.Log;
import android.util.TimeUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * shenchong
 * 2023.11.3
 * SSH连接管理类
 */
public class SSHConnectTask extends Thread {

    private ChannelExec channelExec;

    private final String username;

    private final String host;

    private final String passwd;
    private final int port;

    private final String TAG = "SSHConnect";

    private String connectResult;


    private boolean isSSHConnect = false;

    private SSHConnectCallback sshConnectCallback;

    public SSHConnectTask(String username, String host, String passwd, int port) {
        this.username = username;
        this.host = host;
        this.passwd = passwd;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        startSSHConnect();
    }

    private synchronized void startSSHConnect() {
        JSch jSch = new JSch();
        try {
            Session session = jSch.getSession(username, host, port);
            session.setPassword(passwd);
            session.setPort(port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            if (session.isConnected()) {
                setIsSSHConnect(true);
                sshConnectCallback.isConnect(true);
                setConnectResult("连接成功");
                channelExec = (ChannelExec) session.openChannel("exec");
                Log.i(TAG, "Connected to the server.");
            } else {
                setIsSSHConnect(false);
                sshConnectCallback.isConnect(false);
                setConnectResult("连接失败");
                Log.i(TAG, "Connected to the server Failed.");
            }

        } catch (JSchException e) {
            //暂不抛出异常
            Log.e(TAG, "SSH connection error: " + e.getMessage());
            sshConnectCallback.error(e.getMessage());
            Log.e(TAG, "终止连接！");
            stop();
        }
    }

    public void setSHHConnectCallback(SSHConnectCallback sshConnectCallback) {
        this.sshConnectCallback = sshConnectCallback;
    }

    public void setCommand(String command) {
        try{
            executeCommand(command);
        }catch (IOException e){
            Log.e(TAG, "setCommand: 命令执行异常"+e.getMessage());
            sshConnectCallback.error(e.getMessage());
        }
    }

    public void executeCommand(String command) throws IOException {
        StringBuilder result = new StringBuilder();
        channelExec.setCommand(command);

        InputStream inputStream = null;
        try {
            inputStream = channelExec.getInputStream();
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
                Log.i(TAG, "executeCommand: " + reader);
                sshConnectCallback.getSSHReportResults(reader.toString());
            }
        } catch (IOException | JSchException e) {
            Log.e(TAG, "Error executing command: " + e.getMessage());
            sshConnectCallback.error(e.getMessage());
            // 关闭输入流和断开通道连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    Log.e(TAG, "Error closing input stream: " + ioException.getMessage());
                }
            }
            channelExec.disconnect();
            throw new RuntimeException(e);
        }
    }

    private void setConnectResult(String result) {
        this.connectResult = result;
    }

    public String getConnectResult() {
        return connectResult;
    }

    private void setIsSSHConnect(boolean isSSHConnect) {
        this.isSSHConnect = isSSHConnect;
    }

    public boolean isSSHConnect() {
        return this.isSSHConnect;
    }


    public interface SSHConnectCallback {
        void isConnect(boolean isConnect);

        void getSSHReportResults(String results);

        void error(String error);


    }

}
