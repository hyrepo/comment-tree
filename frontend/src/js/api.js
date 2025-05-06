const API_BASE_URL = 'http://localhost:8080';

export async function fetchComments() {
  try {
    const response = await fetch(`${API_BASE_URL}/comments`);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Fetch comments failed:', error);
    throw error;
  }
}

export async function postComment(commentData) {
  try {
    const loginInfo = JSON.parse(localStorage.getItem('loginInfo') || sessionStorage.getItem('loginInfo') || '{}');
    const token = loginInfo.token;

    if (!token) {
      throw new Error('Need to login before posting comments');
    }

    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify(commentData)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Post comment failed:', error);
    throw error;
  }
}

export async function login(credentials) {
  try {
    const response = await fetch(`${API_BASE_URL}/sessions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(credentials)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Login failed:', error);
    throw error;
  }
}

export async function register(userData) {
  try {
    const response = await fetch(`${API_BASE_URL}/users`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(userData)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
  } catch (error) {
    console.error('Register failed:', error);
    throw error;
  }
}