package com.coditramuntana.discography.report;

import com.coditramuntana.discography.artist.Artist;
import com.coditramuntana.discography.author.Author;
import com.coditramuntana.discography.lp.Lp;
import com.coditramuntana.discography.lp.LpRepository;
import com.coditramuntana.discography.report.dto.DiscographyReportRow;
import com.coditramuntana.discography.song.Song;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscographyReportServiceTest {

    @Mock
    private LpRepository lpRepository;

    @InjectMocks
    private DiscographyReportService service;

    @Test
    void deduplicaAutoresDentroDelMismoLp() {
        //---ARRANGE---//
        // 1. Construir Artist + LP + relación bidireccional
        Artist artist = new Artist("Test Band", "description");
        Lp lp = new Lp("Test album", "description");
        artist.addLp(lp);

        // 2. Construir 3 songs y añadirlas
        Song song1 = new Song("Track 1");
        Song song2 = new Song("Track 2");
        Song song3 = new Song("Track 3");
        lp.addSong(song1);
        lp.addSong(song2);
        lp.addSong(song3);

        // 3. Los dos autores, cada uno firma dos songs
        Author alice = new Author("Alice");
        Author bob = new Author("Bob");
        song1.addAuthor(alice);
        song2.addAuthor(alice);
        song2.addAuthor(bob);
        song3.addAuthor(bob);

        // 4. Programar el mock del repository: cuando el service llame findAllForReport(cualquier Pageable), devuelve un Lp construido
        when(lpRepository.findAllForReport(any()))
                .thenReturn(new PageImpl<>(List.of(lp)));


        //----- ACT -------

        // Cualquier pageable vale, el mock ignora su valor por el any()
        Page<DiscographyReportRow> result = service.generateReport(PageRequest.of(0, 10));

        // ---------- ASSERT ----------

        // Sacar el authorsCsv del único row del reporte
        String authorsCsv = result.getContent().get(0).authorsCsv();

        // Dividir por ", " y comprobar que hay exactamente Alice y Bob (orden irrelevante)
        assertThat(authorsCsv.split(", "))
                .containsExactlyInAnyOrder("Alice", "Bob");

    }

    @Test
    void aplicaSortPorDefectoCuandoPageableLlegaSinSort() {
        // ---------- ARRANGE ----------

        // Mock devuelve una página vacía — no nos importa el contenido,
        // solo queremos ver qué Pageable recibió el repository
        when(lpRepository.findAllForReport(any()))
                .thenReturn(new PageImpl<>(List.of()));

        // Pageable SIN sort (el cliente no mandó ?sort=)
        Pageable pageableSinSort = PageRequest.of(0, 10);

        // --------- ACT ----------

        service.generateReport(pageableSinSort);
        // ---------- ASSERT ----------

        // Capturamos el Pageable que el service pasó al repository
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(lpRepository).findAllForReport(captor.capture());

        // Sacamos el Pageable capturado y verificamos su sort
        Sort sortAplicado = captor.getValue().getSort();

        assertThat(sortAplicado).isEqualTo(Sort.by("artist.name", "name"));
    }

    @Test
    void respetaSortDelClienteCuandoPageableLlegaConSort() {
        // ---------- ARRANGE ----------
        // Creo un pageable con una ordenación de ejemplo Sort.by("name").descending())
        Pageable pageableConSort = PageRequest.of(0, 10, Sort.by("name").descending());

        when(lpRepository.findAllForReport(any()))
                .thenReturn(new PageImpl<>(List.of()));

        // --------- ACT ----------
        service.generateReport(pageableConSort);

        // ---------- ASSERT ----------
        // Comprobar que se ha enviado ese pageableConSort

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(lpRepository).findAllForReport(captor.capture());
        Sort sortAplicado = captor.getValue().getSort();

        assertThat(sortAplicado).isEqualTo(Sort.by("name").descending());
    }

    @Test
    void compruebaSortOrdenAlfabeticoAutores() {

        // ---------- ARRANGE ----------

        // 1. Construir Artist + LP + relación bidireccional
        Artist artist = new Artist("Test Band", "description");
        Lp lp = new Lp("Test album", "description");
        artist.addLp(lp);

        // 2. Construir 1 songs y añadirlas
        Song song1 = new Song("Track 1");
        lp.addSong(song1);
        Author zzzTop = new Author("ZZZTop");
        Author bob = new Author("Bob");
        Author marco = new Author("Marco");

        // 3. Añado los autores
        song1.addAuthor(zzzTop);
        song1.addAuthor(bob);
        song1.addAuthor(marco);

        // 4. Programar el mock del repository: cuando el service llame findAllForReport(cualquier Pageable), devuelve un Lp construido
        when(lpRepository.findAllForReport(any()))
                .thenReturn(new PageImpl<>(List.of(lp)));

        // --------- ACT ----------
        // Cualquier pageable vale, el mock ignora su valor por el any()
        Page<DiscographyReportRow> result = service.generateReport(PageRequest.of(0, 10));

        // ---------- ASSERT ----------

        // Sacar el authorsCsv del único row del reporte
        String authorsCsv = result.getContent().get(0).authorsCsv();

        // Dividir por ", " y comprobar que hay exactamente Alice y Bob (orden irrelevante)
        assertThat(authorsCsv.split(", "))
                .containsExactly("Bob", "Marco", "ZZZTop");

    }
}
