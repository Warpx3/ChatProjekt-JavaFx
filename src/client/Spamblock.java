package client;

import java.io.Serializable;

public class Spamblock extends Transport implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int timeout;

    public Spamblock(int timeout)
    {
        super("Spam");
        this.timeout = timeout;
    }

    public int getTimeout()
    {
        return this.timeout;
    }

}
