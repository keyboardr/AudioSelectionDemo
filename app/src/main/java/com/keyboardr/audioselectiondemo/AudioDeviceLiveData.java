package com.keyboardr.audioselectiondemo;

import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.util.ArraySet;

import java.util.Arrays;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import static java.util.Objects.requireNonNull;

public class AudioDeviceLiveData extends LiveData<Set<AudioDeviceInfo>> {

  @NonNull
  private Set<AudioDeviceInfo> devices = new ArraySet<>();
  @NonNull
  private final AudioManager audioManager;


  private AudioDeviceCallback audioDeviceCallback = new AudioDeviceCallback() {
    @Override
    public void onAudioDevicesAdded(@NonNull AudioDeviceInfo[] addedDevices) {
      devices.addAll(Arrays.asList(addedDevices));
      setDevices();
    }

    @Override
    public void onAudioDevicesRemoved(@NonNull AudioDeviceInfo[] removedDevices) {
      for (final AudioDeviceInfo device : removedDevices) {
        devices.removeIf(audioDeviceInfo -> audioDeviceInfo.getId() == device.getId());
      }
      setDevices();
    }
  };

  private void setDevices() {
    postValue(devices);
  }

  public AudioDeviceLiveData(Context context) {
    audioManager = requireNonNull(context.getSystemService(AudioManager.class));
  }

  @Override
  protected void onActive() {
    super.onActive();
    audioManager.registerAudioDeviceCallback(audioDeviceCallback, null);
  }

  @Override
  protected void onInactive() {
    super.onInactive();
    audioManager.unregisterAudioDeviceCallback(audioDeviceCallback);
  }
}
