package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class GuiControllerPrivat
{
	// ClientPrivat-GUI
		@FXML
		private TextField textField_fluesterNachricht;
		@FXML
		private ListView list_fluesterNachricht;
		@FXML
		private Button btn_Senden;
		//Bild einfügen-GUI
		@FXML
		private ImageView imageViewBild;
		//Temp. Variable für das Bild
		private Image imgBild;
		//Bild anzeigen-GUI
		@FXML
		private ImageView dndAnzeigenContent;
		@FXML
		private Label dndAnzeigenLabel;


		private Nickname empfaenger;
		private GuiController guiController;
		private ClientControl clientControl;
		private Stage stage;
		
		public GuiControllerPrivat(Nickname empfaenger, GuiController guiController, ClientControl clientControl) throws IOException
		{
			this.empfaenger = empfaenger;
			this.guiController = guiController;
			this.clientControl = clientControl;
		}
		
		@FXML
		public void privateNachrichtSenden()
		{
			clientControl.privateNachrichtSenden(empfaenger, this);
			textField_fluesterNachricht.setText("");
		}
		
		//ClientPrivat
		public TextField getTextField_fluesterNachricht()
		{
			return textField_fluesterNachricht;
		}

		public void setTextField_fluesterNachricht(TextField textField_fluesterNachricht)
		{
			this.textField_fluesterNachricht = textField_fluesterNachricht;
		}

		public ListView getList_fluesterNachricht()
		{
			return list_fluesterNachricht;
		}

		public void setList_fluesterNachricht(ListView list_fluesterNachricht)
		{
			this.list_fluesterNachricht = list_fluesterNachricht;
		}

		public Nickname getEmpfaenger()
		{
			return empfaenger;
		}

		public void setEmpfaenger(Nickname empfaenger)
		{
			this.empfaenger = empfaenger;
		}

		public Button getBtn_Senden(){return this.btn_Senden;}

		public Stage getStage()
		{
			return stage;
		}

		public void setStage(Stage stage)
		{
			this.stage = stage;
		}
		//ClientPrivat-Ende

	//Auswahl-Menü (Button [ + ] )
	//Auswahl - Smiley einfügen
	@FXML
	private void handleMenuClicked1(ActionEvent event) throws IOException {
		/*Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					guiController.stageAnlegen("DnDBild", empfaenger);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});*/

	}

	//Auswahl - Bild einfügen
	@FXML
	private void handleMenuClicked2(ActionEvent event) throws IOException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					guiController.stageAnlegen("DnDBild", empfaenger);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void handleDragOverBild(DragEvent dragEvent) {
		final Dragboard db = dragEvent.getDragboard();

		final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
				|| db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");

		if (db.hasFiles()) {
			if (isAccepted) {
				dragEvent.acceptTransferModes(TransferMode.COPY);
			}
		} else {
			dragEvent.consume();
		}

	}

	public void handleDragDroppedBild(DragEvent dragEvent) throws FileNotFoundException {
		List<File> files = dragEvent.getDragboard().getFiles();
		imgBild = new Image(new FileInputStream(files.get(0)));
		imageViewBild.setImage(imgBild);
	}

	public void handleSendenBild(ActionEvent actionEvent) throws IOException {
		if (imgBild != null) {
			Bild versendetesBild = new Bild(clientControl.getNickname(), empfaenger);
			versendetesBild.imageToByteArray(imgBild);
			clientControl.privateBilderSenden(empfaenger, versendetesBild);
		} else {
			System.out.println("Drag&Drop: Kein Bild eingefügt");
		}

	}

	//Getter und Setter für Bild anzeigen-GUI
	public ImageView getDndAnzeigenContent() {
		return dndAnzeigenContent;
	}

	public void setDndAnzeigenContent(ImageView dndAnzeigenContent) {
		this.dndAnzeigenContent = dndAnzeigenContent;
	}

	public Label getDndAnzeigenLabel() {
		return dndAnzeigenLabel;
	}

	public void setDndAnzeigenLabel(Label dndAnzeigenLabel) {
		this.dndAnzeigenLabel = dndAnzeigenLabel;
	}
}
