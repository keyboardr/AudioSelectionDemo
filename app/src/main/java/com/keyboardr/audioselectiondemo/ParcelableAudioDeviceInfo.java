package com.keyboardr.audioselectiondemo;


import android.media.AudioDeviceInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableAudioDeviceInfo implements Parcelable {

  public final String name;
  public final int type;
  public final int id;

  public ParcelableAudioDeviceInfo(AudioDeviceInfo audioDeviceInfo) {
    this.name = audioDeviceInfo.getProductName().toString();
    this.type = audioDeviceInfo.getType();
    this.id = audioDeviceInfo.getId();
  }

  protected ParcelableAudioDeviceInfo(Parcel in) {
    name = in.readString();
    type = in.readInt();
    id = in.readInt();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeInt(type);
    dest.writeInt(id);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<ParcelableAudioDeviceInfo> CREATOR = new Creator<ParcelableAudioDeviceInfo>() {
    @Override
    public ParcelableAudioDeviceInfo createFromParcel(Parcel in) {
      return new ParcelableAudioDeviceInfo(in);
    }

    @Override
    public ParcelableAudioDeviceInfo[] newArray(int size) {
      return new ParcelableAudioDeviceInfo[size];
    }
  };
}
