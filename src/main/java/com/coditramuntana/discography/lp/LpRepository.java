package com.coditramuntana.discography.lp;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LpRepository extends JpaRepository<Lp,Long> {
    long countByArtistId(Long artistId);
}
