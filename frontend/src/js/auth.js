export async function checkAuth() {
  try {
    const loginInfoString = localStorage.getItem('loginInfo') || sessionStorage.getItem('loginInfo');
    if (!loginInfoString) {
      updateUIForLoggedOut();
      return;
    }

    const loginInfo = JSON.parse(loginInfoString);
    const { token, user } = loginInfo;

    if (!token || !user) {
      logout();
      return;
    }

    // Check is token expired if rememberMe is true
    if (loginInfo.rememberMe) {
      const loginTime = loginInfo.loginTime || 0;
      const currentTime = new Date().getTime();
      const oneMonth = 30 * 24 * 60 * 60 * 1000; // 30 days in ms

      if (currentTime - loginTime > oneMonth) {
        logout();
        return;
      }
    }

    updateUIForLoggedIn(user);
  } catch (error) {
    console.error('Authentication check failed:', error);
    logout();
  }
}

function updateUIForLoggedIn(user) {
  $(document).ready(function() {
    $('.user-not-logged-in').addClass('d-none');
    $('.user-logged-in').removeClass('d-none');

    $('#current-username').text(user.username);
    $('#current-email').text(user.email);
  });
}

function updateUIForLoggedOut() {
  $('.user-logged-in').addClass('d-none');
  $('.user-not-logged-in').removeClass('d-none');

  $('#current-username').text('');
  $('#current-email').text('');
}

export function setupLogout() {
  $('#logout-btn').on('click', function(e) {
    e.preventDefault();
    logout();
  });
}

export function logout() {
  localStorage.removeItem('loginInfo');
  sessionStorage.removeItem('loginInfo');

  updateUIForLoggedOut();

  window.location.hash = '#home';
}

export function handleLoginSuccess(data, rememberMe = false) {
  const loginInfo = {
    token: data.token,
    user: data.user,
    loginTime: new Date().getTime(),
    rememberMe: rememberMe
  };

  if (rememberMe) {
    localStorage.setItem('loginInfo', JSON.stringify(loginInfo));
  } else {
    sessionStorage.setItem('loginInfo', JSON.stringify(loginInfo));
  }

  updateUIForLoggedIn(data.user);

  window.location.hash = '#home';
}