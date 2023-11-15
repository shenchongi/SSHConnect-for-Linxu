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
            sshConnectCallback.error(e.toString());
            throw new RuntimeException(e);

        }
    }

    public void setSHHConnectCallback(SSHConnectCallback sshConnectCallback) {
        this.sshConnectCallback = sshConnectCallback;
    }

    public void setCommand(String command) throws JSchException, IOException {
        executeCommand(command);
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
                Log.i(TAG, "executeCommand: " + reader.toString());
                sshConnectCallback.getSSHReportResults(reader.toString());
            }
        } catch (IOException | JSchException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConnectResult(String result) {
        this.connectResult = result;
    }

    public String getConnectResult() {
        return connectResult;
    }

    public void setIsSSHConnect(boolean isSSHConnect) {
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
