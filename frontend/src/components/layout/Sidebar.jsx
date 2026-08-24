import React, { useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { fetchStats } from '../../services/api';

export function Sidebar({ isOpen, onClose }) {
  const [serverStatus, setServerStatus] = useState('connecting');

  useEffect(() => {
    let isMounted = true;
    const checkStatus = async () => {
      try {
        await fetchStats();
        if (isMounted) setServerStatus('online');
      } catch (err) {
        if (isMounted) setServerStatus('offline');
      }
    };

    checkStatus();
    const interval = setInterval(checkStatus, 5000);
    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, []);

  return (
    <aside className={`sidebar ${isOpen ? 'active' : ''}`}>
      <div className="sidebar-logo">
        <span className="logo-icon">📚</span>
        <span className="logo-text">LibraryMS</span>
      </div>

      <nav className="sidebar-nav">
        <NavLink
          to="/"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          onClick={onClose}
        >
          <span className="nav-icon">🏠</span>
          <span className="nav-label">Dashboard</span>
        </NavLink>

        <NavLink
          to="/books"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          onClick={onClose}
        >
          <span className="nav-icon">📖</span>
          <span className="nav-label">Books</span>
        </NavLink>

        <NavLink
          to="/members"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          onClick={onClose}
        >
          <span className="nav-icon">👥</span>
          <span className="nav-label">Members</span>
        </NavLink>

        <NavLink
          to="/issue"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
          onClick={onClose}
        >
          <span className="nav-icon">🔄</span>
          <span className="nav-label">Issue / Return</span>
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="server-status">
          <span
            className={`status-dot ${
              serverStatus === 'online'
                ? 'online'
                : serverStatus === 'offline'
                ? 'offline'
                : ''
            }`}
          ></span>
          <span>
            {serverStatus === 'online'
              ? 'Server: Connected'
              : serverStatus === 'offline'
              ? 'Server: Disconnected'
              : 'Connecting…'}
          </span>
        </div>
      </div>
    </aside>
  );
}
