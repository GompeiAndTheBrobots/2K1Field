package com.example.ikeandmike.field;

/** @brief This class abstracts out the protocols for bluetooth
 * It is used by both and USB connection to the field computer
 */

class BTProtocol {

  public static int TeamNumber = -1;

  public enum Type{
    // 5 is for all the necessary stuff
    FIELD ((byte)0x6, (byte) 0x1),
    ALERT ((byte) 0x6, (byte) 0x3),
    STOP ((byte) 0x5, (byte) 0x4),
    RESUME ((byte) 0x5, (byte) 0x5),
    STATUS ((byte) 0x6, (byte) 0x6),
    HEARTBEAT ((byte) 0x5, (byte) 0x7),
    OTHER ((byte) 0xFF, (byte) 0xFF);

    private final byte id;
    private final byte length;

    Type(byte length, byte id){
      this.length = length;
      this.id = id;
    }

    public byte length(){ return length;}
    public byte id(){ return id;}
  }

  public Type messageType;

  private byte[] data;

  /** @brief give it the data, you get a packet
   */
  public BTProtocol(Type type, byte[] data){
    this.messageType = type;
    this.data = data;
  }

  public static final int REQUEST_ENABLE_BT  = 1;

  private static byte calcChecksum(byte[] packet){
    // 0xff minus the 8 bit sum of bytes from
    // offset 1 up to but no including this byte
    byte sum = 0;
    for (byte b : packet){
      sum += b;
    }

    return (byte)(0xff - sum);
  }

  public static byte[] createPacket(BTProtocol.Type type, byte[] data){
    //construct packet based on BT spec
    byte packet[] = new byte[data.length + 6];
    packet[0] = 0x5F;
    packet[1] = (byte) (type.length() + 5);
    packet[2] = type.id();
    packet[3] = 0x0;
    packet[4] = (byte) BTProtocol.TeamNumber;

    //send the actual data
    int i = 5;
    for (; i < 5 + data.length; i++) {
      packet[i] = data[i - 5];
    }

    packet[i] = calcChecksum(packet);
    return packet;
  }
}
