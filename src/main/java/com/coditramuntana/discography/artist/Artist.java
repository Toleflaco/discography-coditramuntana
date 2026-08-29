package com.coditramuntana.discography.artist;

import com.coditramuntana.discography.lp.Lp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Lob
    private String description;
    @OneToMany(mappedBy = "artist", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Lp> lps = new ArrayList<>();


    // Constructores
    public Artist() {
    }

    public Artist(String name, String description) {
        this.name = name;
        this.description = description;

    }

    // Getter y Setter
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

    public List<Lp> getLps() {
        return lps;
    }

    public void setLps(List<Lp> lps) {
        this.lps = lps;
    }

    // Métodos de conveniencia para relación bidireccional
    public void addLp(Lp lp) {
        this.lps.add(lp);
        lp.setArtist(this);
    }
    public void removeLp(Lp lp) {
        this.lps.remove(lp);
        lp.setArtist(null);
    }

    // Equals y HashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist artist)) return false;
        return id != null && Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();   // constante, independiente del id
    }
}
