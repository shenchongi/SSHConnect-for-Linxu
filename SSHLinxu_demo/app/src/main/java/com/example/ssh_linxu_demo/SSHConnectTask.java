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

public class SSHConnectTask extends Call.Callback {
    private JSch jSch;

    private Session session;

    private ChannelExec channelExec;

    private String username;

    private String host;

    private String passwd;
    private int port;

    private Thread sshThread;

    private String TAG = "SSHConnect";

    private String connectResult;

    private boolean isSSHConnect = false;

    private SSHConnectCallback sshConnectCallback;

    public SSHConnectTask(String username, String host, String passwd, int port) {
        this.username = username;
        this.host = host;
        this.passwd = passwd;
        this.port = port;
    }

    public synchronized void startSSHConnect() {
        sshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                jSch = new JSch();
                try {

                    session = jSch.getSession(username, host, port);
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
        });
        sshThread.start();
    }

    public void setSHHConnectCallback(SSHConnectCallback sshConnectCallback) {
        this.sshConnectCallback = sshConnectCallback;
    }

    public void setCommand(String command) throws JSchException, IOException {
        executeCommand(command);
    }

    public String executeCommand(String command) throws  IOException {
        StringBuffer result = new StringBuffer();
        sshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                channelExec.setCommand(command);

                InputStream inputStream = null;
                try {
                    inputStream = channelExec.getInputStream();
                    channelExec.connect();



                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while((line=reader.readLine())!=null)
                    {
                        result.append(line).append("\n");
                        Log.i(TAG, "executeCommand: " + reader.toString());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSchException e) {
                    throw new RuntimeException(e);
                }
            }
        }) {

        };
        sshThread.start();
        return result.toString();
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

        void error(String error);
    }

}
