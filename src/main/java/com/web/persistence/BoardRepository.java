package com.web.persistence;



import org.springframework.data.jpa.repository.JpaRepository;

import com.web.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
	
	
	
}
