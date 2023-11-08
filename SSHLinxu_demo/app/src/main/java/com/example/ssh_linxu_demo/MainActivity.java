package com.example.ssh_linxu_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private TextView tv_one,tv_tow;

    private SSHConnectTask sshConnectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_one=findViewById(R.id.tv_one);
        tv_tow=findViewById(R.id.tv_tow);

        sshConnectTask=new SSHConnectTask("root","192.168.31.217","123456",21);
        sshConnectTask.start();

        sshConnectTask.setSHHConnectCallback(new SSHConnectTask.SSHConnectCallback() {
            @Override
            public void isConnect(boolean isConnect)  {
                if (isConnect){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(),"连接成功",Toast.LENGTH_LONG).show();
                            tv_one.setText("连接成功");
                            try {
                                sshConnectTask.setCommand("sudo apt update");
                            } catch (JSchException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void getSSHReportResults(String results) {
                if (sshConnectTask.getConnectResult()!=null){
                    tv_tow.setText(sshConnectTask.getConnectResult());
                }
            }

            @Override
            public void error(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_one.setText(error);
                        Toast.makeText(getBaseContext(),"连接异常"+error,Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


    }

}