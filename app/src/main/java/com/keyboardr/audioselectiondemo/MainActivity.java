package com.keyboardr.audioselectiondemo;

import android.arch.lifecycle.Transformations;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class MainActivity extends FragmentActivity {

  Messenger toService;
  Messenger fromService = new Messenger(new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.getData() != null) {
        msg.getData().setClassLoader(getClassLoader());
        if (msg.getData().containsKey(BoundService.DATA_DEVICES)) {
          setRemoteDevices(msg.getData().getParcelableArrayList(BoundService.DATA_DEVICES));
        }
      }
    }
  });
  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      toService = new Messenger(iBinder);
      Message obtain = Message.obtain();
      obtain.replyTo = fromService;
      try {
        toService.send(obtain);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
      toService = null;
    }
  };
  private AudioDeviceAdapter remoteAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RecyclerView mainList = findViewById(R.id.main_list);
    mainList.setLayoutManager(new LinearLayoutManager(this));
    final AudioDeviceAdapter adapter = new AudioDeviceAdapter();
    mainList.setAdapter(adapter);
    Transformations.map(new AudioDeviceLiveData(this),
        input -> input.stream()
            .map(ParcelableAudioDeviceInfo::new)
            .sorted(comparingInt(item -> item.id))
            .collect(toList()))
        .observe(this, adapter::setDevices);


    RecyclerView remoteList = findViewById(R.id.remote_list);
    remoteList.setLayoutManager(new LinearLayoutManager(this));
    remoteAdapter = new AudioDeviceAdapter();
    remoteList.setAdapter(remoteAdapter);
    bindService(new Intent(this, BoundService.class), serviceConnection, BIND_AUTO_CREATE);
  }

  private void setRemoteDevices(List<ParcelableAudioDeviceInfo> devices) {
    remoteAdapter.setDevices(devices);
  }
}
