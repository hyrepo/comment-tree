import { fetchComments, postComment } from '../js/api.js';
import { checkAuth } from '../js/auth.js';
export async function initHomePage() {
  await loadComments();

  // Post comment
  $('#comment-form').on('submit', function(e) {
    e.preventDefault();
    submitComment();
  });

  // Reply
  $('#reply-form').on('submit', function(e) {
    e.preventDefault();
    submitReply();
  });
}

async function loadComments() {
  try {
    $('#comments-container').html(`
      <div class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">Loading...</p>
      </div>
    `);

    const comments = await fetchComments();

   async function renderComments() {
     if (comments.length === 0) {
       $('#comments-container').html(`
         <div class="text-center py-5">
           <i class="fas fa-comments fa-3x text-muted mb-3"></i>
           <p class="lead">No comments yet.</p>
         </div>
       `);
       return;
     }

     const commentsHtml = comments.map(comment => renderComment(comment)).join('');
     $('#comments-container').html(`<div class="comments-list">${commentsHtml}</div>`);

     $('.reply-btn').on('click', function() {
       const commentId = $(this).data('id');
       openReplyModal(commentId);
     });
   };

   await renderComments();
   checkAuth();

  } catch (error) {
    console.error('Load comments failed:', error);
    $('#comments-container').html(`
      <div class="alert alert-danger">
        <i class="fas fa-exclamation-triangle me-2"></i>
        Load comments failed, please refresh page to retry.
      </div>
    `);
  }
}

function renderComment(comment, depth = 0) {
  const date = new Date(comment.createdAt);
  const formattedDate = date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    fractionalSecondDigits: 3,
  });

  let childCommentsHtml = '';
  if (comment.comments && comment.comments.length > 0) {
    childCommentsHtml = comment.comments.map(childComment => renderComment(childComment, depth + 1)).join('');
  }

  const replyButton = `
    <button class="btn btn-sm btn-outline-primary reply-btn user-logged-in d-none" data-id="${comment.id}">
      <i class="fas fa-reply me-1"></i>reply
    </button>
  `;

  const marginClass = depth > 0 ? 'ms-4 border-start border-light ps-3' : '';

  return `
    <div class="comment-item mb-3 ${marginClass}" data-id="${comment.id}">
      <div class="d-flex">
        <div class="flex-shrink-0">
          <div class="avatar bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
            ${comment.username.charAt(0).toUpperCase()}
          </div>
        </div>
        <div class="flex-grow-1 ms-3">
          <div class="d-flex align-items-center mb-1">
            <h6 class="mb-0 me-2">${comment.username}</h6>
            <small class="text-muted">${formattedDate}</small>
          </div>
          <p class="mb-2">${comment.content}</p>
          <div class="d-flex">
            ${replyButton}
          </div>
          <div class="child-comments mt-3">
            ${childCommentsHtml}
          </div>
        </div>
      </div>
    </div>
  `;
}

async function submitComment() {
  const content = $('#comment-content').val().trim();

  if (content.length < 3 || content.length > 200) {
    alert('Length of comment has to between 3~200');
    return;
  }

  try {
    const loginInfo = JSON.parse(localStorage.getItem('loginInfo') || sessionStorage.getItem('loginInfo') || '{}');
    if (!loginInfo || !loginInfo.user.username) {
      alert('Need to login before posting comments');
      window.location.hash = '#login';
      return;
    }

    const commentData = {
      username: loginInfo.user.username,
      content: content,
      parentId: parseInt($('#comment-parent-id').val()) || 0
    };

    await postComment(commentData);

    $('#comment-content').val('');
    $('#comment-char-count').text('200');

    loadComments();
  } catch (error) {
    console.error('Post comment failed:', error);
    alert('Post comment failed, please try again.');
  }
}

function openReplyModal(commentId) {
  $('#reply-parent-id').val(commentId);
  $('#reply-content').val('');
  $('#reply-char-count').text('200');

  const replyModal = new bootstrap.Modal(document.getElementById('reply-modal'));
  replyModal.show();
}

async function submitReply() {
  const content = $('#reply-content').val().trim();
  const parentId = parseInt($('#reply-parent-id').val());

  if (content.length < 3 || content.length > 200) {
    alert('Length of comment has to between 3~200');
    return;
  }

  try {
    const loginInfo = JSON.parse(localStorage.getItem('loginInfo') || sessionStorage.getItem('loginInfo') || '{}');
    if (!loginInfo || !loginInfo.user.username) {
      alert('Need to login before posting comments');
      window.location.hash = '#login';
      return;
    }

    const commentData = {
      username: loginInfo.user.username,
      content: content,
      parentId: parentId
    };

    await postComment(commentData);

    const replyModal = bootstrap.Modal.getInstance(document.getElementById('reply-modal'));
    replyModal.hide();

    loadComments();
  } catch (error) {
    console.error('Reply failed:', error);
    alert('Reply failed, please try again');
  }
}