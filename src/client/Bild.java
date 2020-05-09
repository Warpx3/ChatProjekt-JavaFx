package client;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


public class Bild extends Transport implements Serializable
{
    private static final long serialVersionUID = 1L;
    private byte[] imgBytes;
    private Nickname absender;
    private Nickname empfaenger;



    public Bild(Nickname absender, Nickname empfaenger)
    {
        super("Bild");
        this.empfaenger = empfaenger;
        this.absender = absender;
    }

    public Nickname getAbsender()
    {
        return absender;
    }

    public void setAbsender(Nickname absender)
    {
        this.absender = absender;
    }

    public Nickname getEmpfaenger()
    {
        return empfaenger;
    }

    public void setEmpfaenger(Nickname empfaenger)
    {
        this.empfaenger = empfaenger;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public void setImgBytes(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }

    public void imageToByteArray(Image img) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
        ImageIO.write(bufferedImage, "jpg", bos);
        imgBytes = bos.toByteArray();
        bos.close();
    }

    public Image byteArrayToImage() throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imgBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);
        Image img = SwingFXUtils.toFXImage(bufferedImage, null);
        return img;
    }
}
