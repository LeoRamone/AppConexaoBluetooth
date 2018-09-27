package com.example.leonardo_soares.apppigormito;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Leonardo Soares on 24/09/18.
 * leonardo_soares_santos@outlook.com
 */
public class MainActivity extends AppCompatActivity {


    //BOTÃO
    Button btConexao, btnLed1, btnLed2, btnLed3;
    //CONSTANTES
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    //UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b4fb");

    //ARMAZENAR OS DADOS A SEREM ENVIADOS
    ConnectedThread connectedThread;

    BluetoothAdapter myBluetoothAdapter = null;
    BluetoothDevice meuDevice = null;
    BluetoothSocket meuSocket = null;

    private static String MAC = null;

    //SEGURANÇA SEM PEDIR PERMISSAO
    UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //VERIFICA BLUETOOTH
    boolean conection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//INSTANCIANDO OS BOTOES
        btConexao = (Button) findViewById(R.id.btnConectar);
        btnLed1 = (Button) findViewById(R.id.btn1);
        btnLed2 = (Button) findViewById(R.id.btn2);
        btnLed3 = (Button) findViewById(R.id.btn3);


        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//VERIFICAMDO SE O DISPOSITIVO POSSUI BLUETOOTH
        if (myBluetoothAdapter == null) {


            Toast.makeText(getApplicationContext(), " SEU DISPOSITIVO NÃO POSSUI BLUETOOTH", Toast.LENGTH_LONG).show();


//CASO TENHA, PEDIR PARA ATIVAR


        } else if (!myBluetoothAdapter.isEnabled()) {
            Intent enbleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enbleIntent, SOLICITA_ATIVACAO);

        }

        btConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (conection) {
                    try {
                        meuSocket.close();
                        conection = false;
                        btConexao.setText("CONECTAR");
                        Toast.makeText(getApplicationContext(), "BLUETOOTH DESCONECTADO", Toast.LENGTH_LONG).show();
                    } catch (IOException errado) {

                        Toast.makeText(getApplicationContext(), "OCORREU UM ERRO : " + errado, Toast.LENGTH_LONG).show();
                    }

                } else {

                    Intent abreLista = new Intent(MainActivity.this, ListarBluetooth.class);
                    startActivityForResult(abreLista, SOLICITA_CONEXAO);
                }

            }
        });

        btnLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conection) {

                    connectedThread.envia4Pigor("1");
                    Toast.makeText(getApplicationContext(), "mensagem 1 enviada ao led01!", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Bluetooth não esta conectado  !", Toast.LENGTH_LONG).show();

                }
            }
        });

        btnLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conection) {

                    connectedThread.envia4Pigor("2");
                    Toast.makeText(getApplicationContext(), "mensagem 2 enviada ao led02!", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Bluetooth não esta conectado  !", Toast.LENGTH_LONG).show();

                }
            }
        });

        btnLed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conection) {

                    connectedThread.envia4Pigor("3");
                    Toast.makeText(getApplicationContext(), "mensagem 3 enviada ao led03!", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Bluetooth não esta conectado  !", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case SOLICITA_ATIVACAO:
                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(getApplicationContext(), "O seu Bluetooth foi ativado.", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getApplicationContext(), "o Bluetooth não foi ativado,o app será encerrado", Toast.LENGTH_LONG).show();
                    finish();

                }
                break;


            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {

                    MAC = data.getExtras().getString(ListarBluetooth.ENDERECO_MAC);
                    // Toast.makeText(getApplicationContext(), "MAC final : " + MAC, Toast.LENGTH_LONG).show();


                    meuDevice = myBluetoothAdapter.getRemoteDevice(MAC);
                    try {
                        meuSocket = meuDevice.createRfcommSocketToServiceRecord(MEU_UUID);
                        meuSocket.connect();
                        conection = true;

                        connectedThread = new ConnectedThread(meuSocket);
                        connectedThread.start();

                        btConexao.setText("DESCONECTAR");
                        //     connectedThread = new ConnectedThread(meuSocket);
                        //    connectedThread.start();

                        Toast.makeText(getApplicationContext(), "Voce foi conectado com : " + MAC, Toast.LENGTH_LONG).show();
                    } catch (IOException erro) {
                        conection = false;
                        Toast.makeText(getApplicationContext(), " Ocorreu um erro : " + erro, Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(), "FALHA AO OBTER O MAC", Toast.LENGTH_LONG).show();

                }

        }
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs
           /*
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
            */
        }

        /* Call this from the main activity to send data to the remote device */
        public void envia4Pigor(String dadosEnviar) {
            byte[] msgBuffer = dadosEnviar.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        // public void cancel() {
        //    try {
        //         mmSocket.close();
        //    } catch (IOException e) { }
        //}
    }


}
