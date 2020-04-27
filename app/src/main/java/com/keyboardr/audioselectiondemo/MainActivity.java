package com.keyboardr.audioselectiondemo;

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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class MainActivity extends FragmentActivity {

  Messenger toService;
  Messenger fromService = new Messenger(new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(@NonNull Message msg) {
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
