package com.example.ikeandmike.field;

/** @brief This class abstracts out the protocols for bluetooth
 * It is used by both and USB connection to the field computer
 */

class BTProtocol {

  public static int TeamNumber = -1;

  public enum Type{
    FIELD,
    STOP,
    RESUME,
    ALERT,
    STATUS,
    HEARTBEAT
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
