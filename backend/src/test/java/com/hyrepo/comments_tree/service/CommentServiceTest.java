package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.model.entity.Comment;
import com.hyrepo.comments_tree.model.entity.CommentClosure;
import com.hyrepo.comments_tree.model.dto.CommentRequest;
import com.hyrepo.comments_tree.model.dto.CommentResponse;
import com.hyrepo.comments_tree.repository.CommentClosureRepository;
import com.hyrepo.comments_tree.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

import static com.hyrepo.comments_tree.util.DateTimeUtil.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CommentService.class)
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentClosureRepository closureRepository;

    @Test
    void shouldSaveNewCommentAndUpdateCorrespondingTables() {
        Comment savedComment = commentRepository.save(new Comment("userA", "This is top comment", now()));
        closureRepository.insertSelfRelation(savedComment.getId());

        Comment savedComment2 = commentService.save(new CommentRequest("userB", "This is sub comment", savedComment.getId()));
        Comment savedComment3 = commentService.save(new CommentRequest("userC", "This is another top comment", null));

        List<Comment> comments = commentRepository.findAll();
        List<CommentClosure> closures = closureRepository.findAll();

        assertThat(closures.size()).isEqualTo(4);
        assertThat(comments.size()).isEqualTo(3);
        assertThat(closures.stream().anyMatch(it -> (it.getAncestorId().equals(savedComment.getId()) && it.getDescendantId().equals(savedComment.getId()) && it.getDepth() == 0))).isTrue();
        assertThat(closures.stream().anyMatch(it -> (it.getAncestorId().equals(savedComment2.getId()) && it.getDescendantId().equals(savedComment2.getId()) && it.getDepth() == 0))).isTrue();
        assertThat(closures.stream().anyMatch(it -> (it.getAncestorId().equals(savedComment.getId()) && it.getDescendantId().equals(savedComment2.getId()) && it.getDepth() == 1))).isTrue();
    }

    /*
    Test logic:
    1. Insert comments by time order
    2. Verify the comment tree after rendering and sorting is equal to:
    
    Comment 2
    Comment 1
        Comment 1_2
            Comment 1_2_1
        Comment 1_1
     */
    @Test
    void shouldReturnAllCommentsWithTimeDescendingOrder() throws InterruptedException {
        String username = "user";
        Comment comment1 = commentService.save(new CommentRequest(username, "Comment 1", null));
        Thread.sleep(10);
        Comment comment1_1 = commentService.save(new CommentRequest(username, "Comment 1_1", comment1.getId()));
        Thread.sleep(10);
        Comment comment1_2 = commentService.save(new CommentRequest(username, "Comment 1_2", comment1.getId()));
        Thread.sleep(10);
        Comment comment1_2_1 = commentService.save(new CommentRequest(username, "Comment 1_2_1", comment1_2.getId()));
        Thread.sleep(10);
        Comment comment2 = commentService.save(new CommentRequest(username, "Comment_2", null));

        List<CommentResponse> allComments = commentService.findAll();

        assertThat(allComments.size()).isEqualTo(2);
        // check comment2
        CommentResponse comment2Response = allComments.get(0);
        assertThat(comment2Response.getId()).isEqualTo(comment2.getId());
        assertThat(comment2Response.getComments().isEmpty()).isTrue();
        assertThat(comment2Response.getParentId()).isNull();

        // check comment1
        CommentResponse comment1Response = allComments.get(1);
        assertThat(comment1Response.getId()).isEqualTo(comment1.getId());
        assertThat(comment1Response.getParentId()).isNull();
        assertThat(comment1Response.getComments().size()).isEqualTo(2);

        // check comment1_2
        CommentResponse comment1_2Response = comment1Response.getComments().get(0);
        assertThat(comment1_2Response.getId()).isEqualTo(comment1_2.getId());
        assertThat(comment1_2Response.getParentId()).isEqualTo(comment1.getId());
        assertThat(comment1_2Response.getComments().size()).isEqualTo(1);


        // check comment1_2_1
        CommentResponse comment1_2_1Response = comment1_2Response.getComments().get(0);
        assertThat(comment1_2_1Response.getId()).isEqualTo(comment1_2_1.getId());
        assertThat(comment1_2_1Response.getParentId()).isEqualTo(comment1_2Response.getId());
        assertThat(comment1_2_1Response.getComments().size()).isEqualTo(0);

        // check comment1_1
        CommentResponse comment1_1Response = comment1Response.getComments().get(1);
        assertThat(comment1_1Response.getId()).isEqualTo(comment1_1.getId());
        assertThat(comment1_1Response.getParentId()).isEqualTo(comment1.getId());
        assertThat(comment1_1Response.getComments().size()).isEqualTo(0);


    }
}