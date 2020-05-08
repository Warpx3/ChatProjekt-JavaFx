package client;

import javafx.scene.image.Image;

import java.io.Serializable;

public class Bild extends Transport implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nachricht;
    private Image img;
    private Nickname absender;
    private Nickname empfaenger;

    public Bild(Nickname absender, Nickname empfaenger, String nachricht, Image img) {
        super("Bild");
        this.empfaenger = empfaenger;
        this.absender = absender;
        this.nachricht = nachricht;
        this.img = img;
    }

    public Nickname getAbsender() {
        return absender;
    }

    public void setAbsender(Nickname absender) {
        this.absender = absender;
    }

    public Nickname getEmpfaenger() {
        return empfaenger;
    }

    public void setEmpfaenger(Nickname empfaenger) {
        this.empfaenger = empfaenger;
    }

    public String getNachricht() {
        return nachricht;
    }

    public void setNachricht(String nachricht) {
        this.nachricht = nachricht;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return nachricht;
    }


}
