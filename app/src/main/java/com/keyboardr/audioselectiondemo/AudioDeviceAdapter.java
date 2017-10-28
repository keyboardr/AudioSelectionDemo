package com.keyboardr.audioselectiondemo;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class AudioDeviceAdapter extends Adapter<AudioDeviceAdapter.DeviceHolder> {
  private List<ParcelableAudioDeviceInfo> devices;

  @Override
  public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new DeviceHolder(LayoutInflater.from(parent.getContext())
        .inflate(android.R.layout.two_line_list_item, parent, false));
  }

  @Override
  public void onBindViewHolder(DeviceHolder holder, int position) {
    holder.setDevice(devices.get(position));
  }

  @Override
  public int getItemCount() {
    return devices == null ? 0 : devices.size();
  }

  public void setDevices(@Nullable List<ParcelableAudioDeviceInfo> devices) {
    this.devices = devices;
    notifyDataSetChanged();
  }

  public static class DeviceHolder extends RecyclerView.ViewHolder {
    TextView text1;
    TextView text2;

    public DeviceHolder(View itemView) {
      super(itemView);
      text1 = itemView.findViewById(android.R.id.text1);
      text2 = itemView.findViewById(android.R.id.text2);
    }


    public void setDevice(ParcelableAudioDeviceInfo audioDeviceInfo) {
      text1.setText(audioDeviceInfo.name);
      text2.setText(String.format(text2.getContext().getString(R.string.type_format),
          audioDeviceInfo.type, audioDeviceInfo.id));
    }
  }
}
