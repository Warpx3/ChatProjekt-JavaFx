package server;

import java.time.LocalTime;

public class AktivKontrolle extends Thread
{
    private ClientProxy proxy;
    private long zeitspanne;

    public AktivKontrolle(ClientProxy proxy,long minutenspanne)
    {
        this.proxy = proxy;
        this.zeitspanne = minutenspanne;


    }

    @Override
    public void run()
    {
        waitTime();

        while(proxy.getAktivFlag() && !isInterrupted())
        {
            waitTime();
        }

        if(!isInterrupted())
        {
            proxy.clientKick();
        }

    }

    public void waitTime()
    {
        proxy.setAktivFlag(false);

        try
        {
            Thread.sleep((zeitspanne*60*1000));
        }
        catch(InterruptedException exp)
        {
            System.out.println("Connection wurde beendet!");
        }
    }
}
