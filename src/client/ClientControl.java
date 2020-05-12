package client;

import javax.swing.*;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;

public class ClientControl implements Runnable
{

	private Socket aSocket;
	private InputStream inStream;
	private OutputStream outStream;
	private ObjectInputStream oin = null;
	private ObjectOutputStream oout = null;
	
	private String ip = "localhost";
	private Nickname nickname;
	private int port = 8088;
	
	private AktiveNutzer an;
	private ArrayList<GuiControllerPrivat> privateChatraeume = new ArrayList<>();
	
	private GuiController guiController;
	
	private Thread t;
	
	public ClientControl(GuiController guiController)
	{
		this.guiController = guiController;
	}
	
	public void clientStart()
	{
		try
		{
			verbinden(ip,port);
		}
		catch(UnknownHostException e)
		{
			System.out.println("Unbekannte IP-Adresse!");
			e.printStackTrace();
		}
		catch(IOException e)
		{
			System.out.println("Fehler beim Verbindungsaufbau!");
			e.printStackTrace();
		}
	}
	
	public void verbinden(String ip, int port) throws UnknownHostException,IOException
	{
		aSocket = new Socket(ip,port);
		oout = new ObjectOutputStream(aSocket.getOutputStream());
		oin = new ObjectInputStream(aSocket.getInputStream());
		System.out.println("Verbindung mit dem Server wurde hergestellt!");
		
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run()
	{
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
	
	public void empfangeNachricht()
	{		
		try
		{
			Object o = oin.readObject();
			Transport t = (Transport) o;
			
			if(t != null)
			{
				switch(t.getIdentifier())
				{
					case "Nachricht":
						Nachricht n = (Nachricht) o;
						guiController.itemsZurListeHinzufuegen(guiController.getListView_Nachrichten(), n);
						break;
					case "AnmeldeBestaetigung": 
						AnmeldeBestaetigung a = (AnmeldeBestaetigung) o;
						nickname = a.getNickname();
						guiController.guiAnzeigen("Chat");
						break;
					case "aktiveNutzer":
						an = (AktiveNutzer) o;
						if(an.getBenutzer() != null && an != null)
						{
							guiController.getListView_angemeldeteNutzer().getItems().addAll(an.getBenutzer());
						}
						an = null;
						break;
					case "aktiveNutzerUpdate":
						AktiveNutzerUpdate anu = (AktiveNutzerUpdate) o;
					
						if(anu.isHinzufuegen())
						{
							guiController.itemsZurListeHinzufuegen(guiController.getListView_angemeldeteNutzer(), anu.getNick());
						}
						else
						{
							guiController.itemsVonListeEntfernen(guiController.getListView_angemeldeteNutzer(), anu.getNick());
						}
						break;
					case "privateNachricht":
						PrivateNachricht pn = (PrivateNachricht) o;
						guiController.itemsZurListeHinzufuegen(clientPrivatOeffnen(pn.getAbsender()).getList_fluesterNachricht(), pn);
						break;
					case "Bild":
						Bild bi = (Bild) o;
						Platform.runLater(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									GuiControllerPrivat gCP = null;
									gCP = guiController.stageAnlegen("AnzDnD", bi.getEmpfaenger());
									gCP.getDndAnzeigenLabel().setText(bi.getAbsender() + " hat dir ein Bild gesendet:");
									gCP.getDndAnzeigenContent().setImage(bi.byteArrayToImage());
								} catch (IOException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						break;
					case "Spam":
						Spamblock block = (Spamblock) o;
						guiController.getTextFieldNachricht().setDisable(true);
						guiController.getBtnSenden().setDisable(true);
						guiController.getListView_angemeldeteNutzer().setDisable(true);
						for (GuiControllerPrivat c: privateChatraeume)
						{
							c.getBtn_Senden().setDisable(true);
							c.getTextField_fluesterNachricht().setDisable(true);
						}
						Thread timer = new Thread(new Runnable() {
							@Override
							public void run() {
								System.out.println("Bin im Thread!");
								try
								{
									Thread.sleep((block.getTimeout()*1000));
								}
								catch(InterruptedException exp)
								{
									exp.printStackTrace();
								}

								guiController.getTextFieldNachricht().setDisable(false);
								guiController.getBtnSenden().setDisable(false);
								guiController.getListView_angemeldeteNutzer().setDisable(false);

								for (GuiControllerPrivat c: privateChatraeume)
								{
									c.getBtn_Senden().setDisable(false);
									c.getTextField_fluesterNachricht().setDisable(false);
								}

							}
						});
						timer.start();
						break;
					case "Heuler":
						Heuler heuler = (Heuler) o;
						JOptionPane.showMessageDialog(null, heuler.getNachricht(), "Fehler", JOptionPane.PLAIN_MESSAGE);
						break;
					default: break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendeObject(Object o)
	{
		try
		{
			oout.writeObject(o);
			oout.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Nachricht createNachricht(String s)
	{
		return new Nachricht(nickname.getName(), s);
	}
	
	public void oeffneRegistrierung() throws IOException
	{
		guiController.guiAnzeigen("Registrieren");
	}

	public void registrieren()
	{
		String pw = guiController.getPasswordField_passwortRegistrieren().getText();
		String pwBestaetigung = guiController.getPasswordField_passwortWiederholenRegistrieren().getText();
		String nickname = guiController.getTextField_nicknameRegistrieren().getText();
		String email = guiController.getTextField_emailadresseRegistrieren().getText();
		
		if(pw != null && pwBestaetigung != null && nickname != null && email != null)
		{
			if(pw.equals(pwBestaetigung))
			{
				Registrierung reg = new Registrierung(email, nickname, pw);
				sendeObject(reg);
				
				//#TODO
				//Nickname verschlï¿½sseln
				
			}
			else
			{
				JOptionPane.showMessageDialog(null,"Passwörter stimmen nicht überein.","Fehler", JOptionPane.PLAIN_MESSAGE);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Bitte füllen Sie alle Felder aus.","Fehler", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	public void clientAnmelden()
	{
		String pw = guiController.getPasswordField_passwortAnmelden().getText();
		String email = guiController.getTextField_emailadresseAnmelden().getText();
		
		if(pw != null && email != null)
		{
			AnmeldeObjekt ao = new AnmeldeObjekt(email, pw);
			sendeObject(ao);
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Bitte fï¿½llen Sie alle Felder aus.","Fehler", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public GuiControllerPrivat clientPrivatOeffnen(Nickname empfaenger) throws IOException
	{
		Boolean flag = true;
		GuiControllerPrivat guiControllerPrivat = null;
		
		for(GuiControllerPrivat gCP : privateChatraeume)
		{
			if(gCP.getEmpfaenger().getEmail().equals(empfaenger.getEmail()))
			{
				flag = false;
				guiControllerPrivat = gCP;
			}
		}
		
		if(flag)
		{
			guiControllerPrivat = guiController.stageAnlegen("ClientPrivat", empfaenger);
			privateChatraeume.add(guiControllerPrivat);
		}
		return guiControllerPrivat;
	}

	public void privateNachrichtSenden(Nickname empfaenger, GuiControllerPrivat gCP)
	{
		PrivateNachricht pn = new PrivateNachricht(nickname, empfaenger, gCP.getTextField_fluesterNachricht().getText());
		sendeObject(pn);
		gCP.getList_fluesterNachricht().getItems().add(pn);
	}

	public void privateBilderSenden(Nickname empfaenger, Bild versendetesBild)
	{

		sendeObject(versendetesBild);
	}
	
	public DefaultListModel<Nickname> convertArrayListToDefaultListModel(ArrayList<Nickname> arrayList)
	{
		DefaultListModel<Nickname> modelAktiveNicks = new DefaultListModel<>();
		
		for(Nickname nick : arrayList)
		{
			modelAktiveNicks.addElement(nick);
		}
		
		return modelAktiveNicks;
	}

	public Nickname getNickname()
	{
		return nickname;
	}

	public ArrayList<GuiControllerPrivat> getPrivateChatraeume()
	{
		return privateChatraeume;
	}
	
}
