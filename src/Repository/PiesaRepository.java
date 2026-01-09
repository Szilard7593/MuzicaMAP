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

    public void creeazaTabelaListaRedare(String numeTabela) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + numeTabela + " (" +
                "id INTEGER PRIMARY KEY, " +
                "formatie TEXT NOT NULL, " +
                "titlu TEXT NOT NULL, " +
                "gen_muzical TEXT NOT NULL, " +
                "durata TEXT NOT NULL)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void adaugaInListaRedare(String numeTabela, Piesa piesa) throws SQLException {
        String sql = "INSERT INTO " + numeTabela + "(id, formatie, titlu, gen_muzical, durata) VALUES(?,?,?,?,?)";

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
}