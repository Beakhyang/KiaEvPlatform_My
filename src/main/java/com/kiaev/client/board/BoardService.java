package com.kiaev.client.board;

import com.kiaev.client.board.Board;
import com.kiaev.client.board.BoardRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service // 스프링부트에게 "이 클래스는 비즈니스 로직을 담당하는 서비스야!" 라고 알려줍니다.
@RequiredArgsConstructor // 롬복 기능: BoardRepository를 자동으로 연결(주입)해 줍니다.
public class BoardService {

	private final BoardRepository boardRepository;

	// 1. 게시글 저장 (방금 테스트로 확인했던 그 기능입니다!)
	public void saveBoard(Board board) {
		board.setBoardType("문의글");
		board.setNoticeYn("N");
		boardRepository.save(board);
	}

	// 2. 전체 게시글 목록 조회
	public List<Board> getBoardList() {
		// JPA가 제공하는 findAll()을 쓰면 DB의 모든 글을 다 가져옵니다.
		return boardRepository.findAll();
	}

	// 3. 특정 게시글 상세 조회 (글 번호로 찾기)
	public Board getBoardDetail(Long boardNo) {
		// findById로 찾고, 만약 그 번호의 글이 없으면 에러 메시지를 띄우도록 안전장치를 합니다.
		return boardRepository.findById(boardNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. 글 번호: " + boardNo));
	}

	// 4. 게시글 삭제
	public void deleteBoard(Long boardNo, Long memberNo) {
		Board board = boardRepository.findById(boardNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		if (!isAuthor(board, memberNo)) {
			throw new IllegalStateException("본인이 작성한 글만 삭제할 수 있습니다.");
		}

		boardRepository.delete(board);
	}

	// 5. 게시글 수정 (일정표 8-3)
	@Transactional // ★중요! DB의 데이터가 변경될 때 꼭 붙여주는 마법의 어노테이션입니다.
	public void updateBoard(Board updatedBoard, Long memberNo) {
		// 1. 기존 글을 DB에서 확실하게 찾아옵니다.
		Board board = boardRepository.findById(updatedBoard.getBoardNo())
				.orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

		// 2. 화면에서 넘어온 새 데이터(분류, 제목, 내용)로 기존 글의 내용만 쏙쏙 덮어씁니다.
		if (!isAuthor(board, memberNo)) {
			throw new IllegalStateException("본인이 작성한 글만 수정할 수 있습니다.");
		}

		board.setBoardType("문의글");
		board.setNoticeYn("N");
		board.setTitle(updatedBoard.getTitle());
		board.setContent(updatedBoard.getContent());

		// 3. 따로 save()를 안 해도, @Transactional이 붙어있으면
		// 스프링부트가 "어? 내용이 바뀌었네?" 하고 알아서 DB에 UPDATE 쿼리를 날려줍니다!
		// (이를 '더티 체킹'이라고 부릅니다)
	}

	/*
	 * // 6. 사용자용 목록만 조회
	 * 
	 * @Transactional(readOnly = true) public List<Board> getVisibleBoards() { //
	 * hidden 값이 'N'인 데이터만 가져옵니다. return
	 * boardRepository.findAllByHiddenOrderByBoardNoDesc("N");
	 */
	/*
	 * // (수정) 컨트롤러에서 Pageable을 넘겨받아 'N'인 데이터만 페이징해서 리턴합니다.
	 * 
	 * @Transactional(readOnly = true) public Page<Board>
	 * getVisibleBoardList(Pageable pageable) { return
	 * boardRepository.findAllByHiddenOrderByBoardNoDesc("N", pageable); }
	 */

	// 1. 숨김 처리된 게시글을 제외한 게시글 목록 조회

	/*
	 * public List<Board> getVisibleBoards() { // 'hidden' 값이 'N'인 게시글만 반환합니다.
	 * return boardRepository.findAllByHiddenOrderByBoardNoDesc("N"); }
	 */

	/*
	 * @Transactional(readOnly = true) public Page<Board>
	 * getVisibleBoardList(Pageable pageable) { // 'hidden' 값이 'N'인 데이터만 가져옵니다.
	 * return boardRepository.findAllByHiddenOrderByBoardNoDesc("N", pageable); }
	 */
	@Transactional(readOnly = true)
	public Page<Board> getVisibleBoardList(Pageable pageable) {

		// ★ 다중 정렬 조건 생성
		Sort sort = Sort.by(
				// 1순위: 공지사항 여부 (예: 'Y'가 'N'보다 먼저 오도록 내림차순 DESC)
				Sort.Order.desc("noticeYn"),

				// 2순위: 우선순위 (예: 숫자가 클수록 높은 순위라면 desc, 1이 가장 높다면 asc)
				Sort.Order.desc("priority"),

				// 3순위: 최신글 (글 번호 내림차순 DESC)
				Sort.Order.desc("boardNo"));

		// 컨트롤러에서 넘어온 페이지 번호와 크기는 그대로 유지하고, 정렬 조건만 새로 끼워 넣습니다.
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		// 'N'인 데이터만 가져오면서, 우리가 만든 다중 정렬 조건을 적용!
		return boardRepository.findAllByHidden("N", sortedPageable);
	}

	public boolean isAuthor(Board board, Long memberNo) {
		return board != null && memberNo != null && Objects.equals(board.getMemberNo(), memberNo);
	}

}
