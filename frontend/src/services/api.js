// Token storage key (must match AuthContext)
const TOKEN_KEY = 'library_jwt_token';

const API_BASE = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api`;

/** Get stored JWT token from localStorage or sessionStorage */
function getToken() {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
}

/** Build headers with optional Authorization JWT bearer token */
function authHeaders(extraHeaders = {}) {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extraHeaders,
  };
}

// ─── Auth API ────────────────────────────────────────────────────────────────

export async function loginApi(usernameOrEmail, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ usernameOrEmail, password }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Login failed.');
  return data;
}

export async function getCurrentUserApi() {
  const res = await fetch(`${API_BASE}/auth/me`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to get current user.');
  return res.json();
}

export async function forgotPasswordApi(email) {
  const res = await fetch(`${API_BASE}/auth/forgot-password`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'Request failed.');
  return data;
}

// ─── Stats API ────────────────────────────────────────────────────────────────

export async function fetchStats() {
  const res = await fetch(`${API_BASE}/stats`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load statistics.');
  return res.json();
}

// ─── Books API ────────────────────────────────────────────────────────────────

export async function fetchBooks(search = '') {
  const url = search
    ? `${API_BASE}/books?search=${encodeURIComponent(search)}`
    : `${API_BASE}/books`;
  const res = await fetch(url, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to fetch books.');
  return res.json();
}

export async function saveBook(book, id = null) {
  const url = id ? `${API_BASE}/books/${id}` : `${API_BASE}/books`;
  const method = id ? 'PUT' : 'POST';
  const payload = id ? { ...book, available: book.quantity } : book;

  const res = await fetch(url, {
    method,
    headers: authHeaders(),
    body: JSON.stringify(payload),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Failed to save book.');
  return data;
}

export async function deleteBook(id) {
  const res = await fetch(`${API_BASE}/books/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Could not delete book.');
  return data;
}

// ─── Members API ───────────────────────────────────────────────────────────────

export async function fetchMembers(search = '') {
  const url = search
    ? `${API_BASE}/members?search=${encodeURIComponent(search)}`
    : `${API_BASE}/members`;
  const res = await fetch(url, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to fetch members.');
  return res.json();
}

export async function saveMember(member, id = null) {
  const url = id ? `${API_BASE}/members/${id}` : `${API_BASE}/members`;
  const method = id ? 'PUT' : 'POST';

  const res = await fetch(url, {
    method,
    headers: authHeaders(),
    body: JSON.stringify(member),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Failed to save member.');
  return data;
}

export async function deleteMember(id) {
  const res = await fetch(`${API_BASE}/members/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Could not delete member.');
  return data;
}

// ─── Issue/Return API ─────────────────────────────────────────────────────────

export async function fetchIssued() {
  const res = await fetch(`${API_BASE}/issued`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to fetch issued books.');
  return res.json();
}

export async function issueBookApi(bookId, memberId) {
  const res = await fetch(`${API_BASE}/issue`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ bookId, memberId }),
  });
  const data = await res.json();
  if (!res.ok) {
    let msg = data.error || 'Failed to issue book.';
    if (data.error === 'BOOK_NOT_FOUND') msg = 'Book ID not found.';
    if (data.error === 'NOT_AVAILABLE') msg = 'Book is out of stock / not available.';
    if (data.error === 'MEMBER_NOT_FOUND') msg = 'Member ID not found.';
    throw new Error(msg);
  }
  return data;
}

export async function returnBookApi(issueId) {
  const res = await fetch(`${API_BASE}/return`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ issueId }),
  });
  const data = await res.json();
  if (!res.ok) {
    let msg = data.error || 'Failed to return book.';
    if (data.error === 'ISSUE_NOT_FOUND') msg = 'Issue ID record not found.';
    if (data.error === 'ALREADY_RETURNED') msg = 'Book has already been returned.';
    throw new Error(msg);
  }
  return data;
}
