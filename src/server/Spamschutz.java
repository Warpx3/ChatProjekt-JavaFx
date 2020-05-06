package server;

import java.time.LocalTime;

public class Spamschutz
{
    private int anzahlNachrichten;
    private LocalTime timeout;
    private LocalTime grenze;
    private int timeoutSekunden;
    private int grenzeSekunden;
    private int begrenzungNachrichten;
    private boolean flag;


    public Spamschutz()
    {
        anzahlNachrichten = 0;
        timeoutSekunden = 10;
        grenzeSekunden = 3;
        begrenzungNachrichten = 6;
        flag = false;
    }
    public Spamschutz(int anzahlNachrichten,int timeoutSekunden,int grenzeSekunden, int begrenzungNachrichten)
    {
        this.anzahlNachrichten = anzahlNachrichten;
        this.timeoutSekunden = timeoutSekunden;
        this.grenzeSekunden = grenzeSekunden;
        this.begrenzungNachrichten = begrenzungNachrichten;
        flag = false;
    }

    public void setzeZeit()
    {
        grenze = LocalTime.now().plusSeconds(grenzeSekunden);
    }

    public int getAnzahl()
    {
        return anzahlNachrichten;
    }
    public int getTimeoutSekunden() {return timeoutSekunden;}

    public boolean checkErlaubt()
    {
        boolean erlaubt;

        if(anzahlNachrichten < begrenzungNachrichten && LocalTime.now().isBefore(grenze))
        {
            erlaubt = true;
        }
        else
        {
            erlaubt = false;
            if(!flag)
            {
                timeout = LocalTime.now().plusSeconds(timeoutSekunden);
                flag = true;
            }
        }
        anzahlNachrichten++;

        if(!erlaubt && LocalTime.now().isAfter(timeout))
        {
            resetAnzahlNachrichten();
            flag = false;
        }

        return erlaubt;
    }
    public void resetAnzahlNachrichten()
    {
        anzahlNachrichten = 0;
    }

}
