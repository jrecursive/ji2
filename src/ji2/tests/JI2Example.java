package ji2.tests;

import java.util.*;
import com.ericsson.otp.erlang.*;
import ji2.*;

public class JI2Example {

    static private class ExampleOtpProcess extends OtpProcess {
        public void receive(OtpMsg msg) {
            try {
                System.out.println(this.toString() + ": received: " + msg.getSenderPid() + ": " + msg.getMsg());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String args[]) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: JI2Example <node> <cookie> [remoteRPCNode]");
            System.exit(-1);
        }
        String nodeName = args[0];
        String cookie = args[1];
        String rpcNode;
        if (args.length == 3) {
            rpcNode = args[2];
        } else {
            rpcNode = null;
        }
        
        OtpProcessManager mgr = new OtpProcessManager(nodeName, cookie);
        OtpErlangPid pid0 = mgr.spawn(new ExampleOtpProcess());
        OtpErlangPid pid1 = mgr.spawn(new ExampleOtpProcess());
        OtpErlangPid namedProcessPid = mgr.spawn("jiprocess", new ExampleOtpProcess());
        
        OtpNode node = mgr.getNode();
        OtpMbox mbox = node.createMbox("ji2example");
        
        for(int i=0; i<10; i++) {
            OtpErlangString otpStr = 
                new OtpErlangString("Test String " + System.currentTimeMillis());
            mbox.send(pid0, otpStr);
            mbox.send(pid1, otpStr);
            mbox.send(namedProcessPid, otpStr);
        }
        
        if (rpcNode != null) {
            OtpErlangObject[] rpcArgs = new OtpErlangObject[0];
            OtpErlangObject result = mgr.rpc(rpcNode, "erlang", "now", rpcArgs);
            System.out.println("rpc(" + rpcNode + ", erlang, now, []) = " + result.toString());
        }
        
        System.out.println("done!");
        Thread.sleep(5000);
        
        mgr.shutdown();
    }
}
