import React, { useState, useEffect } from 'react';
import { useToast } from '../../context/ToastContext';

const GENRES = [
  'General',
  'Fiction',
  'Non-Fiction',
  'Classic',
  'Fantasy',
  'Dystopian',
  'Science',
  'History',
  'Biography',
  'Mystery',
  'Romance',
  'Technology',
];

export function BookModal({ isOpen, book, onSave, onClose }) {
  const showToast = useToast();
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    genre: 'General',
    quantity: 1,
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (book) {
      setFormData({
        title: book.title || '',
        author: book.author || '',
        genre: book.genre || 'General',
        quantity: book.quantity || 1,
      });
    } else {
      setFormData({
        title: '',
        author: '',
        genre: 'General',
        quantity: 1,
      });
    }
  }, [book, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title.trim() || !formData.author.trim()) {
      showToast('Title and Author are required fields.', 'error');
      return;
    }

    try {
      setLoading(true);
      await onSave({
        ...formData,
        quantity: parseInt(formData.quantity) || 1,
      });
      onClose();
    } catch (err) {
      showToast(err.message || 'Failed to save book.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <div className="modal-header">
          <h2>{book ? 'Edit Book Details' : 'Add New Book'}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="bookTitle">
                  Title <span className="req">*</span>
                </label>
                <input
                  type="text"
                  id="bookTitle"
                  placeholder="Book title"
                  value={formData.title}
                  onChange={(e) =>
                    setFormData({ ...formData, title: e.target.value })
                  }
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label htmlFor="bookAuthor">
                  Author <span className="req">*</span>
                </label>
                <input
                  type="text"
                  id="bookAuthor"
                  placeholder="Author name"
                  value={formData.author}
                  onChange={(e) =>
                    setFormData({ ...formData, author: e.target.value })
                  }
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="bookGenre">Genre</label>
                <select
                  id="bookGenre"
                  value={formData.genre}
                  onChange={(e) =>
                    setFormData({ ...formData, genre: e.target.value })
                  }
                >
                  {GENRES.map((g) => (
                    <option key={g} value={g}>
                      {g}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="bookQty">Quantity</label>
                <input
                  type="number"
                  id="bookQty"
                  placeholder="1"
                  min="1"
                  value={formData.quantity}
                  onChange={(e) =>
                    setFormData({ ...formData, quantity: e.target.value })
                  }
                />
              </div>
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
              {loading ? 'Saving…' : 'Save Book'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
