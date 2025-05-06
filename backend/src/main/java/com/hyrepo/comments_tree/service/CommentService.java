package com.hyrepo.comments_tree.service;

import com.hyrepo.comments_tree.controller.CommentController;
import com.hyrepo.comments_tree.model.dto.CommentRequest;
import com.hyrepo.comments_tree.model.dto.CommentResponse;
import com.hyrepo.comments_tree.model.entity.Comment;
import com.hyrepo.comments_tree.model.entity.CommentClosure;
import com.hyrepo.comments_tree.repository.CommentClosureRepository;
import com.hyrepo.comments_tree.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hyrepo.comments_tree.util.DateTimeUtil.now;

@Service
public class CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private static final int PARENT_CHILD_DEPTH = 1;
    private final CommentRepository commentRepository;
    private final CommentClosureRepository closureRepository;

    public CommentService(CommentRepository commentRepository, CommentClosureRepository closureRepository) {
        this.commentRepository = commentRepository;
        this.closureRepository = closureRepository;
    }

    @Transactional
    public Comment save(CommentRequest comment) {
        Comment savedComment = commentRepository.save(new Comment(comment.username(), comment.content(), now()));
        closureRepository.insertSelfRelation(savedComment.getId());
        if (comment.parentId() != null) {
            closureRepository.insertChildRelation(comment.parentId(), savedComment.getId());
        }
        return savedComment;
    }
    /**
     * This method constructs the full hierarchical tree of comments by utilizing a closure table pattern.
     * It builds the tree structure by establishing ancestor-descendant relationships between comments and
     * then sorts them by their creation date in descending order.
     *
     * The process involves the following steps:
     * 1. Fetch all comments from the repository and map them by their ID for easy lookup.
     * 2. Retrieve the ancestor-descendant relationships from the closure table.
     * 3. Identify and process all root comments (those with no parent).
     * 4. For each closure entry, link the descendant comment to its corresponding parent comment.
     * 5. Remove the descendants from the list of root comments since they have parents.
     * 6. Finally, sort the root comments by creation time and recursively sort their nested sub-comments.
     *
     * The method returns a list of top-level comments (those with no parents) that include their nested comments,
     * all sorted by creation time, with each comment's sub-comments also ordered.
     *
     * @return a list of top-level comments with their sub-comments, sorted by creation time in descending order.
     */
    @Transactional
    public List<CommentResponse> findAll() {
        long startTime = now();

        Map<Long, CommentResponse> allComments = commentRepository.findAll().stream()
                .map(CommentResponse::new)
                .collect(Collectors.toMap(CommentResponse::getId, comment -> comment));
        List<CommentClosure> closures = closureRepository.findALlByDepth(PARENT_CHILD_DEPTH);

        // topNode: node that doesn't has any parent node
        List<Long> topNodeIds = allComments.values().stream().map(CommentResponse::getId).collect(Collectors.toList());
        for (var closure : closures) {
            // find all parent nodes
            CommentResponse parent = allComments.get(closure.getAncestorId());
            CommentResponse descendant = allComments.get(closure.getDescendantId());
            // set sub comments for parent
            parent.getComments().add(descendant);
            // set parent ID for descendant
            descendant.setParentId(parent.getId());
            // remove any nodes that has parent node from the top nodes list
            topNodeIds.remove(closure.getDescendantId());
        }

        LOGGER.info("Finished rendering comment tree with {} comments, time cost: {}ms.", allComments.size(), now() - startTime);

        return topNodeIds.stream()
                .map(allComments::get)
                .sorted((o1, o2) -> (int) (o2.getCreatedAt() - o1.getCreatedAt()))
                .peek(CommentResponse::sortComments)
                .toList();
    }
}
