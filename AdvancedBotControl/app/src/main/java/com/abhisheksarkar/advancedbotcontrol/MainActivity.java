package com.abhisheksarkar.advancedbotcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    TextView myLabel;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice=null;
    OutputStream mmOutputStream;

    int p=255;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Button lfr=(Button)findViewById(R.id.lfr),lba=(Button)findViewById(R.id.lba),rfr=(Button)findViewById(R.id.rfr),
                rba=(Button)findViewById(R.id.rba),pwm=(Button)findViewById(R.id.pwm);
        final EditText edit=(EditText)findViewById(R.id.edit);
        final SeekBar seek=(SeekBar)findViewById(R.id.seekBar);
        myLabel = (TextView)findViewById(R.id.text);
        edit.setText("255");

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

        try{
            findBT();
            if(mmDevice!=null)
            openBT();
        }
        catch (IOException ex) { }
        if(mmDevice!=null&&mmSocket.isConnected())
        {
        lfr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lba.setEnabled(true);
                try {
                    sendData("x");
                } catch (IOException ex) {
                }
            }
        });

        lba.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lfr.setEnabled(true);
                try {
                    sendData("x");
                } catch (IOException ex) {
                }
            }
        });
        lfr.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                lba.setEnabled(false);
                try {
                    sendData("1");
                } catch (IOException ex) {
                }
                return false;
            }
        });

        lba.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                lfr.setEnabled(false);
                try {
                    sendData("2");
                } catch (IOException ex) {
                }
                return false;
            }
        });
        rfr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rba.setEnabled(true);
                try {
                    sendData("y");
                } catch (IOException ex) {
                }
            }
        });

        rba.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rfr.setEnabled(true);
                try {
                    sendData("y");
                } catch (IOException ex) {
                }
            }
        });
        rfr.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                rba.setEnabled(false);
                try {
                    sendData("3");
                } catch (IOException ex) {
                }
                return false;
            }
        });

        rba.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                rfr.setEnabled(false);
                try {
                    sendData("4");
                } catch (IOException ex) {
                }
                return false;
            }
        });
        pwm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData("p" + edit.getText().toString());
                    seek.setProgress(Integer.parseInt(edit.getText().toString())%256);
                } catch (IOException ex) {
                }
                hide(MainActivity.this);
            }
        });
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    sendData("p" + progress);

                } catch (Exception ex) {
                }
                p = progress;
                edit.setText(""+p);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this,"Fixed PWM at: " +p,Toast.LENGTH_SHORT).show();
            }
        });
        }
        else
        {
            myLabel.setText("Connection Closed");
            lfr.setEnabled(false);
            lba.setEnabled(false);
            rfr.setEnabled(false);
            rba.setEnabled(false);
            pwm.setEnabled(false);
            findViewById(R.id.edit).setEnabled(false);
            findViewById(R.id.seekBar).setEnabled(false);
        }
    }
    public static void hide(Activity activity){
        InputMethodManager in=(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==10)
            if(resultCode==Activity.RESULT_OK)
            {
                finish();
                startActivity(getIntent());
            }
            else
            {
                Toast.makeText(MainActivity.this,"Request Denied",Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 10);
        }
        else
        {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("HC-05")) {
                        mmDevice = device;
                        myLabel.setText("Bluetooth Device Found");
                        break;
                    }
                }
            }
            if (mmDevice == null)
                myLabel.setText("Device Not Found");
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                myLabel.setText("Device Connected");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                finish();
                Toast.makeText(MainActivity.this,"Connection Lost!!!",Toast.LENGTH_SHORT).show();
            }
            //else if...
        }
    };
    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
    }

    void sendData(String n) throws IOException
    {
        String msg = n;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
    }
    /*void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }*/
}