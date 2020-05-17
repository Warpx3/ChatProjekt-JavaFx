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
	protected Thread aktivCheck;
	private boolean aktivFlag;
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
				//Dazu da um das Zeitfenster f�r den Spamschutz zu starten.
				if(!t.getIdentifier().equalsIgnoreCase("Registrierung") && !t.getIdentifier().equalsIgnoreCase("AnmeldeObjekt") && !t.getIdentifier().equalsIgnoreCase("aktivCheck"))
				{
					if (schutz.getAnzahl() == 0)
					{
						schutz.setzeZeit();
					}
				}
				//if-else f�r eine einfachere Spaltung von Nachrichtenobjekte jeglicher Art wie z.b Nachrichten, Bilder, Gif,..(in else) und Anmelde und Registrierungsobjekte
				//Damit entsteht keine Redundanz mehr f�r die Pr�fung des Spamschutzes
				if(t.getIdentifier().equalsIgnoreCase("Registrierung") || t.getIdentifier().equalsIgnoreCase("AnmeldeObjekt") || t.getIdentifier().equalsIgnoreCase("aktivCheck"))
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
						case "aktivCheck":
							aktivFlag = true;
							break;
						default: break;
					}
				}
				else
				{
					if(schutz.checkErlaubt())
					{
						aktivFlag = true;
						switch(t.getIdentifier())
						{
							case "Nachricht":
								Nachricht n = (Nachricht) o;
								aServer.broadcast(n);
								break;
							case "privateNachricht":
								aServer.privateObjectSenden(o);
								break;
							case "Bild":
								aServer.privateObjectSenden(o);
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
			System.out.println("Empf�nger nicht erreichbar!");
		}
	}

	public void clientKick()
	{
		sendeObject(new AktivitaetsCheck());

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


	public Nickname getNick()
	{
		return nick;
	}


	public void setNick(Nickname nick)
	{
		this.nick = nick;
	}

	public Socket getaSocket(){return aSocket;}
	public static int getBenutzer(){return benutzer;}

	public boolean getAktivFlag(){return this.aktivFlag;}
	public void setAktivFlag(boolean flag){this.aktivFlag = flag;}

	public void setAktivCheck(Thread t){this.aktivCheck = t;}
	public Thread getAktivCheck(){return this.aktivCheck;}
	
}
