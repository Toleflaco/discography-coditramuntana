package com.coditramuntana.discography.seed;

import com.coditramuntana.discography.domain.artist.Artist;
import com.coditramuntana.discography.domain.artist.ArtistRepository;
import com.coditramuntana.discography.domain.author.Author;
import com.coditramuntana.discography.domain.author.AuthorRepository;
import com.coditramuntana.discography.domain.lp.Lp;
import com.coditramuntana.discography.domain.song.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(AuthorRepository authorRepository, ArtistRepository artistRepository) {
        this.authorRepository = authorRepository;
        this.artistRepository = artistRepository;

    }


    @Override
    @Transactional
    public void run(String... args) {
        log.info("[DataSeeder] iniciando siembra...");
        if (artistRepository.count() > 0) {
            return;
        }
        // TODO: seeds
        Author hetfield = findOrCreateAuthor("James Hetfield");
        Author ulrich = findOrCreateAuthor("Lars Ulrich");
        Author hammett = findOrCreateAuthor("Kirk Hammett");
        Author newsted = findOrCreateAuthor("Jason Newsted");

        Artist metallica = new Artist("Metallica", "Thrash metal band from Los Angeles");
        Lp blackAlbum = new Lp("Black Album", "Fifth studio album, 1991");

        Song enterSandman = new Song("Enter Sandman");
        enterSandman.addAuthor(hetfield);
        enterSandman.addAuthor(ulrich);
        enterSandman.addAuthor(hammett);

        Song unforgiven = new Song("The Unforgiven");
        unforgiven.addAuthor(hetfield);
        unforgiven.addAuthor(ulrich);
        unforgiven.addAuthor(hammett);

        Song myFriendOfMisery = new Song("My Friend of Misery");
        myFriendOfMisery.addAuthor(hetfield);
        myFriendOfMisery.addAuthor(ulrich);
        myFriendOfMisery.addAuthor(newsted);

        blackAlbum.addSong(enterSandman);
        blackAlbum.addSong(unforgiven);
        blackAlbum.addSong(myFriendOfMisery);

        metallica.addLp(blackAlbum);
        artistRepository.save(metallica);

        // === SEPULTURA AUTHORS ===
        Author kisser = findOrCreateAuthor("Andreas Kisser");
        Author igorCavalera = findOrCreateAuthor("Igor Cavalera");
        Author pauloJr = findOrCreateAuthor("Paulo Jr.");
        Author derrickGreen = findOrCreateAuthor("Derrick Green");

// === SEPULTURA ARTIST + LP ===
        Artist sepultura = new Artist("Sepultura", "Brazilian metal band formed in Belo Horizonte");
        Lp against = new Lp("Against", "Seventh studio album, 1998");

// === SONG ===
        Song hatredAside = new Song("Hatred Aside");
        hatredAside.addAuthor(kisser);
        hatredAside.addAuthor(igorCavalera);
        hatredAside.addAuthor(pauloJr);
        hatredAside.addAuthor(derrickGreen);

// === RELACIONES ===
        against.addSong(hatredAside);
        sepultura.addLp(against);

        artistRepository.save(sepultura);

        // === RADIOHEAD AUTHORS ===
        Author yorke        = findOrCreateAuthor("Thom Yorke");
        Author jonnyGreen   = findOrCreateAuthor("Jonny Greenwood");
        Author colinGreen   = findOrCreateAuthor("Colin Greenwood");
        Author obrien       = findOrCreateAuthor("Ed O'Brien");
        Author selway       = findOrCreateAuthor("Philip Selway");

// === RADIOHEAD ARTIST + LP ===
        Artist radiohead = new Artist("Radiohead", "Alternative rock band from Oxford");
        Lp okComputer = new Lp("OK Computer", "Third studio album, 1997");

// === SONG 1 ===
        Song paranoidAndroid = new Song("Paranoid Android");
        paranoidAndroid.addAuthor(yorke);
        paranoidAndroid.addAuthor(jonnyGreen);
        paranoidAndroid.addAuthor(colinGreen);
        paranoidAndroid.addAuthor(obrien);
        paranoidAndroid.addAuthor(selway);

// === SONG 2 ===
        Song karmaPolice = new Song("Karma Police");
        karmaPolice.addAuthor(yorke);
        karmaPolice.addAuthor(jonnyGreen);
        karmaPolice.addAuthor(colinGreen);
        karmaPolice.addAuthor(obrien);
        karmaPolice.addAuthor(selway);

// === SONG 3 ===
        Song noSurprises = new Song("No Surprises");
        noSurprises.addAuthor(yorke);
        noSurprises.addAuthor(jonnyGreen);
        noSurprises.addAuthor(colinGreen);
        noSurprises.addAuthor(obrien);
        noSurprises.addAuthor(selway);

// === RELACIONES ===
        okComputer.addSong(paranoidAndroid);
        okComputer.addSong(karmaPolice);
        okComputer.addSong(noSurprises);

        radiohead.addLp(okComputer);
        artistRepository.save(radiohead);

        // === RHCP AUTHORS ===
        Author kiedis      = findOrCreateAuthor("Anthony Kiedis");
        Author flea        = findOrCreateAuthor("Flea");
        Author frusciante  = findOrCreateAuthor("John Frusciante");
        Author chadSmith   = findOrCreateAuthor("Chad Smith");

// === RHCP ARTIST + LP ===
        Artist rhcp = new Artist("Red Hot Chili Peppers", "Funk rock band from Los Angeles");
        Lp byTheWay = new Lp("By the Way", "Eighth studio album, 2002");

// === SONG 1 ===
        Song byTheWaySong = new Song("By the Way");
        byTheWaySong.addAuthor(kiedis);
        byTheWaySong.addAuthor(flea);
        byTheWaySong.addAuthor(frusciante);
        byTheWaySong.addAuthor(chadSmith);

// === SONG 2 ===
        Song cantStop = new Song("Can't Stop");
        cantStop.addAuthor(kiedis);
        cantStop.addAuthor(flea);
        cantStop.addAuthor(frusciante);
        cantStop.addAuthor(chadSmith);

// === SONG 3 ===
        Song universallySpeaking = new Song("Universally Speaking");
        universallySpeaking.addAuthor(kiedis);
        universallySpeaking.addAuthor(flea);
        universallySpeaking.addAuthor(frusciante);
        universallySpeaking.addAuthor(chadSmith);

// === RELACIONES ===
        byTheWay.addSong(byTheWaySong);
        byTheWay.addSong(cantStop);
        byTheWay.addSong(universallySpeaking);

        rhcp.addLp(byTheWay);
        artistRepository.save(rhcp);
// === GUNS N' ROSES AUTHORS ===
        Author axlRose     = findOrCreateAuthor("Axl Rose");
        Author slash       = findOrCreateAuthor("Slash");
        Author izzy        = findOrCreateAuthor("Izzy Stradlin");
        Author duff        = findOrCreateAuthor("Duff McKagan");
        Author adler       = findOrCreateAuthor("Steven Adler");

// === GNR ARTIST + LP ===
        Artist gunsNRoses = new Artist("Guns N' Roses", "Hard rock band from Los Angeles");
        Lp appetite = new Lp("Appetite for Destruction", "Debut studio album, 1987");

// === SONG 1 ===
        Song welcomeToTheJungle = new Song("Welcome to the Jungle");
        welcomeToTheJungle.addAuthor(axlRose);
        welcomeToTheJungle.addAuthor(slash);
        welcomeToTheJungle.addAuthor(izzy);
        welcomeToTheJungle.addAuthor(duff);
        welcomeToTheJungle.addAuthor(adler);

// === SONG 2 ===
        Song sweetChild = new Song("Sweet Child o' Mine");
        sweetChild.addAuthor(axlRose);
        sweetChild.addAuthor(slash);
        sweetChild.addAuthor(izzy);
        sweetChild.addAuthor(duff);
        sweetChild.addAuthor(adler);

// === SONG 3 ===
        Song paradiseCity = new Song("Paradise City");
        paradiseCity.addAuthor(axlRose);
        paradiseCity.addAuthor(slash);
        paradiseCity.addAuthor(izzy);
        paradiseCity.addAuthor(duff);
        paradiseCity.addAuthor(adler);

// === RELACIONES ===
        appetite.addSong(welcomeToTheJungle);
        appetite.addSong(sweetChild);
        appetite.addSong(paradiseCity);

        gunsNRoses.addLp(appetite);
        artistRepository.save(gunsNRoses);


    }

    private Author findOrCreateAuthor(String name) {
        return authorRepository.findByName(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }
}
