package com.example.jolvre.post.service;


import com.example.jolvre.post.dto.commentRequest;
import com.example.jolvre.post.dto.commentResponse;
import com.example.jolvre.post.entity.Comment;
import com.example.jolvre.post.entity.Post;
import com.example.jolvre.post.repository.CommentRepository;
import com.example.jolvre.post.repository.PostRepository;
import com.example.jolvre.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Builder
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public void writeComment (Long postId, commentRequest request, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 확인 불가"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(user);
        comment.setUserName(user.getNickname());

        commentRepository.save(comment);
        log.info("[comment] : 댓글 작성 완료");

    }

    public Page<commentResponse> findAllComment(Pageable pageable, Long postId) {
        Post post = postService.findById(postId);
        Page<Comment> commentList = commentRepository.findByPost(post, pageable);
        return commentList.map(commentResponse::findFromComment);
    }

    @Transactional
    public void deleteComment (Long commentId) {
        Comment comment = findCommentById(commentId);
        comment.setPost(null);
        comment.setUser(null);
        commentRepository.delete(comment);
        log.info("[comment] : {} 댓글 삭제 완료", commentId);
    }

    public commentResponse getCommentById(Long commentId) {
        Comment comment = findCommentById(commentId);

        return commentResponse.findFromComment(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Does not exist"));
    }
}