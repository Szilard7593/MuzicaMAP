package Servicii;

import Entitati.Piesa;
import Repository.PiesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistGeneratorTest {

    private PiesaRepository repository;
    private PlaylistGenerator generator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // Creare baza de date temporara pentru teste
        String dbPath = tempDir.resolve("test_muzica.db").toString();
        repository = new PiesaRepository(dbPath);
        generator = new PlaylistGenerator(repository);

        // Adaugare piese de test
        repository.adauga(new Piesa(1, "Coldplay", "Yellow", "Alternative", "04:26"));
        repository.adauga(new Piesa(2, "The Beatles", "Let It Be", "Rock", "04:03"));
        repository.adauga(new Piesa(3, "Queen", "Bohemian Rhapsody", "Rock", "05:55"));
        repository.adauga(new Piesa(4, "Ed Sheeran", "Shape of You", "Pop", "03:53"));
        repository.adauga(new Piesa(5, "Coldplay", "Viva La Vida", "Alternative", "04:01"));
        repository.adauga(new Piesa(6, "Imagine Dragons", "Radioactive", "Rock", "03:06"));
        repository.adauga(new Piesa(7, "The Weeknd", "Blinding Lights", "Pop", "03:20"));
        repository.adauga(new Piesa(8, "Arctic Monkeys", "Do I Wanna Know", "Indie", "04:32"));
    }

    @Test
    void testGenereazaPlaylistCuSucces() {
        // Test: Generare playlist cu suficiente piese diverse
        boolean result = generator.genereazaPlaylist("playlist_test");
        assertTrue(result, "Playlist-ul ar trebui sa fie generat cu succes");
    }

    @Test
    void testGenereazaPlaylistNumeValid() {
        // Test: Playlist cu nume valid
        String numePlaylist = "my_awesome_playlist_123";
        boolean result = generator.genereazaPlaylist(numePlaylist);
        assertTrue(result, "Playlist-ul cu nume valid ar trebui generat");
    }

    @Test
    void testPoateAdaugaDupa() {
        // Test: Verificare regula - formatie si gen diferite
        Piesa piesa1 = new Piesa(1, "Coldplay", "Yellow", "Alternative", "04:26");
        Piesa piesa2 = new Piesa(2, "The Beatles", "Let It Be", "Rock", "04:03");
        Piesa piesa3 = new Piesa(3, "Coldplay", "Viva La Vida", "Alternative", "04:01");

        // Piesa2 poate urma dupa Piesa1 (formatie si gen diferite)
        assertTrue(generator.poateAdaugaDupa(piesa2, piesa1));

        // Piesa3 NU poate urma dupa Piesa1 (aceeasi formatie)
        assertFalse(generator.poateAdaugaDupa(piesa3, piesa1));

        // Prima piesa poate fi orice
        assertTrue(generator.poateAdaugaDupa(piesa1, null));
    }

    @Test
    void testCalculeazaDurataPlaylist() {
        // Test: Calcul durata playlist
        List<Piesa> playlist = new ArrayList<>();
        playlist.add(new Piesa(1, "Artist1", "Song1", "Pop", "03:30"));
        playlist.add(new Piesa(2, "Artist2", "Song2", "Rock", "04:15"));
        playlist.add(new Piesa(3, "Artist3", "Song3", "Jazz", "05:00"));

        String durata = generator.calculeazaDurataPlaylist(playlist);
        assertEquals("12:45", durata, "Durata totala ar trebui sa fie 12:45");
    }

    @Test
    void testCalculeazaDurataPlaylistGol() {
        // Test: Playlist gol
        List<Piesa> playlist = new ArrayList<>();
        String durata = generator.calculeazaDurataPlaylist(playlist);
        assertEquals("00:00", durata, "Durata unui playlist gol ar trebui sa fie 00:00");
    }

    @Test
    void testPoateAdaugaDupaGenDiferit() {
        // Test: Aceeasi formatie dar gen diferit - NU poate adauga
        Piesa piesa1 = new Piesa(1, "Coldplay", "Yellow", "Alternative", "04:26");
        Piesa piesa2 = new Piesa(2, "Coldplay", "Paradise", "Pop", "04:18");

        assertFalse(generator.poateAdaugaDupa(piesa2, piesa1),
                "Nu poate adauga piesa cu aceeasi formatie, chiar daca genul e diferit");
    }

    @Test
    void testPoateAdaugaDupaFormatieDiferita() {
        // Test: Formatie diferita dar acelasi gen - NU poate adauga
        Piesa piesa1 = new Piesa(1, "The Beatles", "Let It Be", "Rock", "04:03");
        Piesa piesa2 = new Piesa(2, "Queen", "Bohemian Rhapsody", "Rock", "05:55");

        assertFalse(generator.poateAdaugaDupa(piesa2, piesa1),
                "Nu poate adauga piesa cu acelasi gen, chiar daca formatia e diferita");
    }

    @Test
    void testDurataInSecunde() {
        // Test: Conversie durata in secunde
        Piesa piesa = new Piesa(1, "Test", "Test", "Test", "03:45");
        assertEquals(225, piesa.getDurataInSecunde(),
                "03:45 ar trebui sa fie 225 secunde");

        Piesa piesa2 = new Piesa(2, "Test", "Test", "Test", "15:00");
        assertEquals(900, piesa2.getDurataInSecunde(),
                "15:00 ar trebui sa fie 900 secunde");
    }
}