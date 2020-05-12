package client;

import java.io.Serializable;

public class Heuler extends Transport implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String nachricht;
	private Nickname nickname;
	
	public Heuler(String nachricht, Nickname nickname)
	{
		super("Heuler");
		this.nachricht = nachricht;
		this.nickname = nickname;
	}
	
	public String getNachricht()
	{
		return nachricht;
	}
	public void setNachricht(String nachricht)
	{
		this.nachricht = nachricht;
	}
	public Nickname getNickname()
	{
		return nickname;
	}
	public void setNickname(Nickname nickname)
	{
		this.nickname = nickname;
	}
}
