package com.coditramuntana.discography.song;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SongRepository extends JpaRepository<Song,Long> {
    long countByLpId(Long lpId);
}
