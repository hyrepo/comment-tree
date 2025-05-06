export function validateUsername(username) {
  if (!username) {
    return {
      valid: false,
      message: 'Username is required'
    };
  }

  if (!/^[a-zA-Z0-9]+$/.test(username)) {
    return {
      valid: false,
      message: 'Username can only contains letters and numbers'
    };
  }

  if (username.length < 5 || username.length > 20) {
    return {
      valid: false,
      message: 'Length of username must between 5-20'
    };
  }

  return {
    valid: true,
    message: 'Valid username'
  };
}

export function validatePassword(password) {
  if (!password) {
    return {
      valid: false,
      message: 'Password is required'
    };
  }

  if (password.length < 8 || password.length > 20) {
    return {
      valid: false,
      message: 'Length of password must between 8-20'
    };
  }

  if (!/[A-Z]/.test(password)) {
    return {
      valid: false,
      message: 'Password must contains at least one uppercase letter'
    };
  }

  if (!/[a-z]/.test(password)) {
    return {
      valid: false,
      message: 'Password must contains at least one lowercase letter'
    };
  }

  if (!/[0-9]/.test(password)) {
    return {
      valid: false,
      message: 'Password must contains at least one digit'
    };
  }

  if (!/[^A-Za-z0-9]/.test(password)) {
    return {
      valid: false,
      message: 'Password must contains at least one special character'
    };
  }

  return {
    valid: true,
    message: 'Valid password'
  };
}

export function validateEmail(email) {
  if (!email) {
    return {
      valid: false,
      message: 'Email is required'
    };
  }

  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(email)) {
    return {
      valid: false,
      message: 'Invalid Email address'
    };
  }

  return {
    valid: true,
    message: 'Valid Email'
  };
}