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

    private JSch jSch;
    private Session session;

    private TextView tv_one,tv_tow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_one=findViewById(R.id.tv_one);
        tv_tow=findViewById(R.id.tv_tow);

        SSHConnectTask task=new SSHConnectTask();
        task.execute("root","192.168.31.217","123456");
    }


    private class SSHConnectTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String username=strings[0];
            String host=strings[1];
            int port=22;
            String passwd=strings[2];

            try {
                if (openSession(username,host,port,passwd)){
                    String results=executeCommand("top");
                    Log.i("SSHConnect", "openSession:"+results);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_LONG).show();
                            Log.i("SSHConnect", "openSession: SSH connecting");
                            tv_one.setText("连接成功");
                            tv_tow.setText(results);
                        }
                    });
                }
            } catch (JSchException | IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        }
    }

    private boolean openSession(String username,String host,int port,String passwd) throws JSchException, IOException {
        jSch=new JSch();
        session=jSch.getSession(username,host,port);
        session.setPassword(passwd);
        session.setConfig("StrictHostKeyChecking","no");
        session.connect();
        System.out.println("Connected to the server.");

        if (session.isConnected()){
            Log.i("SSHConnect", "openSession: "+"连接成功");
            return true;
        }else {
            Log.i("SSHConnect", "openSession: "+"连接失败");
        }
        return false;
    }

    private String executeCommand(String command) throws JSchException, IOException {
        ChannelExec channelExec=(ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);

        InputStream inputStream=channelExec.getInputStream();

        channelExec.connect();

        StringBuffer result=new StringBuffer();
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line=reader.readLine())!=null){
                result.append(line).append("\n");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            channelExec.connect();
        }
        return result.toString();
    }
}