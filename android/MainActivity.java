
package drone.siege.root.rainbowsixsiegedrone;

import android.app.Activity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends Activity {
    private int leftSpeed = 250;
    private int rightSpeed = 250;
    private BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothThread bluetoothThread;
    Utils utils;
    BluetoothDevice device;
    boolean isConnected;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = bluetoothAdapter.getRemoteDevice("00:06:66:D0:DC:E4");
        utils = new Utils(this);
        final SeekBar leftWheel = findViewById(R.id.leftWheel);
        SeekBar forward = findViewById(R.id.forward);
        SeekBar rightWheel = findViewById(R.id.rightWheel);

        connect();

        Button a = findViewById(R.id.nitro);
        String fast = Utils.command(500, 500);
        a.setOnTouchListener(btnOnClick(fast));

        for (int i = 0; i < 5; i++) {
            if (!isConnected) {
                disconnect();
                connect();
            } else {
                break;
            }
        }

        leftWheel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                leftSpeed = progress;
                if (leftSpeed > 350) {
                    String cmd = Utils.command(350, rightSpeed);
                    bluetoothThread.write(cmd);
                } else if (leftSpeed < 150) {
                    String cmd = Utils.command(150, rightSpeed);
                    bluetoothThread.write(cmd);
                } else {
                    String cmd = Utils.command(leftSpeed, rightSpeed);
                    bluetoothThread.write(cmd);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String cmd = Utils.command(250, rightSpeed);
                bluetoothThread.write(cmd);
                seekBar.setProgress(250);
            }
        });

        rightWheel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rightSpeed = progress;
                if (rightSpeed > 350) {
                    String cmd = Utils.command(leftSpeed, 350);
                    bluetoothThread.write(cmd);
                } else if (rightSpeed < 150) {
                    String cmd = Utils.command(leftSpeed, 150);
                    bluetoothThread.write(cmd);
                } else {
                    String cmd = Utils.command(leftSpeed, rightSpeed);
                    bluetoothThread.write(cmd);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String cmd = Utils.command(leftSpeed, 250);
                bluetoothThread.write(cmd);
                seekBar.setProgress(250);

            }
        });

        forward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 350) {
                    String cmd = Utils.command(350, 350);
                    bluetoothThread.write(cmd);
                } else if (leftSpeed < 150) {
                    String cmd = Utils.command(150, 150);
                    bluetoothThread.write(cmd);
                } else {
                    String cmd = Utils.command(leftSpeed, rightSpeed);
                    bluetoothThread.write(cmd);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String cmd = Utils.command(250, 250);
                bluetoothThread.write(cmd);
                seekBar.setProgress(250);
            }
        });
    }

    private BluetoothSocket connect(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void connect() {
        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
            isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }
        bluetoothThread = new BluetoothThread(socket, BluetoothThread.handle("/", new BluetoothThread.BluetoothClass() {
            @Override
            public void onReceived(String data) {
                utils.makeToast(data);
            }
        }));
        bluetoothThread.start();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public View.OnTouchListener btnOnClick(final String cmd) {
        View.OnTouchListener ocl = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bluetoothThread.write(cmd);
                        break;
                    case MotionEvent.ACTION_UP:
                        String stop = Utils.command(leftSpeed, rightSpeed);
                        bluetoothThread.write(stop);
                        break;
                }
                return false;
            }

        };
        return ocl;
    }
}
