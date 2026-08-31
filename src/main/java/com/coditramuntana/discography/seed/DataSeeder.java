package com.coditramuntana.discography.seed;

import com.coditramuntana.discography.artist.Artist;
import com.coditramuntana.discography.artist.ArtistRepository;
import com.coditramuntana.discography.author.Author;
import com.coditramuntana.discography.author.AuthorRepository;
import com.coditramuntana.discography.lp.Lp;
import com.coditramuntana.discography.song.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Poblado inicial de la base de datos para demo.
 * <p>
 * 25 artistas, 45 LPs, ~130 canciones. Datos suficientes para
 * visualizar paginación (25 → 3 páginas con size=10) tanto en
 * el listado de Artistas como en LPs y en el reporte de discografía.
 * <p>
 * Los autores se comparten entre bandas cuando aplica (ej. Roger Waters
 * en Pink Floyd) mediante {@link #findOrCreateAuthor(String)}.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;

    public DataSeeder(AuthorRepository authorRepository, ArtistRepository artistRepository) {
        this.authorRepository = authorRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (artistRepository.count() > 0) {
            log.info("[DataSeeder] La base de datos ya contiene artistas. Se omite la siembra.");
            return;
        }
        log.info("[DataSeeder] Iniciando siembra de datos de demostración...");

        seedAcDc();
        seedAliceInChains();
        seedBlackSabbath();
        seedDavidBowie();
        seedDepecheMode();
        seedFooFighters();
        seedGunsNRoses();
        seedIronMaiden();
        seedJoyDivision();
        seedLedZeppelin();
        seedMetallica();
        seedNirvana();
        seedPearlJam();
        seedPinkFloyd();
        seedQueen();
        seedRadiohead();
        seedRammstein();
        seedRhcp();
        seedSepultura();
        seedSoundgarden();
        seedSystemOfADown();
        seedTheBeatles();
        seedTheCure();
        seedTheSmiths();
        seedU2();

        log.info("[DataSeeder] Siembra completada: {} artistas.", artistRepository.count());
    }

    // ---------- BANDAS ----------

    private void seedAcDc() {
        Author angusYoung = findOrCreateAuthor("Angus Young");
        Author malcolmYoung = findOrCreateAuthor("Malcolm Young");
        Author brianJohnson = findOrCreateAuthor("Brian Johnson");
        Author bonScott = findOrCreateAuthor("Bon Scott");

        Artist acdc = new Artist("AC/DC", "Hard rock band from Sydney");

        Lp backInBlack = new Lp("Back in Black", "Seventh studio album, 1980");
        backInBlack.addSong(song("Back in Black", angusYoung, malcolmYoung, brianJohnson));
        backInBlack.addSong(song("Hells Bells", angusYoung, malcolmYoung, brianJohnson));
        backInBlack.addSong(song("You Shook Me All Night Long", angusYoung, malcolmYoung, brianJohnson));

        Lp highwayToHell = new Lp("Highway to Hell", "Sixth studio album, 1979");
        highwayToHell.addSong(song("Highway to Hell", angusYoung, malcolmYoung, bonScott));
        highwayToHell.addSong(song("Girls Got Rhythm", angusYoung, malcolmYoung, bonScott));

        acdc.addLp(backInBlack);
        acdc.addLp(highwayToHell);
        artistRepository.save(acdc);
    }

    private void seedAliceInChains() {
        Author cantrell = findOrCreateAuthor("Jerry Cantrell");
        Author staley = findOrCreateAuthor("Layne Staley");

        Artist aic = new Artist("Alice in Chains", "Grunge band from Seattle");

        Lp dirt = new Lp("Dirt", "Second studio album, 1992");
        dirt.addSong(song("Would?", cantrell, staley));
        dirt.addSong(song("Rooster", cantrell));
        dirt.addSong(song("Down in a Hole", cantrell));

        aic.addLp(dirt);
        artistRepository.save(aic);
    }

    private void seedBlackSabbath() {
        Author iommi = findOrCreateAuthor("Tony Iommi");
        Author ozzy = findOrCreateAuthor("Ozzy Osbourne");
        Author butler = findOrCreateAuthor("Geezer Butler");
        Author ward = findOrCreateAuthor("Bill Ward");

        Artist sabbath = new Artist("Black Sabbath", "Pioneering heavy metal band from Birmingham");

        Lp paranoid = new Lp("Paranoid", "Second studio album, 1970");
        paranoid.addSong(song("Paranoid", iommi, ozzy, butler, ward));
        paranoid.addSong(song("Iron Man", iommi, ozzy, butler, ward));
        paranoid.addSong(song("War Pigs", iommi, ozzy, butler, ward));

        sabbath.addLp(paranoid);
        artistRepository.save(sabbath);
    }

    private void seedDavidBowie() {
        Author bowie = findOrCreateAuthor("David Bowie");

        Artist davidBowie = new Artist("David Bowie", "English singer, songwriter and actor");

        Lp ziggy = new Lp("The Rise and Fall of Ziggy Stardust", "Fifth studio album, 1972");
        ziggy.addSong(song("Starman", bowie));
        ziggy.addSong(song("Ziggy Stardust", bowie));
        ziggy.addSong(song("Suffragette City", bowie));

        Lp lowLp = new Lp("Low", "Eleventh studio album, 1977");
        lowLp.addSong(song("Sound and Vision", bowie));
        lowLp.addSong(song("Speed of Life", bowie));

        davidBowie.addLp(ziggy);
        davidBowie.addLp(lowLp);
        artistRepository.save(davidBowie);
    }

    private void seedDepecheMode() {
        Author gore = findOrCreateAuthor("Martin Gore");
        Author gahan = findOrCreateAuthor("Dave Gahan");

        Artist depeche = new Artist("Depeche Mode", "English electronic band from Basildon");

        Lp violator = new Lp("Violator", "Seventh studio album, 1990");
        violator.addSong(song("Personal Jesus", gore));
        violator.addSong(song("Enjoy the Silence", gore));
        violator.addSong(song("Policy of Truth", gore));

        depeche.addLp(violator);
        artistRepository.save(depeche);
    }

    private void seedFooFighters() {
        Author grohl = findOrCreateAuthor("Dave Grohl");
        Author mendel = findOrCreateAuthor("Nate Mendel");
        Author shiflett = findOrCreateAuthor("Chris Shiflett");

        Artist foo = new Artist("Foo Fighters", "Rock band from Seattle formed in 1994");

        Lp theColour = new Lp("The Colour and the Shape", "Second studio album, 1997");
        theColour.addSong(song("Everlong", grohl));
        theColour.addSong(song("Monkey Wrench", grohl));
        theColour.addSong(song("My Hero", grohl));

        Lp wastingLight = new Lp("Wasting Light", "Seventh studio album, 2011");
        wastingLight.addSong(song("Walk", grohl, mendel, shiflett));
        wastingLight.addSong(song("Rope", grohl, mendel, shiflett));

        foo.addLp(theColour);
        foo.addLp(wastingLight);
        artistRepository.save(foo);
    }

    private void seedGunsNRoses() {
        Author axl = findOrCreateAuthor("Axl Rose");
        Author slash = findOrCreateAuthor("Slash");
        Author izzy = findOrCreateAuthor("Izzy Stradlin");
        Author duff = findOrCreateAuthor("Duff McKagan");
        Author adler = findOrCreateAuthor("Steven Adler");

        Artist gnr = new Artist("Guns N' Roses", "Hard rock band from Los Angeles");

        Lp appetite = new Lp("Appetite for Destruction", "Debut studio album, 1987");
        appetite.addSong(song("Welcome to the Jungle", axl, slash, izzy, duff, adler));
        appetite.addSong(song("Sweet Child o' Mine", axl, slash, izzy, duff, adler));
        appetite.addSong(song("Paradise City", axl, slash, izzy, duff, adler));

        Lp useYourIllusion = new Lp("Use Your Illusion I", "Second studio album, 1991");
        useYourIllusion.addSong(song("November Rain", axl));
        useYourIllusion.addSong(song("Don't Cry", axl, izzy));

        gnr.addLp(appetite);
        gnr.addLp(useYourIllusion);
        artistRepository.save(gnr);
    }

    private void seedIronMaiden() {
        Author harris = findOrCreateAuthor("Steve Harris");
        Author dickinson = findOrCreateAuthor("Bruce Dickinson");
        Author smith = findOrCreateAuthor("Adrian Smith");

        Artist maiden = new Artist("Iron Maiden", "Heavy metal band from Leyton, London");

        Lp numberOfTheBeast = new Lp("The Number of the Beast", "Third studio album, 1982");
        numberOfTheBeast.addSong(song("The Number of the Beast", harris));
        numberOfTheBeast.addSong(song("Hallowed Be Thy Name", harris));
        numberOfTheBeast.addSong(song("Run to the Hills", harris));

        Lp powerslave = new Lp("Powerslave", "Fifth studio album, 1984");
        powerslave.addSong(song("Aces High", harris));
        powerslave.addSong(song("2 Minutes to Midnight", dickinson, smith));

        maiden.addLp(numberOfTheBeast);
        maiden.addLp(powerslave);
        artistRepository.save(maiden);
    }

    private void seedJoyDivision() {
        Author curtis = findOrCreateAuthor("Ian Curtis");
        Author hook = findOrCreateAuthor("Peter Hook");
        Author sumner = findOrCreateAuthor("Bernard Sumner");
        Author morris = findOrCreateAuthor("Stephen Morris");

        Artist joy = new Artist("Joy Division", "Post-punk band from Salford, England");

        Lp unknownPleasures = new Lp("Unknown Pleasures", "Debut studio album, 1979");
        unknownPleasures.addSong(song("Disorder", curtis, hook, sumner, morris));
        unknownPleasures.addSong(song("She's Lost Control", curtis, hook, sumner, morris));
        unknownPleasures.addSong(song("Shadowplay", curtis, hook, sumner, morris));

        joy.addLp(unknownPleasures);
        artistRepository.save(joy);
    }

    private void seedLedZeppelin() {
        Author page = findOrCreateAuthor("Jimmy Page");
        Author plant = findOrCreateAuthor("Robert Plant");
        Author jonesJPJ = findOrCreateAuthor("John Paul Jones");
        Author bonham = findOrCreateAuthor("John Bonham");

        Artist ledZep = new Artist("Led Zeppelin", "English rock band formed in London in 1968");

        Lp ledZepIV = new Lp("Led Zeppelin IV", "Fourth studio album, 1971");
        ledZepIV.addSong(song("Stairway to Heaven", page, plant));
        ledZepIV.addSong(song("Black Dog", page, plant, jonesJPJ));
        ledZepIV.addSong(song("Rock and Roll", page, plant, jonesJPJ, bonham));

        Lp physicalGraffiti = new Lp("Physical Graffiti", "Sixth studio album, 1975");
        physicalGraffiti.addSong(song("Kashmir", page, plant, bonham));
        physicalGraffiti.addSong(song("Trampled Under Foot", page, plant, jonesJPJ));

        ledZep.addLp(ledZepIV);
        ledZep.addLp(physicalGraffiti);
        artistRepository.save(ledZep);
    }

    private void seedMetallica() {
        Author hetfield = findOrCreateAuthor("James Hetfield");
        Author ulrich = findOrCreateAuthor("Lars Ulrich");
        Author hammett = findOrCreateAuthor("Kirk Hammett");
        Author newsted = findOrCreateAuthor("Jason Newsted");
        Author burton = findOrCreateAuthor("Cliff Burton");

        Artist metallica = new Artist("Metallica", "Thrash metal band from Los Angeles");

        Lp blackAlbum = new Lp("Black Album", "Fifth studio album, 1991");
        blackAlbum.addSong(song("Enter Sandman", hetfield, ulrich, hammett));
        blackAlbum.addSong(song("The Unforgiven", hetfield, ulrich, hammett));
        blackAlbum.addSong(song("My Friend of Misery", hetfield, ulrich, newsted));

        Lp masterOfPuppets = new Lp("Master of Puppets", "Third studio album, 1986");
        masterOfPuppets.addSong(song("Master of Puppets", hetfield, ulrich, hammett, burton));
        masterOfPuppets.addSong(song("Battery", hetfield, ulrich));
        masterOfPuppets.addSong(song("Orion", hetfield, ulrich, burton));

        metallica.addLp(blackAlbum);
        metallica.addLp(masterOfPuppets);
        artistRepository.save(metallica);
    }

    private void seedNirvana() {
        Author cobain = findOrCreateAuthor("Kurt Cobain");
        Author novoselic = findOrCreateAuthor("Krist Novoselic");
        Author grohl = findOrCreateAuthor("Dave Grohl");

        Artist nirvana = new Artist("Nirvana", "Grunge band from Aberdeen, Washington");

        Lp nevermind = new Lp("Nevermind", "Second studio album, 1991");
        nevermind.addSong(song("Smells Like Teen Spirit", cobain, novoselic, grohl));
        nevermind.addSong(song("Come as You Are", cobain));
        nevermind.addSong(song("In Bloom", cobain));

        Lp inUtero = new Lp("In Utero", "Third studio album, 1993");
        inUtero.addSong(song("Heart-Shaped Box", cobain));
        inUtero.addSong(song("All Apologies", cobain));

        nirvana.addLp(nevermind);
        nirvana.addLp(inUtero);
        artistRepository.save(nirvana);
    }

    private void seedPearlJam() {
        Author vedder = findOrCreateAuthor("Eddie Vedder");
        Author gossard = findOrCreateAuthor("Stone Gossard");
        Author ament = findOrCreateAuthor("Jeff Ament");
        Author mccready = findOrCreateAuthor("Mike McCready");

        Artist pearlJam = new Artist("Pearl Jam", "Grunge band from Seattle");

        Lp ten = new Lp("Ten", "Debut studio album, 1991");
        ten.addSong(song("Alive", vedder, gossard));
        ten.addSong(song("Jeremy", vedder, ament));
        ten.addSong(song("Black", vedder, gossard));

        pearlJam.addLp(ten);
        artistRepository.save(pearlJam);
    }

    private void seedPinkFloyd() {
        Author waters = findOrCreateAuthor("Roger Waters");
        Author gilmour = findOrCreateAuthor("David Gilmour");
        Author wright = findOrCreateAuthor("Richard Wright");
        Author mason = findOrCreateAuthor("Nick Mason");

        Artist pinkFloyd = new Artist("Pink Floyd", "Progressive rock band from London");

        Lp dsotm = new Lp("The Dark Side of the Moon", "Eighth studio album, 1973");
        dsotm.addSong(song("Money", waters));
        dsotm.addSong(song("Time", waters, gilmour, wright, mason));
        dsotm.addSong(song("Us and Them", waters, wright));

        Lp theWall = new Lp("The Wall", "Eleventh studio album, 1979");
        theWall.addSong(song("Another Brick in the Wall, Part 2", waters));
        theWall.addSong(song("Comfortably Numb", waters, gilmour));
        theWall.addSong(song("Hey You", waters));

        Lp wishYouWereHere = new Lp("Wish You Were Here", "Ninth studio album, 1975");
        wishYouWereHere.addSong(song("Wish You Were Here", waters, gilmour));
        wishYouWereHere.addSong(song("Shine On You Crazy Diamond (Parts I-V)", waters, gilmour, wright));

        pinkFloyd.addLp(dsotm);
        pinkFloyd.addLp(theWall);
        pinkFloyd.addLp(wishYouWereHere);
        artistRepository.save(pinkFloyd);
    }

    private void seedQueen() {
        Author mercury = findOrCreateAuthor("Freddie Mercury");
        Author mayQ = findOrCreateAuthor("Brian May");
        Author taylorQ = findOrCreateAuthor("Roger Taylor");
        Author deacon = findOrCreateAuthor("John Deacon");

        Artist queen = new Artist("Queen", "British rock band formed in London in 1970");

        Lp nightAtTheOpera = new Lp("A Night at the Opera", "Fourth studio album, 1975");
        nightAtTheOpera.addSong(song("Bohemian Rhapsody", mercury));
        nightAtTheOpera.addSong(song("Love of My Life", mercury));
        nightAtTheOpera.addSong(song("You're My Best Friend", deacon));

        Lp newsOfTheWorld = new Lp("News of the World", "Sixth studio album, 1977");
        newsOfTheWorld.addSong(song("We Will Rock You", mayQ));
        newsOfTheWorld.addSong(song("We Are the Champions", mercury));

        queen.addLp(nightAtTheOpera);
        queen.addLp(newsOfTheWorld);
        artistRepository.save(queen);
    }

    private void seedRadiohead() {
        Author yorke = findOrCreateAuthor("Thom Yorke");
        Author jonnyGreen = findOrCreateAuthor("Jonny Greenwood");
        Author colinGreen = findOrCreateAuthor("Colin Greenwood");
        Author obrien = findOrCreateAuthor("Ed O'Brien");
        Author selway = findOrCreateAuthor("Philip Selway");

        Artist radiohead = new Artist("Radiohead", "Alternative rock band from Oxford");

        Lp okComputer = new Lp("OK Computer", "Third studio album, 1997");
        okComputer.addSong(song("Paranoid Android", yorke, jonnyGreen, colinGreen, obrien, selway));
        okComputer.addSong(song("Karma Police", yorke, jonnyGreen, colinGreen, obrien, selway));
        okComputer.addSong(song("No Surprises", yorke, jonnyGreen, colinGreen, obrien, selway));

        Lp inRainbows = new Lp("In Rainbows", "Seventh studio album, 2007");
        inRainbows.addSong(song("Nude", yorke, jonnyGreen));
        inRainbows.addSong(song("Weird Fishes/Arpeggi", yorke, jonnyGreen));

        radiohead.addLp(okComputer);
        radiohead.addLp(inRainbows);
        artistRepository.save(radiohead);
    }

    private void seedRammstein() {
        Author lindemann = findOrCreateAuthor("Till Lindemann");
        Author kruspe = findOrCreateAuthor("Richard Kruspe");
        Author landers = findOrCreateAuthor("Paul Landers");
        Author riedel = findOrCreateAuthor("Oliver Riedel");

        Artist rammstein = new Artist("Rammstein", "Neue Deutsche Härte band from Berlin");

        Lp mutter = new Lp("Mutter", "Third studio album, 2001");
        mutter.addSong(song("Sonne", lindemann, kruspe, landers));
        mutter.addSong(song("Ich Will", lindemann, kruspe, landers, riedel));
        mutter.addSong(song("Mutter", lindemann, kruspe, landers, riedel));

        rammstein.addLp(mutter);
        artistRepository.save(rammstein);
    }

    private void seedRhcp() {
        Author kiedis = findOrCreateAuthor("Anthony Kiedis");
        Author flea = findOrCreateAuthor("Flea");
        Author frusciante = findOrCreateAuthor("John Frusciante");
        Author chadSmith = findOrCreateAuthor("Chad Smith");

        Artist rhcp = new Artist("Red Hot Chili Peppers", "Funk rock band from Los Angeles");

        Lp byTheWay = new Lp("By the Way", "Eighth studio album, 2002");
        byTheWay.addSong(song("By the Way", kiedis, flea, frusciante, chadSmith));
        byTheWay.addSong(song("Can't Stop", kiedis, flea, frusciante, chadSmith));
        byTheWay.addSong(song("Universally Speaking", kiedis, flea, frusciante, chadSmith));

        Lp californication = new Lp("Californication", "Seventh studio album, 1999");
        californication.addSong(song("Californication", kiedis, flea, frusciante, chadSmith));
        californication.addSong(song("Scar Tissue", kiedis, flea, frusciante, chadSmith));
        californication.addSong(song("Otherside", kiedis, flea, frusciante, chadSmith));

        rhcp.addLp(byTheWay);
        rhcp.addLp(californication);
        artistRepository.save(rhcp);
    }

    private void seedSepultura() {
        Author kisser = findOrCreateAuthor("Andreas Kisser");
        Author igorCavalera = findOrCreateAuthor("Igor Cavalera");
        Author pauloJr = findOrCreateAuthor("Paulo Jr.");
        Author derrickGreen = findOrCreateAuthor("Derrick Green");

        Artist sepultura = new Artist("Sepultura", "Brazilian metal band formed in Belo Horizonte");

        Lp against = new Lp("Against", "Seventh studio album, 1998");
        against.addSong(song("Hatred Aside", kisser, igorCavalera, pauloJr, derrickGreen));

        sepultura.addLp(against);
        artistRepository.save(sepultura);
    }

    private void seedSoundgarden() {
        Author cornell = findOrCreateAuthor("Chris Cornell");
        Author thayil = findOrCreateAuthor("Kim Thayil");
        Author cameron = findOrCreateAuthor("Matt Cameron");

        Artist soundgarden = new Artist("Soundgarden", "Grunge band from Seattle");

        Lp superunknown = new Lp("Superunknown", "Fourth studio album, 1994");
        superunknown.addSong(song("Black Hole Sun", cornell));
        superunknown.addSong(song("Spoonman", cornell));
        superunknown.addSong(song("Fell on Black Days", cornell, thayil, cameron));

        soundgarden.addLp(superunknown);
        artistRepository.save(soundgarden);
    }

    private void seedSystemOfADown() {
        Author serj = findOrCreateAuthor("Serj Tankian");
        Author daron = findOrCreateAuthor("Daron Malakian");
        Author odadjian = findOrCreateAuthor("Shavo Odadjian");
        Author dolmayan = findOrCreateAuthor("John Dolmayan");

        Artist soad = new Artist("System of a Down", "Armenian-American metal band from Glendale, California");

        Lp toxicity = new Lp("Toxicity", "Second studio album, 2001");
        toxicity.addSong(song("Chop Suey!", serj, daron));
        toxicity.addSong(song("Toxicity", serj, daron, odadjian, dolmayan));
        toxicity.addSong(song("Aerials", serj, daron));

        soad.addLp(toxicity);
        artistRepository.save(soad);
    }

    private void seedTheBeatles() {
        Author lennon = findOrCreateAuthor("John Lennon");
        Author mccartney = findOrCreateAuthor("Paul McCartney");
        Author harrison = findOrCreateAuthor("George Harrison");
        Author starr = findOrCreateAuthor("Ringo Starr");

        Artist beatles = new Artist("The Beatles", "English rock band formed in Liverpool in 1960");

        Lp abbeyRoad = new Lp("Abbey Road", "Eleventh studio album, 1969");
        abbeyRoad.addSong(song("Come Together", lennon, mccartney));
        abbeyRoad.addSong(song("Something", harrison));
        abbeyRoad.addSong(song("Here Comes the Sun", harrison));

        Lp sgtPeppers = new Lp("Sgt. Pepper's Lonely Hearts Club Band", "Eighth studio album, 1967");
        sgtPeppers.addSong(song("A Day in the Life", lennon, mccartney));
        sgtPeppers.addSong(song("Lucy in the Sky with Diamonds", lennon, mccartney));
        sgtPeppers.addSong(song("With a Little Help from My Friends", lennon, mccartney, starr));

        beatles.addLp(abbeyRoad);
        beatles.addLp(sgtPeppers);
        artistRepository.save(beatles);
    }

    private void seedTheCure() {
        Author smithR = findOrCreateAuthor("Robert Smith");
        Author gallup = findOrCreateAuthor("Simon Gallup");

        Artist cure = new Artist("The Cure", "English rock band formed in Crawley in 1978");

        Lp disintegration = new Lp("Disintegration", "Eighth studio album, 1989");
        disintegration.addSong(song("Pictures of You", smithR, gallup));
        disintegration.addSong(song("Lovesong", smithR, gallup));
        disintegration.addSong(song("Fascination Street", smithR, gallup));

        cure.addLp(disintegration);
        artistRepository.save(cure);
    }

    private void seedTheSmiths() {
        Author morrissey = findOrCreateAuthor("Morrissey");
        Author marr = findOrCreateAuthor("Johnny Marr");

        Artist smiths = new Artist("The Smiths", "English rock band formed in Manchester in 1982");

        Lp theQueenIsDead = new Lp("The Queen Is Dead", "Third studio album, 1986");
        theQueenIsDead.addSong(song("There Is a Light That Never Goes Out", morrissey, marr));
        theQueenIsDead.addSong(song("Bigmouth Strikes Again", morrissey, marr));
        theQueenIsDead.addSong(song("The Boy with the Thorn in His Side", morrissey, marr));

        smiths.addLp(theQueenIsDead);
        artistRepository.save(smiths);
    }

    private void seedU2() {
        Author bono = findOrCreateAuthor("Bono");
        Author edge = findOrCreateAuthor("The Edge");
        Author claytonU2 = findOrCreateAuthor("Adam Clayton");
        Author mullen = findOrCreateAuthor("Larry Mullen Jr.");

        Artist u2 = new Artist("U2", "Rock band from Dublin");

        Lp joshuaTree = new Lp("The Joshua Tree", "Fifth studio album, 1987");
        joshuaTree.addSong(song("With or Without You", bono, edge, claytonU2, mullen));
        joshuaTree.addSong(song("I Still Haven't Found What I'm Looking For", bono, edge, claytonU2, mullen));
        joshuaTree.addSong(song("Where the Streets Have No Name", bono, edge, claytonU2, mullen));

        u2.addLp(joshuaTree);
        artistRepository.save(u2);
    }

    // ---------- HELPERS ----------

    private Author findOrCreateAuthor(String name) {
        return authorRepository.findByName(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }

    /**
     * Helper para construir una Song con sus autores en una sola línea,
     * reduciendo verbosidad y evitando errores de "olvidé asignar autor".
     */
    private Song song(String title, Author... authors) {
        Song s = new Song(title);
        for (Author a : authors) {
            s.addAuthor(a);
        }
        return s;
    }
}
