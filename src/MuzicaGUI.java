import Entitati.Piesa;
import Repository.PiesaRepository;
import Servicii.PlaylistGenerator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
@SuppressWarnings("unchecked")
public class MuzicaGUI extends Application {

    private PiesaRepository repository;
    private PlaylistGenerator playlistGenerator;
    private TableView<Piesa> tablePiese;
    private ComboBox<String> comboGenuri;
    private TextField txtNumePlaylist;

    // Campuri pentru CRUD
    private TextField txtId;
    private TextField txtFormatie;
    private TextField txtTitlu;
    private TextField txtGen;
    private TextField txtDurata;

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

        // Tabel piese
        tablePiese = new TableView<>();
        tablePiese.setPrefHeight(280);

        TableColumn<Piesa, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Piesa, String> colFormatie = new TableColumn<>("Formație");
        colFormatie.setCellValueFactory(new PropertyValueFactory<>("formatie"));
        colFormatie.setPrefWidth(150);

        TableColumn<Piesa, String> colTitlu = new TableColumn<>("Titlu");
        colTitlu.setCellValueFactory(new PropertyValueFactory<>("titlu"));
        colTitlu.setPrefWidth(200);

        TableColumn<Piesa, String> colGen = new TableColumn<>("Gen Muzical");
        colGen.setCellValueFactory(new PropertyValueFactory<>("genMuzical"));
        colGen.setPrefWidth(120);

        TableColumn<Piesa, String> colDurata = new TableColumn<>("Durată");
        colDurata.setCellValueFactory(new PropertyValueFactory<>("durata"));
        colDurata.setPrefWidth(80);

        tablePiese.getColumns().addAll(colId, colFormatie, colTitlu, colGen, colDurata);
        incarcaPiese();

        // Separator
        Separator sep1 = new Separator();

        // Campuri formular - 1 rand
        HBox hboxFormular = new HBox(10);
        txtId = new TextField();
        txtId.setPromptText("ID");
        txtId.setPrefWidth(60);

        txtFormatie = new TextField();
        txtFormatie.setPromptText("Formație");
        txtFormatie.setPrefWidth(120);

        txtTitlu = new TextField();
        txtTitlu.setPromptText("Titlu");
        txtTitlu.setPrefWidth(150);

        txtGen = new TextField();
        txtGen.setPromptText("Gen");
        txtGen.setPrefWidth(100);

        txtDurata = new TextField();
        txtDurata.setPromptText("MM:SS");
        txtDurata.setPrefWidth(70);

        hboxFormular.getChildren().addAll(txtId, txtFormatie, txtTitlu, txtGen, txtDurata);

        // Butoane CRUD - 1 rand
        HBox hboxCRUD = new HBox(10);

        Button btnAdauga = new Button("Adaugă");
        btnAdauga.setOnAction(e -> adaugaPiesa());

        Button btnModifica = new Button("Modifică");
        btnModifica.setOnAction(e -> modificaPiesa());

        Button btnSterge = new Button("Șterge");
        btnSterge.setOnAction(e -> stergePiesa());

        Button btnIncarca = new Button("Încarcă Selectat");
        btnIncarca.setOnAction(e -> incarcaPiesaInFormular());

        Button btnGoleste = new Button("Golește");
        btnGoleste.setOnAction(e -> golesteCampuri());

        hboxCRUD.getChildren().addAll(btnAdauga, btnModifica, btnSterge, btnIncarca, btnGoleste);

        // Separator
        Separator sep2 = new Separator();

        // Filtrare - 1 rand
        HBox hboxFiltrare = new HBox(10);
        Label lblGen = new Label("Filtrare gen:");
        comboGenuri = new ComboBox<>();
        comboGenuri.setPrefWidth(130);
        comboGenuri.setPromptText("Selectează");

        Button btnResetare = new Button("Resetare");
        btnResetare.setOnAction(e -> resetareFiltrare());

        hboxFiltrare.getChildren().addAll(lblGen, comboGenuri, btnResetare);

        incarcaGenuri();
        comboGenuri.valueProperty().addListener((obs, old, nou) -> {
            if (nou != null && !nou.isEmpty()) {
                filtreazaDupaGen(nou);
            }
        });

        // Separator
        Separator sep3 = new Separator();

