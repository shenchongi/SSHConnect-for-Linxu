package com.example.ssh_linxu_demo;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnectTask {
    private JSch jSch;

    private Session session;

    private ChannelExec channelExec;

    private String username;

    private String host;

    private String passwd;
    private int port;

    private Thread sshThread;

    private String TAG="SSHConnect";

    private String connectResult;

    private boolean isSSHConnect;

    public SSHConnectTask(String username,String host,String passwd,int port){
        this.username=username;
        this.host=host;
        this.passwd=passwd;
        this.port=port;
    }

    public synchronized void startSSHConnect() {
        sshThread=new Thread(new Runnable() {
            @Override
            public void run() {
                jSch=new JSch();
                try {

                    session=jSch.getSession(username,host,port);
                    session.setPassword(passwd);
                    session.setConfig("StrictHostKeyChecking","no");
                    session.connect();


                    if (session.isConnected()){
                        isSSHConnect(true);
                        setConnectResult("连接成功");
                        executeCommand("sudo apt update");
                        Log.i(TAG, "Connected to the server.");
                    }else {
                        isSSHConnect(false);
                        setConnectResult("连接失败");
                        Log.i(TAG, "Connected to the server Failed.");
                    }

                } catch (JSchException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        sshThread.start();
    }
    public String executeCommand(String command) throws JSchException {
        channelExec=(ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);

        return connectResult;
    }

    public void setConnectResult(String result){
        this.connectResult=result;
    }

    public String getConnectResult(){
        return connectResult;
    }

    public boolean isSSHConnect(boolean isSSHConnect){
        this.isSSHConnect=isSSHConnect;

        return isSSHConnect;
    }

}
