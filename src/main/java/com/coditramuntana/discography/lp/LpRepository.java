package com.coditramuntana.discography.lp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface LpRepository extends JpaRepository<Lp, Long> {
    long countByArtistId(Long artistId);

    @Query(
            value = """
                    SELECT l FROM Lp l
                    JOIN FETCH l.artist
                    """,
            countQuery = """
                    SELECT COUNT(l) FROM Lp l
                    """
    )
    Page<Lp> findAllWithArtist(Pageable pageable);

    @Query(
            value = """
                        SELECT l FROM Lp l 
                        JOIN FETCH l.artist
                        WHERE lower(l.artist.name) LIKE lower(concat('%', :artistName, '%'))
                    """,
            countQuery = """
                    SELECT COUNT(l) FROM Lp l
                    WHERE lower(l.artist.name) LIKE lower(concat('%', :artistName, '%'))
                    """
    )
    Page<Lp> findAllByArtistNameWithArtist(@Param("artistName") String artistName, Pageable pageable);

    @Query("""
            SELECT l FROM Lp l
            JOIN FETCH l.artist
            WHERE l.id = :id
            """)
    Optional<Lp> findByIdWithArtist(@Param("id") Long id);
    Optional<Lp> findByArtistIdAndName(Long artistId, String name);
}
