package ji2;

import java.util.*;
import java.util.concurrent.*;
import com.ericsson.otp.erlang.*;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.channels.*;
import org.jetlang.fibers.Fiber;

public abstract class OtpProcess implements Callback<OtpMsg> {
    private String name = null;
    
    private OtpMbox mbox = null;
    private OtpErlangPid pid = null;
    
    private Fiber fiber = null;
    private Channel channel = null;
    
    private OtpProcessManager owner = null;
    
    protected void setName(String name) {
        this.name = name;
    }
    
    protected void setMbox(OtpMbox mbox) {
        this.mbox = mbox;
    }
    
    protected void setPid(OtpErlangPid pid) {
        this.pid = pid;
    }
    
    protected void setFiber(Fiber fiber) {
        this.fiber = fiber;
    }
    
    protected void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    protected void setOwner(OtpProcessManager owner) {
        this.owner = owner;
    }
    
    public String getName() {
        return name;
    }
    
    public OtpMbox getMbox() {
        return mbox;
    }
    
    public OtpErlangPid getPid() {
        return pid;
    }
    
    protected Channel getChannel() {
        return channel;
    }
    
    protected Fiber getFiber() {
        return fiber;
    }
    
    private OtpProcessManager getOwner() {
        return owner;
    }
    
    // internal api
    
    protected void cast(OtpMsg msg) {
        channel.publish(msg);
    }
    
    protected void kill(String reason) {
        fiber.dispose();
        mbox.exit(reason);
        // TODO: channel?
    }
    
    protected void kill() {
        kill("killed");
    }
    
    public void onMessage(OtpMsg msg) {
        receive(msg);
    }

    // public api
    
    abstract public void receive(OtpMsg msg);
}



