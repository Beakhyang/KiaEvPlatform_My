package com.kiaev.client.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	// 'hidden' 값이 'N'인 데이터만 가져오고, 페이징 처리
	/* Page<Board> findAllByHiddenOrderByBoardNoDesc(String hidden, Pageable pageable); */

	
	// 정렬(OrderBy)은 서비스 로직에서 Pageable에 담아서 넘길 것이므로 이름에서 제거합니다.
    Page<Board> findAllByHidden(String hidden, Pageable pageable);

}

