package Repository;

import Entitati.Piesa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PiesaRepository {
    private final String dbUrl;

    public PiesaRepository(String dbFilePath) {
        this.dbUrl = "jdbc:sqlite:" + dbFilePath;
        initDatabase();
        initializareDateInitiale();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS piese (" +
                    "id INTEGER PRIMARY KEY, " +
                    "formatie TEXT NOT NULL, " +
                    "titlu TEXT NOT NULL, " +
                    "gen_muzical TEXT NOT NULL, " +
                    "durata TEXT NOT NULL)";
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializareDateInitiale() {
        try {
            // Verifica daca exista deja piese
            if (getAll().isEmpty()) {
                // Adauga minim 5 piese conform cerintelor PDF
                adauga(new Piesa(1000, "Coldplay", "A Head Full of Dreams", "Alternative", "03:43"));
                adauga(new Piesa(1001, "Imagine Dragons", "Radioactive", "Rock", "03:06"));
                adauga(new Piesa(1002, "The Beatles", "Let It Be", "Rock", "04:03"));
                adauga(new Piesa(1003, "Coldplay", "Yellow", "Alternative", "04:26"));
                adauga(new Piesa(1004, "Queen", "Bohemian Rhapsody", "Rock", "05:55"));
                adauga(new Piesa(1005, "Ed Sheeran", "Shape of You", "Pop", "03:53"));
                adauga(new Piesa(1006, "Imagine Dragons", "Believer", "Rock", "03:24"));
                adauga(new Piesa(1007, "Coldplay", "Viva La Vida", "Alternative", "04:01"));
                System.out.println("✓ 8 piese initiale adaugate in baza de date");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adauga(Piesa piesa) throws Exception {
        String sql = "INSERT INTO piese(id, formatie, titlu, gen_muzical, durata) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, piesa.getId());
            pstmt.setString(2, piesa.getFormatie());
            pstmt.setString(3, piesa.getTitlu());
            pstmt.setString(4, piesa.getGenMuzical());
            pstmt.setString(5, piesa.getDurata());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY")) {
                throw new Exception("ID duplicat: " + piesa.getId());
            }
            throw e;
        }
    }

    public void actualizeaza(Piesa piesa) {
        String sql = "UPDATE piese SET formatie=?, titlu=?, gen_muzical=?, durata=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, piesa.getFormatie());
            pstmt.setString(2, piesa.getTitlu());
            pstmt.setString(3, piesa.getGenMuzical());
            pstmt.setString(4, piesa.getDurata());
            pstmt.setInt(5, piesa.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sterge(int id) {
        String sql = "DELETE FROM piese WHERE id=?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Piesa> getAll() {
        List<Piesa> piese = new ArrayList<>();
        String sql = "SELECT * FROM piese ORDER BY formatie ASC, titlu ASC";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                piese.add(new Piesa(
                        rs.getInt("id"),
                        rs.getString("formatie"),
                        rs.getString("titlu"),
                        rs.getString("gen_muzical"),
                        rs.getString("durata")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piese;
    }

    public List<String> getGenuriFiltrate() {
        List<String> genuri = new ArrayList<>();
        String sql = "SELECT DISTINCT gen_muzical FROM piese ORDER BY gen_muzical";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                genuri.add(rs.getString("gen_muzical"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genuri;
    }

    public List<Piesa> filtrareGenMuzical(String gen) {
        List<Piesa> piese = new ArrayList<>();
        String sql = "SELECT * FROM piese WHERE gen_muzical=? ORDER BY formatie ASC, titlu ASC";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gen);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                piese.add(new Piesa(
                        rs.getInt("id"),
                        rs.getString("formatie"),
                        rs.getString("titlu"),
                        rs.getString("gen_muzical"),
                        rs.getString("durata")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return piese;
    }

    /**
     * Creează tabelă pentru lista de redare
     * Tabela are ACELAȘI NUME cu numele listei de redare
     * Structura este IDENTICĂ cu tabela piese
     */
    public void creeazaTabelaListaRedare(String numeTabela) throws SQLException {
        // Șterge tabela dacă există (pentru regenerare)
        String sqlDrop = "DROP TABLE IF EXISTS " + numeTabela;

        // Creează tabela cu aceeași structură
        String sqlCreate = "CREATE TABLE " + numeTabela + " (" +
                "id INTEGER PRIMARY KEY, " +
                "formatie TEXT NOT NULL, " +
                "titlu TEXT NOT NULL, " +
                "gen_muzical TEXT NOT NULL, " +
                "durata TEXT NOT NULL)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDrop);
            stmt.execute(sqlCreate);
        }
    }

    /**
     * Adaugă o piesă în lista de redare (tabela cu numele dat)
     */
    public void adaugaInListaRedare(String numeTabela, Piesa piesa) throws SQLException {
        String sql = "INSERT INTO " + numeTabela +
                " (id, formatie, titlu, gen_muzical, durata) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, piesa.getId());
            pstmt.setString(2, piesa.getFormatie());
            pstmt.setString(3, piesa.getTitlu());
            pstmt.setString(4, piesa.getGenMuzical());
            pstmt.setString(5, piesa.getDurata());
            pstmt.executeUpdate();
        }
    }

    /**
     * Verifică dacă o tabelă există în baza de date
     */
    public boolean existaTabela(String numeTabela) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeTabela);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obține piesele dintr-o listă de redare (tabelă)
     */
    public List<Piesa> getPieseListaRedare(String numeTabela) {
        List<Piesa> piese = new ArrayList<>();

        if (!existaTabela(numeTabela)) {
            return piese;
        }

        String sql = "SELECT * FROM " + numeTabela;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                piese.add(new Piesa(
                        rs.getInt("id"),
                        rs.getString("formatie"),
                        rs.getString("titlu"),
                        rs.getString("gen_muzical"),
                        rs.getString("durata")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return piese;
    }
}