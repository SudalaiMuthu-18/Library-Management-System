import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// SVG Icons
const BookOpenIcon = () => (
  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/>
    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>
  </svg>
);

const EyeIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
    <circle cx="12" cy="12" r="3"/>
  </svg>
);

const EyeOffIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
    <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
    <line x1="1" y1="1" x2="23" y2="23"/>
  </svg>
);

const LockIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
  </svg>
);

const UserIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
    <circle cx="12" cy="7" r="4"/>
  </svg>
);

const AlertIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10"/>
    <line x1="12" y1="8" x2="12" y2="12"/>
    <line x1="12" y1="16" x2="12.01" y2="16"/>
  </svg>
);

const MailIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
    <polyline points="22,6 12,13 2,6"/>
  </svg>
);

const CheckCircleIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
    <polyline points="22 4 12 14.01 9 11.01"/>
  </svg>
);

// Floating particle component
function Particle({ style }) {
  return <div className="login-particle" style={style} />;
}

// ForgotPasswordModal component
function ForgotPasswordModal({ isOpen, onClose }) {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim()) { setError('Please enter your email address.'); return; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { setError('Please enter a valid email address.'); return; }

    setLoading(true);
    setError('');
    try {
      await fetch(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/auth/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });
      setSubmitted(true);
    } catch {
      setError('Unable to process request. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setEmail('');
    setSubmitted(false);
    setError('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal-card" onClick={e => e.stopPropagation()}>
        <button className="modal-close-btn" onClick={handleClose} aria-label="Close modal">×</button>

        {!submitted ? (
          <>
            <div className="modal-icon-wrap">
              <MailIcon />
            </div>
            <h2 className="modal-title">Forgot Password?</h2>
            <p className="modal-subtitle">
              Enter the email address associated with your account and we'll send you a reset link.
            </p>
            <form onSubmit={handleSubmit} className="modal-form">
              {error && (
                <div className="login-error-box modal-error">
                  <AlertIcon /> {error}
                </div>
              )}
              <div className="login-field">
                <label className="login-label" htmlFor="forgot-email">Email Address</label>
                <div className="login-input-wrap">
                  <span className="login-input-icon"><MailIcon /></span>
                  <input
                    id="forgot-email"
                    type="email"
                    className="login-input"
                    placeholder="your@email.com"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    autoFocus
                  />
                </div>
              </div>
              <button type="submit" className="login-btn" disabled={loading}>
                {loading ? <span className="login-spinner" /> : 'Send Reset Link'}
              </button>
            </form>
          </>
        ) : (
          <div className="modal-success">
            <div className="modal-success-icon"><CheckCircleIcon /></div>
            <h2 className="modal-title">Check Your Email</h2>
            <p className="modal-subtitle">
              If an account exists for <strong>{email}</strong>, a password reset link will be sent shortly.
            </p>
            <button className="login-btn" onClick={handleClose}>Back to Login</button>
          </div>
        )}
      </div>
    </div>
  );
}

// Main LoginPage component
export function LoginPage() {
  const { login, loading, error, clearError, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [forgotOpen, setForgotOpen] = useState(false);
  const [localError, setLocalError] = useState('');
  const firstInputRef = useRef(null);

  const from = location.state?.from?.pathname || '/';

  // Auto-redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  useEffect(() => {
    if (firstInputRef.current) firstInputRef.current.focus();
  }, []);

  // Sync context error to local error
  useEffect(() => {
    if (error) setLocalError(error);
  }, [error]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError('');
    clearError();

    if (!usernameOrEmail.trim()) { setLocalError('Please enter your username or email.'); return; }
    if (!password) { setLocalError('Please enter your password.'); return; }

    const result = await login({ usernameOrEmail, password }, rememberMe);
    if (result.success) {
      navigate(from, { replace: true });
    }
  };

  // Generate random particles
  const particles = Array.from({ length: 12 }, (_, i) => ({
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${4 + Math.random() * 8}px`,
    height: `${4 + Math.random() * 8}px`,
    animationDelay: `${Math.random() * 6}s`,
    animationDuration: `${6 + Math.random() * 8}s`,
    opacity: 0.15 + Math.random() * 0.25,
  }));

  return (
    <div className="login-page">
      {/* Ambient background orbs */}
      <div className="login-orb login-orb-1" />
      <div className="login-orb login-orb-2" />
      <div className="login-orb login-orb-3" />

      {/* Floating particles */}
      {particles.map((style, i) => <Particle key={i} style={style} />)}

      <div className="login-wrapper">
        {/* Left branding panel */}
        <div className="login-brand-panel">
          <div className="login-brand-inner">
            <div className="login-brand-logo">
              <BookOpenIcon />
            </div>
            <h1 className="login-brand-title">Library<br />Management<br />System</h1>
            <p className="login-brand-desc">
              Your complete solution for managing books, members, and library operations with ease and efficiency.
            </p>
            <div className="login-brand-stats">
              <div className="login-stat">
                <span className="login-stat-value">∞</span>
                <span className="login-stat-label">Books</span>
              </div>
              <div className="login-stat-divider" />
              <div className="login-stat">
                <span className="login-stat-value">24/7</span>
                <span className="login-stat-label">Access</span>
              </div>
              <div className="login-stat-divider" />
              <div className="login-stat">
                <span className="login-stat-value">100%</span>
                <span className="login-stat-label">Secure</span>
              </div>
            </div>
            <div className="login-brand-tags">
              <span className="login-tag">📚 Books Catalog</span>
              <span className="login-tag">👥 Members</span>
              <span className="login-tag">🔄 Issue &amp; Return</span>
              <span className="login-tag">📊 Dashboard</span>
            </div>
          </div>
        </div>

        {/* Right login form panel */}
        <div className="login-form-panel">
          <div className="login-card">
            {/* Card header */}
            <div className="login-card-header">
              <div className="login-card-logo">
                <BookOpenIcon />
              </div>
              <h2 className="login-card-title">Welcome Back</h2>
              <p className="login-card-subtitle">Sign in to access the library portal</p>
            </div>

            {/* Error Alert */}
            {localError && (
              <div className="login-error-box" role="alert">
                <AlertIcon />
                <span>{localError}</span>
              </div>
            )}

            {/* Login Form */}
            <form id="login-form" className="login-form" onSubmit={handleSubmit} noValidate>
              {/* Username / Email */}
              <div className="login-field">
                <label className="login-label" htmlFor="username-email">Username or Email</label>
                <div className="login-input-wrap">
                  <span className="login-input-icon"><UserIcon /></span>
                  <input
                    id="username-email"
                    ref={firstInputRef}
                    type="text"
                    className="login-input"
                    placeholder="Enter username or email"
                    value={usernameOrEmail}
                    onChange={e => { setUsernameOrEmail(e.target.value); setLocalError(''); clearError(); }}
                    autoComplete="username"
                    required
                  />
                </div>
              </div>

              {/* Password */}
              <div className="login-field">
                <label className="login-label" htmlFor="login-password">Password</label>
                <div className="login-input-wrap">
                  <span className="login-input-icon"><LockIcon /></span>
                  <input
                    id="login-password"
                    type={showPassword ? 'text' : 'password'}
                    className="login-input login-input-padded-right"
                    placeholder="Enter your password"
                    value={password}
                    onChange={e => { setPassword(e.target.value); setLocalError(''); clearError(); }}
                    autoComplete="current-password"
                    required
                  />
                  <button
                    type="button"
                    className="login-eye-btn"
                    onClick={() => setShowPassword(v => !v)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? <EyeOffIcon /> : <EyeIcon />}
                  </button>
                </div>
              </div>

              {/* Remember Me + Forgot Password row */}
              <div className="login-options-row">
                <label className="login-remember-label" htmlFor="remember-me">
                  <input
                    id="remember-me"
                    type="checkbox"
                    className="login-checkbox"
                    checked={rememberMe}
                    onChange={e => setRememberMe(e.target.checked)}
                  />
                  <span className="login-checkbox-custom" />
                  <span>Remember me</span>
                </label>
                <button
                  type="button"
                  id="forgot-password-btn"
                  className="login-forgot-btn"
                  onClick={() => setForgotOpen(true)}
                >
                  Forgot password?
                </button>
              </div>

              {/* Submit Button */}
              <button
                type="submit"
                id="login-submit-btn"
                className="login-btn"
                disabled={loading}
              >
                {loading ? (
                  <><span className="login-spinner" /> Signing in...</>
                ) : (
                  'Sign In'
                )}
              </button>
            </form>

            {/* Footer */}
            <div className="login-card-footer">
              <div className="login-divider">
                <span>Default Credentials</span>
              </div>
              <div className="login-default-creds">
                <div className="login-cred-item">
                  <span className="login-cred-role">Admin</span>
                  <code>admin / admin123</code>
                </div>
                <div className="login-cred-item">
                  <span className="login-cred-role">Librarian</span>
                  <code>librarian / librarian123</code>
                </div>
              </div>
            </div>
          </div>

          {/* Bottom copyright */}
          <p className="login-copyright">
            © {new Date().getFullYear()} Library Management System · Built with Spring Boot &amp; React
          </p>
        </div>
      </div>

      <ForgotPasswordModal isOpen={forgotOpen} onClose={() => setForgotOpen(false)} />
    </div>
  );
}
