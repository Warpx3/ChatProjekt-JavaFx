package server;

import java.net.Socket;
import java.util.ArrayList;

public class DosProtection
{
    //Check ob IP bereits vorhanden und max Anzahl an Nutzern
    public boolean joinCheck (Socket socket, ArrayList<ClientProxy> liste)
    {
        if(liste.isEmpty() == false)
        {
            if(liste.get(0).getBenutzer() < 50)
            {
                for (ClientProxy clientProxy : liste)
                {
                    if(clientProxy.getaSocket().getInetAddress().equals(socket.getInetAddress()))
                    {
                        System.out.println(socket.getInetAddress() + " " + clientProxy.getaSocket().getInetAddress());
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
