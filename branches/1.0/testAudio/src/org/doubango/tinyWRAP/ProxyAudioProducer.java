/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.39
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class ProxyAudioProducer {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ProxyAudioProducer(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ProxyAudioProducer obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      tinyWRAPJNI.delete_ProxyAudioProducer(swigCPtr);
    }
    swigCPtr = 0;
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    tinyWRAPJNI.ProxyAudioProducer_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    tinyWRAPJNI.ProxyAudioProducer_change_ownership(this, swigCPtr, true);
  }

  public ProxyAudioProducer() {
    this(tinyWRAPJNI.new_ProxyAudioProducer(), true);
    tinyWRAPJNI.ProxyAudioProducer_director_connect(this, swigCPtr, swigCMemOwn, false);
  }

  public int prepare(int ptime, int rate, int channels) {
    return (getClass() == ProxyAudioProducer.class) ? tinyWRAPJNI.ProxyAudioProducer_prepare(swigCPtr, this, ptime, rate, channels) : tinyWRAPJNI.ProxyAudioProducer_prepareSwigExplicitProxyAudioProducer(swigCPtr, this, ptime, rate, channels);
  }

  public int start() {
    return (getClass() == ProxyAudioProducer.class) ? tinyWRAPJNI.ProxyAudioProducer_start(swigCPtr, this) : tinyWRAPJNI.ProxyAudioProducer_startSwigExplicitProxyAudioProducer(swigCPtr, this);
  }

  public int pause() {
    return (getClass() == ProxyAudioProducer.class) ? tinyWRAPJNI.ProxyAudioProducer_pause(swigCPtr, this) : tinyWRAPJNI.ProxyAudioProducer_pauseSwigExplicitProxyAudioProducer(swigCPtr, this);
  }

  public int stop() {
    return (getClass() == ProxyAudioProducer.class) ? tinyWRAPJNI.ProxyAudioProducer_stop(swigCPtr, this) : tinyWRAPJNI.ProxyAudioProducer_stopSwigExplicitProxyAudioProducer(swigCPtr, this);
  }

  public void setActivate() {
    tinyWRAPJNI.ProxyAudioProducer_setActivate(swigCPtr, this);
  }

  public int push(java.nio.ByteBuffer buffer, long size) {
    return tinyWRAPJNI.ProxyAudioProducer_push(swigCPtr, this, buffer, size);
  }

  public static boolean registerPlugin() {
    return tinyWRAPJNI.ProxyAudioProducer_registerPlugin();
  }

}
