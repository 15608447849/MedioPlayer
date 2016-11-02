// ICommunicationAIDL.aidl
package lzp.yw.com.medioplayer.model_communication;
import lzp.yw.com.medioplayer.model_communication.ICallBackAIDL;
// Declare any non-default types here with import statements

interface ICommunicationAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendRequest(String command,String msg);
    void receiveResult(ICallBackAIDL cb);
}
