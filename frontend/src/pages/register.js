import { register } from '../js/api.js';
import { handleLoginSuccess } from '../js/auth.js';
import { validateUsername, validatePassword, validateEmail } from '../js/validation.js';

export function initRegisterPage() {
  setupRegisterPage();

  $('#register-form').on('submit', function(e) {
    e.preventDefault();
    handleRegistration();
  });

  $('#register-username').on('blur', function() {
    validateUsernameField();
  });

  $('#register-password').on('input', function() {
    validatePasswordField();
  });

  $('#register-email').on('blur', function() {
    validateEmailField();
  });
}

function setupRegisterPage() {
  $('#register-page').html(`
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="text-center mb-4">Registration</h2>

            <form id="register-form">
              <div class="mb-3">
                <label for="register-username" class="form-label">Username</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-user"></i>
                  </span>
                  <input type="text" class="form-control" id="register-username" placeholder="5 to 20 characters, containing only letters and numbers" required>
                </div>
                <div id="username-feedback" class="form-text"></div>
              </div>

              <div class="mb-3">
                <label for="register-email" class="form-label">Email</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-envelope"></i>
                  </span>
                  <input type="email" class="form-control" id="register-email" placeholder="Enter a valid Email address" required>
                </div>
                <div id="email-feedback" class="form-text"></div>
              </div>

              <div class="mb-3">
                <label for="register-password" class="form-label">Password</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-lock"></i>
                  </span>
                  <input type="password" class="form-control" id="register-password" placeholder="8-20 characters" required>
                </div>
                <div id="password-feedback" class="form-text"></div>
                <div class="password-strength mt-2">
                  <div class="row g-1">
                    <div class="col">
                      <div class="progress" style="height: 5px;">
                        <div id="length-check" class="progress-bar bg-danger" style="width: 0%"></div>
                      </div>
                      <small>Length (8-20)</small>
                    </div>
                    <div class="col">
                      <div class="progress" style="height: 5px;">
                        <div id="uppercase-check" class="progress-bar bg-danger" style="width: 0%"></div>
                      </div>
                      <small>Uppercase Letters</small>
                    </div>
                    <div class="col">
                      <div class="progress" style="height: 5px;">
                        <div id="lowercase-check" class="progress-bar bg-danger" style="width: 0%"></div>
                      </div>
                      <small>Lowercase Letters</small>
                    </div>
                    <div class="col">
                      <div class="progress" style="height: 5px;">
                        <div id="number-check" class="progress-bar bg-danger" style="width: 0%"></div>
                      </div>
                      <small>Numbers</small>
                    </div>
                    <div class="col">
                      <div class="progress" style="height: 5px;">
                        <div id="special-check" class="progress-bar bg-danger" style="width: 0%"></div>
                      </div>
                      <small>Special Characters</small>
                    </div>
                  </div>
                </div>
              </div>

              <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary" id="register-button" disabled>Register</button>
                <button type="button" class="btn btn-outline-secondary" onclick="window.location.hash='#login'">
                  Already has an account？Log in here
                </button>
              </div>
            </form>

            <div id="register-alert" class="alert mt-3 d-none">
            </div>
          </div>
        </div>
      </div>
    </div>
  `);
}

async function handleRegistration() {
  try {
    const username = $('#register-username').val().trim();
    const email = $('#register-email').val().trim();
    const password = $('#register-password').val();

    const isUsernameValid = validateUsernameField();
    const isEmailValid = validateEmailField();
    const isPasswordValid = validatePasswordField();

    if (!isUsernameValid || !isEmailValid || !isPasswordValid) {
      showRegisterAlert('Please enter all necessary types.', 'danger');
      return;
    }

    const submitBtn = $('#register-button');
    submitBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Registering...');

    const registerData = {
      username: username,
      email: email,
      password: password
    };

    await register(registerData);

    showRegisterAlert('Register success!', 'success');
  } catch (error) {
    console.error('Register failed:', error);
    showRegisterAlert('Register failed，username or Email may already exist', 'danger');

    $('#register-button').prop('disabled', false).text('Register');
  }
}

function showRegisterAlert(message, type = 'danger') {
  const alertBox = $('#register-alert');
  alertBox.text(message)
    .removeClass('d-none alert-danger alert-success alert-warning')
    .addClass(`alert-${type}`);
  
  if (type === 'success') {
    document.getElementById('register-button').remove();
  }

  if (type === 'danger') {
    setTimeout(() => {
      alertBox.addClass('d-none');
    }, 5000);
  }
}

