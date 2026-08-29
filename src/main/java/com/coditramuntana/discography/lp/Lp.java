package com.coditramuntana.discography.lp;

import com.coditramuntana.discography.artist.Artist;
import com.coditramuntana.discography.song.Song;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Lp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable= false, length=100)
    private String name;

    @Lob
    private String description;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="artist_id", nullable = false)
    private Artist artist;

    @OneToMany(mappedBy="lp",cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();

    // Constructores
    public Lp() {
    }

    public Lp(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getter y Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }


    // Métodos de conveniencia para relación bidireccional

    public void addSong(Song song) {
        this.songs.add(song);
        song.setLp(this);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
        song.setLp(null);
    }
    // Equals y HashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lp lp)) return false;
        return id != null && Objects.equals(id, lp.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
