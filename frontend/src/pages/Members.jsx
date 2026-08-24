import React, { useState, useEffect, useCallback } from 'react';
import { fetchMembers, saveMember, deleteMember } from '../services/api';
import { MemberModal } from '../components/members/MemberModal';
import { ConfirmModal } from '../components/common/ConfirmModal';
import { useToast } from '../context/ToastContext';

export function Members() {
  const showToast = useToast();
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Modal state
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState(null);

  // Confirm delete dialog state
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState(null);

  const loadMembers = useCallback(async (query = '') => {
    try {
      setLoading(true);
      const data = await fetchMembers(query);
      setMembers(data || []);
    } catch (e) {
      showToast(e.message || 'Error fetching members.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => {
    const timer = setTimeout(() => {
      loadMembers(searchQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery, loadMembers]);

  const handleOpenAddModal = () => {
    setSelectedMember(null);
    setModalOpen(true);
  };

  const handleOpenEditModal = (mem) => {
    setSelectedMember(mem);
    setModalOpen(true);
  };

  const handleSave = async (formData) => {
    const id = selectedMember ? selectedMember.memberId : null;
    await saveMember(formData, id);
    showToast(id ? 'Member updated successfully!' : 'Member registered successfully!');
    loadMembers(searchQuery);
  };

  const handlePromptDelete = (id) => {
    setDeleteTargetId(id);
    setConfirmOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTargetId) return;
    try {
      await deleteMember(deleteTargetId);
      showToast('Member deleted successfully.');
      loadMembers(searchQuery);
    } catch (err) {
      showToast(err.message || 'Failed to delete member.', 'error');
    } finally {
      setConfirmOpen(false);
      setDeleteTargetId(null);
    }
  };

  return (
    <section className="section">
      <div className="section-header">
        <h1>Members</h1>
        <button className="btn btn-primary" onClick={handleOpenAddModal}>
          ➕ Add Member
        </button>
      </div>

      <div className="toolbar">
        <div className="search-box">
          <span className="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Search by name or email…"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="table-wrap">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="5" className="loading-row">
                  Loading members…
                </td>
              </tr>
            ) : members.length === 0 ? (
              <tr>
                <td colSpan="5" className="loading-row">
                  No members found. Add some!
                </td>
              </tr>
            ) : (
              members.map((mem) => (
                <tr key={mem.memberId}>
                  <td>
                    <b>#{mem.memberId}</b>
                  </td>
                  <td>{mem.name}</td>
                  <td>{mem.email || '—'}</td>
                  <td>{mem.phone || '—'}</td>
                  <td>
                    <button
                      className="btn-action edit-action"
                      onClick={() => handleOpenEditModal(mem)}
                      title="Edit Member"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn-action delete-action"
                      onClick={() => handlePromptDelete(mem.memberId)}
                      title="Delete Member"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <MemberModal
        isOpen={modalOpen}
        member={selectedMember}
        onSave={handleSave}
        onClose={() => setModalOpen(false)}
      />

      <ConfirmModal
        isOpen={confirmOpen}
        title="Delete Member"
        message={`Are you sure you want to delete member #${deleteTargetId}?`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setConfirmOpen(false)}
      />
    </section>
  );
}
