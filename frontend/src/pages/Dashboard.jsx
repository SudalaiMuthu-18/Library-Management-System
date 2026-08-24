import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchStats, fetchIssued, saveBook, saveMember } from '../services/api';
import { BookModal } from '../components/books/BookModal';
import { MemberModal } from '../components/members/MemberModal';
import { useToast } from '../context/ToastContext';

export function Dashboard() {
  const navigate = useNavigate();
  const showToast = useToast();

  const [stats, setStats] = useState({
    totalBooks: '—',
    availableBooks: '—',
    totalMembers: '—',
    issuedBooks: '—',
  });
  const [issuedList, setIssuedList] = useState([]);
  const [loadingIssued, setLoadingIssued] = useState(true);

  // Modals
  const [bookModalOpen, setBookModalOpen] = useState(false);
  const [memberModalOpen, setMemberModalOpen] = useState(false);

  const loadData = async () => {
    try {
      const statsData = await fetchStats();
      setStats(statsData);
    } catch (e) {
      console.error(e);
    }

    try {
      setLoadingIssued(true);
      const issuedData = await fetchIssued();
      setIssuedList(issuedData || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoadingIssued(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSaveBook = async (bookData) => {
    await saveBook(bookData);
    showToast('Book added successfully!');
    loadData();
  };

  const handleSaveMember = async (memberData) => {
    await saveMember(memberData);
    showToast('Member registered successfully!');
    loadData();
  };

  return (
    <section className="section">
      <div className="section-header">
        <h1>Dashboard</h1>
        <p className="section-sub">Welcome back! Here's your library at a glance.</p>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card stat-blue">
          <div className="stat-icon">📚</div>
          <div className="stat-info">
            <div className="stat-value">{stats.totalBooks}</div>
            <div className="stat-label">Total Books</div>
          </div>
          <div className="stat-glow"></div>
        </div>

        <div className="stat-card stat-green">
          <div className="stat-icon">✅</div>
          <div className="stat-info">
            <div className="stat-value">{stats.availableBooks}</div>
            <div className="stat-label">Available</div>
          </div>
          <div className="stat-glow"></div>
        </div>

        <div className="stat-card stat-purple">
          <div className="stat-icon">👥</div>
          <div className="stat-info">
            <div className="stat-value">{stats.totalMembers}</div>
            <div className="stat-label">Members</div>
          </div>
          <div className="stat-glow"></div>
        </div>

        <div className="stat-card stat-orange">
          <div className="stat-icon">📤</div>
          <div className="stat-info">
            <div className="stat-value">{stats.issuedBooks}</div>
            <div className="stat-label">Books Issued</div>
          </div>
          <div className="stat-glow"></div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="quick-actions">
        <h2 className="section-h2">Quick Actions</h2>
        <div className="qa-grid">
          <button
            className="qa-btn"
            onClick={() => setBookModalOpen(true)}
          >
            <span>➕</span> Add Book
          </button>
          <button
            className="qa-btn"
            onClick={() => setMemberModalOpen(true)}
          >
            <span>➕</span> Add Member
          </button>
          <button
            className="qa-btn"
            onClick={() => navigate('/issue')}
          >
            <span>📤</span> Issue Book
          </button>
          <button
            className="qa-btn"
            onClick={() => navigate('/issue')}
          >
            <span>📥</span> Return Book
          </button>
        </div>
      </div>

      {/* Currently Issued Preview */}
      <div className="dashboard-issued">
        <h2 className="section-h2">Currently Issued Books</h2>
        <div className="table-wrap">
          {loadingIssued ? (
            <div className="loading-row">Loading…</div>
          ) : issuedList.length === 0 ? (
            <div className="loading-row">No books currently issued. Great job!</div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Issue ID</th>
                  <th>Book ID</th>
                  <th>Book Title</th>
                  <th>Member Name</th>
                  <th>Due Date</th>
                </tr>
              </thead>
              <tbody>
                {issuedList.slice(0, 5).map((row) => (
                  <tr key={row.issueId}>
                    <td>
                      <b>#{row.issueId}</b>
                    </td>
                    <td>{row.bookId}</td>
                    <td>{row.bookTitle}</td>
                    <td>{row.memberName}</td>
                    <td>
                      <span className="badge badge-danger">{row.dueDate}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Modals */}
      <BookModal
        isOpen={bookModalOpen}
        book={null}
        onSave={handleSaveBook}
        onClose={() => setBookModalOpen(false)}
      />

      <MemberModal
        isOpen={memberModalOpen}
        member={null}
        onSave={handleSaveMember}
        onClose={() => setMemberModalOpen(false)}
      />
    </section>
  );
}
