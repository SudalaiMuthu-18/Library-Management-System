import React, { useState, useEffect, useCallback } from 'react';
import { fetchIssued, issueBookApi, returnBookApi } from '../services/api';
import { useToast } from '../context/ToastContext';

export function IssueReturn() {
  const showToast = useToast();

  // Form states
  const [issueBookId, setIssueBookId] = useState('');
  const [issueMemberId, setIssueMemberId] = useState('');
  const [returnIssueId, setReturnIssueId] = useState('');

  const [issuing, setIssuing] = useState(false);
  const [returning, setReturning] = useState(false);

  // Issued list state
  const [issuedList, setIssuedList] = useState([]);
  const [loadingIssued, setLoadingIssued] = useState(true);

  const loadIssued = useCallback(async () => {
    try {
      setLoadingIssued(true);
      const data = await fetchIssued();
      setIssuedList(data || []);
    } catch (e) {
      showToast('Could not load active borrow transaction records.', 'error');
    } finally {
      setLoadingIssued(false);
    }
  }, [showToast]);

  useEffect(() => {
    loadIssued();
  }, [loadIssued]);

  const handleIssue = async (e) => {
    e.preventDefault();
    const bId = parseInt(issueBookId);
    const mId = parseInt(issueMemberId);

    if (!bId || !mId) {
      showToast('Please enter valid Book ID and Member ID.', 'error');
      return;
    }

    try {
      setIssuing(true);
      await issueBookApi(bId, mId);
      showToast('Book issued successfully!');
      setIssueBookId('');
      setIssueMemberId('');
      loadIssued();
    } catch (err) {
      showToast(err.message || 'Failed to issue book.', 'error');
    } finally {
      setIssuing(false);
    }
  };

  const handleReturn = async (e) => {
    e.preventDefault();
    const iId = parseInt(returnIssueId);

    if (!iId) {
      showToast('Please enter a valid Issue ID.', 'error');
      return;
    }

    try {
      setReturning(true);
      await returnBookApi(iId);
      showToast('Book returned successfully!');
      setReturnIssueId('');
      loadIssued();
    } catch (err) {
      showToast(err.message || 'Failed to return book.', 'error');
    } finally {
      setReturning(false);
    }
  };

  return (
    <section className="section">
      <div className="section-header">
        <h1>Issue / Return</h1>
      </div>

      <div className="issue-grid">
        {/* Issue Card */}
        <div className="glass-card issue-card">
          <div className="issue-card-header">
            <span className="issue-card-icon">📤</span>
            <h2>Issue a Book</h2>
          </div>
          <form onSubmit={handleIssue}>
            <div className="form-group">
              <label htmlFor="issueBookId">Book ID</label>
              <input
                type="number"
                id="issueBookId"
                placeholder="Enter Book ID"
                min="1"
                value={issueBookId}
                onChange={(e) => setIssueBookId(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="issueMemberId">Member ID</label>
              <input
                type="number"
                id="issueMemberId"
                placeholder="Enter Member ID"
                min="1"
                value={issueMemberId}
                onChange={(e) => setIssueMemberId(e.target.value)}
              />
            </div>
            <button
              type="submit"
              className="btn btn-primary btn-full"
              disabled={issuing}
            >
              {issuing ? 'Issuing…' : 'Issue Book'}
            </button>
          </form>
        </div>

        {/* Return Card */}
        <div className="glass-card issue-card">
          <div className="issue-card-header">
            <span className="issue-card-icon">📥</span>
            <h2>Return a Book</h2>
          </div>
          <form onSubmit={handleReturn}>
            <div className="form-group">
              <label htmlFor="returnIssueId">Issue ID</label>
              <input
                type="number"
                id="returnIssueId"
                placeholder="Enter Issue ID"
                min="1"
                value={returnIssueId}
                onChange={(e) => setReturnIssueId(e.target.value)}
              />
            </div>
            <p className="hint-text">Find the Issue ID in the table below.</p>
            <button
              type="submit"
              className="btn btn-success btn-full"
              disabled={returning}
            >
              {returning ? 'Returning…' : 'Return Book'}
            </button>
          </form>
        </div>
      </div>

      {/* Issued Books Table */}
      <div className="issued-table-wrap">
        <div className="issued-table-header">
          <h2 className="section-h2">Currently Issued Books</h2>
          <button className="btn btn-sm" onClick={loadIssued}>
            🔄 Refresh
          </button>
        </div>
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Issue ID</th>
                <th>Book</th>
                <th>Member</th>
                <th>Issue Date</th>
                <th>Due Date</th>
              </tr>
            </thead>
            <tbody>
              {loadingIssued ? (
                <tr>
                  <td colSpan="5" className="loading-row">
                    Loading transactions…
                  </td>
                </tr>
              ) : issuedList.length === 0 ? (
                <tr>
                  <td colSpan="5" className="loading-row">
                    No books are currently issued. All caught up!
                  </td>
                </tr>
              ) : (
                issuedList.map((row) => (
                  <tr key={row.issueId}>
                    <td>
                      <b>#{row.issueId}</b>
                    </td>
                    <td>
                      <span
                        className="badge badge-success"
                        style={{ fontSize: '0.7rem', marginRight: '0.5rem' }}
                      >
                        ID: {row.bookId}
                      </span>
                      {row.bookTitle}
                    </td>
                    <td>
                      <span
                        className="badge badge-success"
                        style={{ fontSize: '0.7rem', marginRight: '0.5rem' }}
                      >
                        ID: {row.memberId}
                      </span>
                      {row.memberName}
                    </td>
                    <td>{row.issueDate}</td>
                    <td>
                      <span className="badge badge-danger">{row.dueDate}</span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
