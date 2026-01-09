package Servicii;

import Entitati.Piesa;
import Repository.PiesaRepository;

import java.sql.SQLException;
import java.util.*;

public class PlaylistGenerator {
    private final PiesaRepository repository;

    public PlaylistGenerator(PiesaRepository repository) {
        this.repository = repository;
    }

    /**
     * Genereaza o lista de redare conform cerintelor:
     * - Minim 3 piese
     * - Durata totala > 15 minute (900 secunde)
     * - Nu exista 2 piese consecutive cu aceeasi formatie sau gen
     *
     * @param numePlaylist numele listei de redare
     * @return true daca generarea a reusit, false daca nu e posibila
     */
    public boolean genereazaPlaylist(String numePlaylist) {
        try {
            List<Piesa> toatePiesele = repository.getAll();
            if (toatePiesele.size() < 3) {
                return false;
            }

            // Creare tabela pentru playlist
            repository.creeazaTabelaListaRedare(numePlaylist);

            // Generare playlist
            List<Piesa> playlist = new ArrayList<>();
            List<Piesa> pieseDisponibile = new ArrayList<>(toatePiesele);
            Collections.shuffle(pieseDisponibile);

            int durataTotal = 0;
            int incercari = 0;
            int maxIncercari = 1000;

            while (incercari < maxIncercari) {
                Piesa piesaCurenta = null;

                // Cauta o piesa valida
                for (Piesa piesa : pieseDisponibile) {
                    if (playlist.isEmpty()) {
                        piesaCurenta = piesa;
                        break;
                    }

                    Piesa ultimaPiesa = playlist.get(playlist.size() - 1);

                    // Verifica conditiile: formatie si gen diferite
                    if (!piesa.getFormatie().equals(ultimaPiesa.getFormatie()) &&
                            !piesa.getGenMuzical().equals(ultimaPiesa.getGenMuzical())) {
                        piesaCurenta = piesa;
                        break;
                    }
                }

                // Daca am gasit o piesa valida
                if (piesaCurenta != null) {
                    playlist.add(piesaCurenta);
                    pieseDisponibile.remove(piesaCurenta);
                    durataTotal += piesaCurenta.getDurataInSecunde();

                    // Verifica conditiile de finalizare
                    if (playlist.size() >= 3 && durataTotal > 900) {
                        // Salvare playlist in baza de date
                        for (Piesa p : playlist) {
                            repository.adaugaInListaRedare(numePlaylist, p);
                        }
                        return true;
                    }

                    // Daca nu mai sunt piese disponibile, shuffle din nou
                    if (pieseDisponibile.isEmpty()) {
                        pieseDisponibile = new ArrayList<>(toatePiesele);
                        pieseDisponibile.removeAll(playlist);
                        Collections.shuffle(pieseDisponibile);
                    }
                } else {
                    // Nu am gasit piesa valida, reseteaza si incearca din nou
                    playlist.clear();
                    durataTotal = 0;
                    pieseDisponibile = new ArrayList<>(toatePiesele);
                    Collections.shuffle(pieseDisponibile);
                }

                incercari++;
            }

            return false; // Nu s-a reusit generarea

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculeaza durata totala a unui playlist in format MM:SS
     */
    public String calculeazaDurataPlaylist(List<Piesa> playlist) {
        int totalSecunde = 0;
        for (Piesa piesa : playlist) {
            totalSecunde += piesa.getDurataInSecunde();
        }

        int minute = totalSecunde / 60;
        int secunde = totalSecunde % 60;
        return String.format("%02d:%02d", minute, secunde);
    }

    /**
     * Verifica daca o piesa poate fi adaugata dupa alta piesa
     */
    public boolean poateAdaugaDupa(Piesa piesaNoua, Piesa piesaPrecedenta) {
        if (piesaPrecedenta == null) {
            return true;
        }

        return !piesaNoua.getFormatie().equals(piesaPrecedenta.getFormatie()) &&
                !piesaNoua.getGenMuzical().equals(piesaPrecedenta.getGenMuzical());
    }
}