        // Playlist - 1 rand
        HBox hboxPlaylist = new HBox(10);
        Label lblPlaylist = new Label("Generare playlist:");
        txtNumePlaylist = new TextField();
        txtNumePlaylist.setPrefWidth(150);
        txtNumePlaylist.setPromptText("nume_playlist");

        Button btnGenereaza = new Button("Generează");
        btnGenereaza.setOnAction(e -> genereazaPlaylist());

        Button btnVeziPlaylist = new Button("Vezi Playlist");
        btnVeziPlaylist.setOnAction(e -> veziPlaylist());

        hboxPlaylist.getChildren().addAll(lblPlaylist, txtNumePlaylist, btnGenereaza, btnVeziPlaylist);

        // Adauga tot in root
        root.getChildren().addAll(
                lblTitlu,
                tablePiese,
                sep1,
                hboxFormular,
                hboxCRUD,
                sep2,
                hboxFiltrare,
                sep3,
                hboxPlaylist
        );

        // Scena
        Scene scene = new Scene(root, 750, 600);
        primaryStage.setTitle("Administrare Muzică");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void incarcaPiese() {
        ObservableList<Piesa> items = FXCollections.observableArrayList(repository.getAll());
        tablePiese.setItems(items);
    }

    private void incarcaGenuri() {
        ObservableList<String> genuri = FXCollections.observableArrayList(
                repository.getGenuriFiltrate()
        );
        comboGenuri.setItems(genuri);
    }

    private void filtreazaDupaGen(String gen) {
        ObservableList<Piesa> items = FXCollections.observableArrayList(
                repository.filtrareGenMuzical(gen)
        );
        tablePiese.setItems(items);
    }

    private void resetareFiltrare() {
        comboGenuri.setValue(null);
        incarcaPiese();
    }

    private void adaugaPiesa() {
        try {
            if (!valideazaCampuri()) {
                return;
            }

            int id = Integer.parseInt(txtId.getText().trim());
            String formatie = txtFormatie.getText().trim();
            String titlu = txtTitlu.getText().trim();
            String gen = txtGen.getText().trim();
            String durata = txtDurata.getText().trim();

            if (!valideazaDurata(durata)) {
                afiseazaEroare("Eroare", "Durata trebuie în format MM:SS (ex: 03:45)");
                return;
            }

            Piesa piesa = new Piesa(id, formatie, titlu, gen, durata);
            repository.adauga(piesa);

            incarcaPiese();
            incarcaGenuri();
            golesteCampuri();
            afiseazaSucces("Succes", "Piesa adăugată!");

        } catch (NumberFormatException e) {
            afiseazaEroare("Eroare", "ID-ul trebuie să fie număr!");
        } catch (Exception e) {
            afiseazaEroare("Eroare", e.getMessage());
        }
    }

    private void modificaPiesa() {
        try {
            if (!valideazaCampuri()) {
                return;
            }

            int id = Integer.parseInt(txtId.getText().trim());
            String formatie = txtFormatie.getText().trim();
            String titlu = txtTitlu.getText().trim();
            String gen = txtGen.getText().trim();
            String durata = txtDurata.getText().trim();

            if (!valideazaDurata(durata)) {
                afiseazaEroare("Eroare", "Durata trebuie în format MM:SS");
                return;
            }

            Piesa piesa = new Piesa(id, formatie, titlu, gen, durata);
            repository.actualizeaza(piesa);

            incarcaPiese();
            incarcaGenuri();
            golesteCampuri();
            afiseazaSucces("Succes", "Piesa modificată!");

        } catch (NumberFormatException e) {
            afiseazaEroare("Eroare", "ID-ul trebuie să fie număr!");
        } catch (Exception e) {
            afiseazaEroare("Eroare", e.getMessage());
        }
    }

