package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.*;


public class ClientProxy implements Runnable
{

	private ServerControl aServer;
	private Socket aSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Nickname nick;
	private AnmeldeObjekt ao;
	private Spamschutz schutz;
	protected Thread t;
	static int benutzer = 0;
	
	
	public ClientProxy(ServerControl control,Socket s)
	{
		this.aServer = control;
		this.aSocket = s;
		this.nick = new Nickname("", "");
		benutzer++;
		
		try
		{
			this.in = new ObjectInputStream(aSocket.getInputStream());
			this.out = new ObjectOutputStream(aSocket.getOutputStream());
			
			t = new Thread(this);
			t.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void run()
	{
		System.out.println("Client wurde angemeldet!");


		schutz = new Spamschutz();


		while(!t.isInterrupted())
		{
			try
			{
				Thread.sleep(100);
				empfangeNachricht();
			}
			catch(InterruptedException e)
			{
				t.interrupt();
			}
		}
	}
	
	private void empfangeNachricht()
	{
		try
		{
			Object o = (Object) in.readObject();
			Transport t = (Transport) o;
			
			if(t != null)
			{
				//Dazu da um das Zeitfenster für den Spamschutz zu starten.
				if(!t.getIdentifier().equalsIgnoreCase("Registrierung") && !t.getIdentifier().equalsIgnoreCase("AnmeldeObjekt"))
				{
					if (schutz.getAnzahl() == 0)
					{
						schutz.setzeZeit();
					}
				}
				//if-else für eine einfachere Spaltung von Nachrichtenobjekte jeglicher Art wie z.b Nachrichten, Bilder, Gif,..(in else) und Anmelde und Registrierungsobjekte
				//Damit entsteht keine Redundanz mehr für die Prüfung des Spamschutzes
				if(t.getIdentifier().equalsIgnoreCase("Registrierung") || t.getIdentifier().equalsIgnoreCase("AnmeldeObjekt"))
				{
					switch(t.getIdentifier())
					{
						case "Registrierung":
							Registrierung reg = (Registrierung) o;
							aServer.registrierungPruefen(reg);
							break;
						case "AnmeldeObjekt":
							ao = (AnmeldeObjekt) o;
							aServer.anmelden(ao);
							break;
						default: break;
					}
				}
				else
				{
					if(schutz.checkErlaubt())
					{
						switch(t.getIdentifier())
						{
							case "Nachricht":
								Nachricht n = (Nachricht) o;
								aServer.broadcast(n);
								break;
							case "privateNachricht":
								PrivateNachricht pn = (PrivateNachricht) o;
								aServer.privateNachrichtSenden(pn);
								break;
							default: break;
						}
					}
					else
					{
						Spamblock block = new Spamblock(schutz.getTimeoutSekunden());
						aServer.spamschutzNachricht(block,this);
					}
				}
/*
				switch(t.getIdentifier()) {
					case "Nachricht":
						Nachricht n = (Nachricht) o;
						aServer.broadcast(n);
						break;
					case "Registrierung":
						Registrierung reg = (Registrierung) o;
						aServer.registrierungPruefen(reg);
						break;
					case "AnmeldeObjekt":
						ao = (AnmeldeObjekt) o;
						aServer.anmelden(ao);
						break;
					case "privateNachricht":
						PrivateNachricht pn = (PrivateNachricht) o;
						aServer.privateNachrichtSenden(pn);
						break;
					default:
						break;
				}

 */
			}
		}
		catch (ClassNotFoundException | IOException e)
		{
			AktiveNutzerUpdate anu = new AktiveNutzerUpdate(nick, false); 

			if(nick != null)
			{
				aServer.aktivenBenutzerEntfernen(nick.getEmail());
			}
			else
			{
				aServer.aktivenBenutzerEntfernen(ao.getEmail());
			}
			aServer.notifyObserver(anu);
			this.t.interrupt();
		}
	}
	
	public void sendeObject(Object o)
	{
		try
		{
			out.writeObject(o);
			out.flush();
		}
		catch (IOException e)
		{
			System.out.println("Empfänger nicht erreichbar!");
		}
	}


	public Nickname getNick()
	{
		return nick;
	}


	public void setNick(Nickname nick)
	{
		this.nick = nick;
	}
	
	
}
