package com.example.ssh_linxu_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements SSHConnectTask.SSHConnectCallback {

    private TextView tv_one,tv_tow;

    private SSHConnectTask sshConnectTask;

    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_one=findViewById(R.id.tv_one);
        tv_tow=findViewById(R.id.tv_tow);

        sshConnectTask=new SSHConnectTask("root","192.168.31.217","123456",21);
        sshConnectTask.start();

    }

    @Override
    public void isConnect(boolean isConnect) {
        if (isConnect){
            handler.sendEmptyMessage(1);
        }else {
            handler.sendEmptyMessage(0);
        }

    }

    @Override
    public void getSSHReportResults(String results) {

    }

    @Override
    public void error(String error) {

    }
}