    private void stergePiesa() {
        Piesa selectata = tablePiese.getSelectionModel().getSelectedItem();

        if (selectata == null) {
            afiseazaEroare("Eroare", "Selectați o piesă din tabel!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmare");
        confirm.setHeaderText("Ștergeți piesa?");
        confirm.setContentText(selectata.getFormatie() + " - " + selectata.getTitlu());

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                repository.sterge(selectata.getId());
                incarcaPiese();
                incarcaGenuri();
                golesteCampuri();
                afiseazaSucces("Succes", "Piesa ștearsă!");
            }
        });
    }

    private void incarcaPiesaInFormular() {
        Piesa selectata = tablePiese.getSelectionModel().getSelectedItem();

        if (selectata == null) {
            afiseazaEroare("Eroare", "Selectați o piesă din tabel!");
            return;
        }

        txtId.setText(String.valueOf(selectata.getId()));
        txtFormatie.setText(selectata.getFormatie());
        txtTitlu.setText(selectata.getTitlu());
        txtGen.setText(selectata.getGenMuzical());
        txtDurata.setText(selectata.getDurata());
    }

    private void golesteCampuri() {
        txtId.clear();
        txtFormatie.clear();
        txtTitlu.clear();
        txtGen.clear();
        txtDurata.clear();
    }

    private boolean valideazaCampuri() {
        if (txtId.getText().trim().isEmpty() ||
                txtFormatie.getText().trim().isEmpty() ||
                txtTitlu.getText().trim().isEmpty() ||
                txtGen.getText().trim().isEmpty() ||
                txtDurata.getText().trim().isEmpty()) {

            afiseazaEroare("Eroare", "Toate câmpurile sunt obligatorii!");
            return false;
        }
        return true;
    }

    private boolean valideazaDurata(String durata) {
        return durata.matches("\\d{2}:\\d{2}");
    }

    private void genereazaPlaylist() {
        String nume = txtNumePlaylist.getText().trim();

        if (nume.isEmpty()) {
            afiseazaEroare("Eroare", "Introduceți un nume pentru playlist!");
            return;
        }

        if (!nume.matches("[a-zA-Z0-9_]+")) {
            afiseazaEroare("Eroare", "Doar litere, cifre și underscore!");
            return;
        }

        boolean succes = playlistGenerator.genereazaPlaylist(nume);

        if (succes) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succes");
            alert.setHeaderText(null);
            alert.setContentText("Playlist-ul '" + nume + "' a fost generat!\n" +
                    "Min 3 piese, durata >15 min, fără repetări.");
            alert.showAndWait();
            txtNumePlaylist.clear();
        } else {
            afiseazaEroare("Eroare",
                    "Nu s-a putut genera playlist-ul!\n" +
                            "Verificați că există suficiente piese diverse.");
        }
    }

    private void veziPlaylist() {
        String nume = txtNumePlaylist.getText().trim();

        if (nume.isEmpty()) {
            afiseazaEroare("Eroare", "Introduceți numele playlist-ului!");
            return;
        }

        // Verifică dacă tabela există
        if (!repository.existaTabela(nume)) {
            afiseazaEroare("Eroare", "Playlist-ul '" + nume + "' nu există în baza de date!\n" +
                    "Generați mai întâi playlist-ul.");
            return;
        }

        // Obține piesele din playlist
        List<Piesa> piese = repository.getPieseListaRedare(nume);

        if (piese.isEmpty()) {
            afiseazaEroare("Eroare", "Playlist-ul este gol!");
            return;
        }

        // Calculează durata totală
        int durataTotal = 0;
        for (Piesa p : piese) {
            durataTotal += p.getDurataInSecunde();
        }
        int minute = durataTotal / 60;
        int secunde = durataTotal % 60;

        // Creează mesajul
        StringBuilder mesaj = new StringBuilder();
        mesaj.append("📊 Playlist: ").append(nume).append("\n");
        mesaj.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        for (int i = 0; i < piese.size(); i++) {
            Piesa p = piese.get(i);
            mesaj.append(i + 1).append(". ")
                    .append(p.getFormatie()).append(" - ")
                    .append(p.getTitlu()).append("\n")
                    .append("   Gen: ").append(p.getGenMuzical())
                    .append(" | Durată: ").append(p.getDurata())
                    .append("\n\n");
        }

        mesaj.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        mesaj.append("Total: ").append(piese.size()).append(" piese | ");
        mesaj.append("Durată: ").append(String.format("%02d:%02d", minute, secunde));

        // Afișează dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conținut Playlist");
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(mesaj.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void afiseazaEroare(String titlu, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titlu);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    private void afiseazaSucces(String titlu, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titlu);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}