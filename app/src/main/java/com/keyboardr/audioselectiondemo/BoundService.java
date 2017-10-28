package com.keyboardr.audioselectiondemo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Transformations;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class BoundService extends LifecycleService {
  public static final String DATA_DEVICES = "devices";
  private final Messenger messenger = new Messenger(new IncomingHandler());
  private Messenger toClient;
  private List<ParcelableAudioDeviceInfo> devices;

  @Override
  public IBinder onBind(Intent intent) {
    super.onBind(intent);
    return messenger.getBinder();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Transformations.map(new AudioDeviceLiveData(this),
        input -> input.stream()
            .map(ParcelableAudioDeviceInfo::new)
            .sorted(comparingInt(item -> item.id))
            .collect(toList()))
        .observe(this, this::setDevices);
  }

  private void setDevices(List<ParcelableAudioDeviceInfo> parcelableAudioDeviceInfos) {
    devices = parcelableAudioDeviceInfos;
    if (toClient == null) {
      return;
    }
    Bundle data = new Bundle();
    data.putParcelableArrayList(DATA_DEVICES,
        parcelableAudioDeviceInfos == null ? null : new ArrayList<>(parcelableAudioDeviceInfos));
    Message message = Message.obtain();
    message.setData(data);
    try {
      toClient.send(message);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @SuppressLint("HandlerLeak")
  class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (msg.replyTo != null) {
        toClient = msg.replyTo;
        setDevices(devices);
      }
    }
  }
}
