import React, { useState, useEffect } from 'react';
import { useToast } from '../../context/ToastContext';

export function MemberModal({ isOpen, member, onSave, onClose }) {
  const showToast = useToast();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (member) {
      setFormData({
        name: member.name || '',
        email: member.email || '',
        phone: member.phone || '',
      });
    } else {
      setFormData({
        name: '',
        email: '',
        phone: '',
      });
    }
  }, [member, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      showToast('Member Name is required.', 'error');
      return;
    }

    try {
      setLoading(true);
      await onSave(formData);
      onClose();
    } catch (err) {
      showToast(err.message || 'Failed to save member.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h2>{member ? 'Edit Member Information' : 'Register New Member'}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="memberName">
                  Name <span className="req">*</span>
                </label>
                <input
                  type="text"
                  id="memberName"
                  placeholder="Full name"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label htmlFor="memberEmail">Email</label>
                <input
                  type="email"
                  id="memberEmail"
                  placeholder="email@example.com"
                  value={formData.email}
                  onChange={(e) =>
                    setFormData({ ...formData, email: e.target.value })
                  }
                />
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="memberPhone">Phone</label>
              <input
                type="tel"
                id="memberPhone"
                placeholder="Phone number"
                value={formData.phone}
                onChange={(e) =>
                  setFormData({ ...formData, phone: e.target.value })
                }
              />
            </div>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-ghost"
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : 'Save Member'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
