import { login } from '../js/api.js';
import { handleLoginSuccess } from '../js/auth.js';

export function initLoginPage() {
  setupLoginPage();

  $('#login-form').on('submit', function(e) {
    e.preventDefault();
    handleLogin();
  });
}

function setupLoginPage() {
  $('#login-page').html(`
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="text-center mb-4">Login</h2>

            <form id="login-form">
              <div class="mb-3">
                <label for="login-principal" class="form-label" id="principal-label">Username or Email</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-user" id="principal-icon"></i>
                  </span>
                  <input type="text" class="form-control" id="login-principal" placeholder="Enter username or Email" required>
                </div>
              </div>

              <div class="mb-3">
                <label for="login-password" class="form-label">Password</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-lock"></i>
                  </span>
                  <input type="password" class="form-control" id="login-password" placeholder="Enter password" required>
                </div>
              </div>

              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="remember-me">
                <label class="form-check-label" for="remember-me">Keep login status(30 days)</label>
              </div>

              <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary">Log in</button>
                <button type="button" class="btn btn-outline-secondary" onclick="window.location.hash='#register'">
                  No account? Register now
                </button>
              </div>
            </form>

            <div id="login-alert" class="alert alert-danger mt-3 d-none">
            </div>
          </div>
        </div>
      </div>
    </div>
  `);
}

async function handleLogin() {
  try {
    const principal = $('#login-principal').val().trim();
    const password = $('#login-password').val();
    const rememberMe = $('#remember-me').is(':checked');

    if (!principal || !password) {
      showLoginError('Please enter all required fields.');
      return;
    }

    const submitBtn = $('#login-form button[type="submit"]');
    const originalBtnText = submitBtn.text();
    submitBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Login...');

    const loginData = {
      principal: principal,
      password: password
    };

    const response = await login(loginData);

    handleLoginSuccess(response, rememberMe);

    $('#login-form')[0].reset();
  } catch (error) {
    console.error('Log in failed:', error);
    showLoginError('Log in failed，please check username/Email and password');
  } finally {
    const submitBtn = $('#login-form button[type="submit"]');
    submitBtn.prop('disabled', false).text('Log in');
  }
}

function showLoginError(message) {
  const alertBox = $('#login-alert');
  alertBox.text(message).removeClass('d-none');

  setTimeout(() => {
    alertBox.addClass('d-none');
  }, 5000);
}
