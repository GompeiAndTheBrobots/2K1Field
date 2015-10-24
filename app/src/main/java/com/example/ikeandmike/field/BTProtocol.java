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
}
