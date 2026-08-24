import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const titles = {
  '/': 'Dashboard Overview',
  '/books': 'Books Catalog',
  '/members': 'Library Members',
  '/issue': 'Book Transactions',
};

const LogOutIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
    <polyline points="16 17 21 12 16 7"/>
    <line x1="21" y1="12" x2="9" y2="12"/>
  </svg>
);

export function Topbar({ onToggleSidebar }) {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [timeStr, setTimeStr] = useState('');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimeStr(
        now.toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        })
      );
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const pageTitle = titles[location.pathname] || 'Library Management System';

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // Build initials avatar from fullName or username
  const getInitials = () => {
    if (user?.fullName) {
      const parts = user.fullName.trim().split(' ');
      return parts.length >= 2
        ? `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase()
        : parts[0].substring(0, 2).toUpperCase();
    }
    return user?.username?.substring(0, 2).toUpperCase() || 'LB';
  };

  const getRoleLabel = () => {
    if (!user?.role) return 'Staff';
    const r = user.role.replace('ROLE_', '');
    return r.charAt(0) + r.slice(1).toLowerCase();
  };

  return (
    <header className="topbar">
      <button className="menu-toggle" onClick={onToggleSidebar} aria-label="Toggle sidebar">
        ☰
      </button>
      <div className="topbar-title">{pageTitle}</div>
      <div className="topbar-right">
        <span className="topbar-time">{timeStr}</span>

        {/* User profile pill */}
        {user && (
          <div className="topbar-user">
            <div className="topbar-avatar">{getInitials()}</div>
            <div className="topbar-user-info">
              <span className="topbar-username">{user.fullName || user.username}</span>
              <span className="topbar-role">{getRoleLabel()}</span>
            </div>
            <button
              id="logout-btn"
              className="topbar-logout-btn"
              onClick={handleLogout}
              title="Sign out"
              aria-label="Sign out"
            >
              <LogOutIcon />
              <span>Logout</span>
            </button>
          </div>
        )}
      </div>
    </header>
  );
}
