package com.example.teo.servicio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

public class WifiTester extends Service {

    public static final String tag = "WifiTester";
    Boolean servicioActivado;
    Boolean wifiActivada;
    public WifiTester() {
        servicioActivado = false;
        wifiActivada = false;
    }

    public Boolean getEstadoWifi() {
        return wifiActivada;
    }

    public void setEstadoWifi(Boolean estadoWifi) {
        wifiActivada = estadoWifi;
    }

    public void cambiarEstadoWifi() {
        wifiActivada = !wifiActivada;
    }

    public Boolean estadoServicio() {
        return servicioActivado;
    }

    public void setEstadoServicio(Boolean servicioActivado) {
        this.servicioActivado = servicioActivado;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //En el servicio no vinculado retornamos null
       return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "Creado el servicio.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!estadoServicio()){
            Log.d(tag, "Iniciando el servicio");
            setEstadoServicio(true);
            new WifiHilo().start();
                        //Aquí lanzamos el hilo.


        }else{
            Log.d(tag,"El servicio ya estaba iniciado");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setEstadoServicio(false);
        Log.d(tag,"Servicio parado");
    }

    public class WifiHilo extends Thread{
        @Override
        public void run() {
            super.run();
            while (estadoServicio()){
                try {
                    if(getEstadoWifi() != comprobarWifiActivado()){
                        cambiarEstadoWifi();
                        if(getEstadoWifi() == true){
                            Log.d(tag,"Estás conectado por WiFi");
                        }else{
                            Log.d(tag,"No estás conectado por WiFi");
                        }
                    }
                    this.sleep(3000);
                }
                catch (InterruptedException e){
                    setEstadoServicio(false);
                    Log.d(tag, "El hilo se ha parado");
                }
            }
        }

        public Boolean comprobarWifiActivado() {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    }
                }
            }
                return false;
        }
    }



}
