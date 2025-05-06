import { initHomePage } from './pages/home.js';
import { initLoginPage } from './pages/login.js';
import { initRegisterPage } from './pages/register.js';
import { checkAuth, setupLogout } from './js/auth.js';
import './css/main.css';

$(document).ready(function() {
  initRouting();

  initHomePage();
  initLoginPage();
  initRegisterPage();
  checkAuth();

  setupLogout();

  $('#comment-content').on('input', function() {
    const maxLength = 200;
    const currentLength = $(this).val().length;
    const remaining = maxLength - currentLength;
    $('#comment-char-count').text(remaining);
  });

  $('#reply-content').on('input', function() {
    const maxLength = 200;
    const currentLength = $(this).val().length;
    const remaining = maxLength - currentLength;
    $('#reply-char-count').text(remaining);
  });
});

function initRouting() {
  const pages = {
    '': 'home-page',
    '#': 'home-page',
    '#home': 'home-page',
    '#login': 'login-page',
    '#register': 'register-page'
  };

  function route() {
    const hash = window.location.hash || '';
    const pageId = pages[hash] || pages[''];

    // Disable and enable pages based on route
    $('#home-page, #login-page, #register-page').addClass('d-none');
    $(`#${pageId}`).removeClass('d-none');
  }

  route();

  $(window).on('hashchange', route);

  $('.navbar-nav .nav-link').on('click', function(e) {
    if ($(this).attr('href').startsWith('#')) {
      e.preventDefault();
      window.location.hash = $(this).attr('href');
    }
  });
}