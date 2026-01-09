import Entitati.Piesa;
import Repository.PiesaRepository;
import Servicii.PlaylistGenerator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MuzicaGUI extends Application {

    private PiesaRepository repository;
    private PlaylistGenerator playlistGenerator;
    private ListView<String> listViewPiese;
    private ComboBox<String> comboGenuri;
    private TextField txtNumePlaylist;

    @Override
    public void start(Stage primaryStage) {
        // Initializare repository
        repository = new PiesaRepository("muzica.db");
        playlistGenerator = new PlaylistGenerator(repository);

        // Layout principal
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Titlu
        Label lblTitlu = new Label("Administrare Piese Muzicale");
        lblTitlu.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Lista piese
        listViewPiese = new ListView<>();
        listViewPiese.setPrefHeight(300);
        incarcaPiese();

        // Sectiune filtrare
        HBox hboxFiltrare = new HBox(10);
        Label lblGen = new Label("Gen muzical:");
        comboGenuri = new ComboBox<>();
        comboGenuri.setPrefWidth(150);

        Button btnResetare = new Button("Resetare");
        btnResetare.setOnAction(e -> resetareFiltrare());

        hboxFiltrare.getChildren().addAll(lblGen, comboGenuri, btnResetare);

        // Incarca genurile in ComboBox
        incarcaGenuri();

        // Listener pentru ComboBox
        comboGenuri.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                filtreazaDupaGen(newValue);
            }
        });

        // Sectiune generare playlist
        HBox hboxPlaylist = new HBox(10);
        Label lblPlaylist = new Label("Nume playlist:");
        txtNumePlaylist = new TextField();
        txtNumePlaylist.setPrefWidth(200);
        txtNumePlaylist.setPromptText("Introduceti numele playlistului");

        Button btnGenereaza = new Button("Generează");
        btnGenereaza.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnGenereaza.setOnAction(e -> genereazaPlaylist());

        hboxPlaylist.getChildren().addAll(lblPlaylist, txtNumePlaylist, btnGenereaza);

        // Separator
        Separator separator = new Separator();

        // Adauga componentele la layout
        root.getChildren().addAll(
                lblTitlu,
                new Label("Lista piese (sortate după formație și titlu):"),
                listViewPiese,
                separator,
                hboxFiltrare,
                hboxPlaylist
        );

        // Scena
        Scene scene = new Scene(root, 700, 550);
        primaryStage.setTitle("Administrare Muzică");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void incarcaPiese() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Piesa piesa : repository.getAll()) {
            String item = String.format("ID: %d | %s - %s | Gen: %s | Durată: %s",
                    piesa.getId(),
                    piesa.getFormatie(),
                    piesa.getTitlu(),
                    piesa.getGenMuzical(),
                    piesa.getDurata()
            );
            items.add(item);
        }

        listViewPiese.setItems(items);
    }

    private void incarcaGenuri() {
        ObservableList<String> genuri = FXCollections.observableArrayList(
                repository.getGenuriFiltrate()
        );
        comboGenuri.setItems(genuri);
    }

    private void filtreazaDupaGen(String gen) {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Piesa piesa : repository.filtrareGenMuzical(gen)) {
            String item = String.format("ID: %d | %s - %s | Gen: %s | Durată: %s",
                    piesa.getId(),
                    piesa.getFormatie(),
                    piesa.getTitlu(),
                    piesa.getGenMuzical(),
                    piesa.getDurata()
            );
            items.add(item);
        }

        listViewPiese.setItems(items);
    }

    private void resetareFiltrare() {
        comboGenuri.setValue(null);
        incarcaPiese();
    }

    private void genereazaPlaylist() {
        String numePlaylist = txtNumePlaylist.getText().trim();

        if (numePlaylist.isEmpty()) {
            afiseazaEroare("Eroare", "Introduceți un nume pentru playlist!");
            return;
        }

        // Valideaza numele (doar litere, cifre si underscore)
        if (!numePlaylist.matches("[a-zA-Z0-9_]+")) {
            afiseazaEroare("Eroare", "Numele poate conține doar litere, cifre și underscore!");
            return;
        }

        // Genereaza playlist
        boolean success = playlistGenerator.genereazaPlaylist(numePlaylist);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succes");
            alert.setHeaderText(null);
            alert.setContentText("Playlist-ul '" + numePlaylist + "' a fost generat cu succes!\n" +
                    "Conține minim 3 piese cu durata totală > 15 minute.\n" +
                    "Nu există două piese consecutive cu aceeași formație sau gen.");
            alert.showAndWait();
            txtNumePlaylist.clear();
        } else {
            afiseazaEroare("Eroare",
                    "Nu s-a putut genera playlist-ul!\n" +
                            "Verificați că există suficiente piese în baza de date\n" +
                            "cu formații și genuri diferite.");
        }
    }

    private void afiseazaEroare(String titlu, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titlu);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }
}