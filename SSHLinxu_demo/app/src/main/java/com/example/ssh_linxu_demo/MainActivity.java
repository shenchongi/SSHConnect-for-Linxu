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

    private final Message message=new Message();

    private final String TAG = "MainActivity";

    private final Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    tv_one.setText("连接失败");
                    Log.i(TAG,"Connect Suss");
                    break;
                case 1:
                    tv_one.setText("连接成功");
                    break;
                case 4:
                    tv_tow.setText(msg.obj.toString());
                    break;
                case 6:
                    tv_tow.setText(msg.obj.toString());
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewInit();

        SSHConnectTask sshConnectTask = new SSHConnectTask("root", "192.168.31.217", "123456", 21);
        sshConnectTask.start();
        sshConnectTask.setCommand("sudo apt install");
    }

    private void ViewInit() {
        tv_one=findViewById(R.id.tv_one);
        tv_tow=findViewById(R.id.tv_tow);
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
        message.what=4;
        message.obj=results;
        handler.sendMessage(message);
    }

    @Override
    public void error(String error) {
        message.what=6;
        message.obj=error;
        handler.sendMessage(message);
    }
}