function validateUsernameField() {
  const username = $('#register-username').val().trim();
  const feedbackElement = $('#username-feedback');

  const validationResult = validateUsername(username);

  if (validationResult.valid) {
    feedbackElement.html('<i class="fas fa-check-circle text-success"></i> Valid Username').addClass('text-success').removeClass('text-danger');
    $('#register-username').removeClass('is-invalid').addClass('is-valid');
  } else {
    feedbackElement.html(`<i class="fas fa-exclamation-circle"></i> ${validationResult.message}`).addClass('text-danger').removeClass('text-success');
    $('#register-username').removeClass('is-valid').addClass('is-invalid');
  }

  updateRegisterButtonState();
  return validationResult.valid;
}

function validatePasswordField() {
  const password = $('#register-password').val();
  const feedbackElement = $('#password-feedback');

  const validationResult = validatePassword(password);

  updatePasswordStrengthIndicators(password);

  if (validationResult.valid) {
    feedbackElement.html('<i class="fas fa-check-circle text-success"></i> Valid Password').addClass('text-success').removeClass('text-danger');
    $('#register-password').removeClass('is-invalid').addClass('is-valid');
  } else {
    feedbackElement.html(`<i class="fas fa-exclamation-circle"></i> ${validationResult.message}`).addClass('text-danger').removeClass('text-success');
    $('#register-password').removeClass('is-valid').addClass('is-invalid');
  }

  updateRegisterButtonState();
  return validationResult.valid;
}

function validateEmailField() {
  const email = $('#register-email').val().trim();
  const feedbackElement = $('#email-feedback');

  const validationResult = validateEmail(email);

  if (validationResult.valid) {
    feedbackElement.html('<i class="fas fa-check-circle text-success"></i> Valid Email').addClass('text-success').removeClass('text-danger');
    $('#register-email').removeClass('is-invalid').addClass('is-valid');
  } else {
    feedbackElement.html(`<i class="fas fa-exclamation-circle"></i> ${validationResult.message}`).addClass('text-danger').removeClass('text-success');
    $('#register-email').removeClass('is-valid').addClass('is-invalid');
  }

  updateRegisterButtonState();
  return validationResult.valid;
}

function updatePasswordStrengthIndicators(password) {
  const lengthValid = password.length >= 8 && password.length <= 20;
  $('#length-check').css('width', lengthValid ? '100%' : '0%')
    .removeClass(lengthValid ? 'bg-danger' : 'bg-success')
    .addClass(lengthValid ? 'bg-success' : 'bg-danger');

  const uppercaseValid = /[A-Z]/.test(password);
  $('#uppercase-check').css('width', uppercaseValid ? '100%' : '0%')
    .removeClass(uppercaseValid ? 'bg-danger' : 'bg-success')
    .addClass(uppercaseValid ? 'bg-success' : 'bg-danger');

  const lowercaseValid = /[a-z]/.test(password);
  $('#lowercase-check').css('width', lowercaseValid ? '100%' : '0%')
    .removeClass(lowercaseValid ? 'bg-danger' : 'bg-success')
    .addClass(lowercaseValid ? 'bg-success' : 'bg-danger');

  const numberValid = /[0-9]/.test(password);
  $('#number-check').css('width', numberValid ? '100%' : '0%')
    .removeClass(numberValid ? 'bg-danger' : 'bg-success')
    .addClass(numberValid ? 'bg-success' : 'bg-danger');

  const specialValid = /[^A-Za-z0-9]/.test(password);
  $('#special-check').css('width', specialValid ? '100%' : '0%')
    .removeClass(specialValid ? 'bg-danger' : 'bg-success')
    .addClass(specialValid ? 'bg-success' : 'bg-danger');
}

function updateRegisterButtonState() {
  const username = $('#register-username').val().trim();
  const email = $('#register-email').val().trim();
  const password = $('#register-password').val();

  const isUsernameValid = validateUsername(username).valid;
  const isEmailValid = validateEmail(email).valid;
  const isPasswordValid = validatePassword(password).valid;

  $('#register-button').prop('disabled', !(isUsernameValid && isEmailValid && isPasswordValid));
}