import React, { useState, useEffect, useCallback } from 'react';
import { fetchBooks, saveBook, deleteBook } from '../services/api';
import { BookModal } from '../components/books/BookModal';
import { ConfirmModal } from '../components/common/ConfirmModal';
import { useToast } from '../context/ToastContext';

export function Books() {
  const showToast = useToast();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Modal states
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);

  // Confirm dialog state
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState(null);

  const loadBooks = useCallback(async (query = '') => {
    try {
      setLoading(true);
      const data = await fetchBooks(query);
      setBooks(data || []);
    } catch (e) {
      showToast(e.message || 'Error fetching books.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showToast]);

  useEffect(() => {
    const timer = setTimeout(() => {
      loadBooks(searchQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery, loadBooks]);

  const handleOpenAddModal = () => {
    setSelectedBook(null);
    setModalOpen(true);
  };

  const handleOpenEditModal = (book) => {
    setSelectedBook(book);
    setModalOpen(true);
  };

  const handleSave = async (formData) => {
    const id = selectedBook ? selectedBook.bookId : null;
    await saveBook(formData, id);
    showToast(id ? 'Book updated successfully!' : 'Book added successfully!');
    loadBooks(searchQuery);
  };

  const handlePromptDelete = (id) => {
    setDeleteTargetId(id);
    setConfirmOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTargetId) return;
    try {
      await deleteBook(deleteTargetId);
      showToast('Book deleted successfully.');
      loadBooks(searchQuery);
    } catch (err) {
      showToast(err.message || 'Failed to delete book.', 'error');
    } finally {
      setConfirmOpen(false);
      setDeleteTargetId(null);
    }
  };

  return (
    <section className="section">
      <div className="section-header">
        <h1>Books</h1>
        <button className="btn btn-primary" onClick={handleOpenAddModal}>
          ➕ Add Book
        </button>
      </div>

      <div className="toolbar">
        <div className="search-box">
          <span className="search-icon">🔍</span>
          <input
            type="text"
            placeholder="Search by title or author…"
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
              <th>Title</th>
              <th>Author</th>
              <th>Genre</th>
              <th>Qty</th>
              <th>Available</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="8" className="loading-row">
                  Loading books…
                </td>
              </tr>
            ) : books.length === 0 ? (
              <tr>
                <td colSpan="8" className="loading-row">
                  No books found. Add some!
                </td>
              </tr>
            ) : (
              books.map((book) => {
                const isAvailable = book.available > 0;
                return (
                  <tr key={book.bookId}>
                    <td>
                      <b>#{book.bookId}</b>
                    </td>
                    <td>{book.title}</td>
                    <td>{book.author}</td>
                    <td>{book.genre}</td>
                    <td>{book.quantity}</td>
                    <td>{book.available}</td>
                    <td>
                      {isAvailable ? (
                        <span className="badge badge-success">Available</span>
                      ) : (
                        <span className="badge badge-danger">Out of Stock</span>
                      )}
                    </td>
                    <td>
                      <button
                        className="btn-action edit-action"
                        onClick={() => handleOpenEditModal(book)}
                        title="Edit Book"
                      >
                        ✏️
                      </button>
                      <button
                        className="btn-action delete-action"
                        onClick={() => handlePromptDelete(book.bookId)}
                        title="Delete Book"
                      >
                        🗑️
                      </button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      <BookModal
        isOpen={modalOpen}
        book={selectedBook}
        onSave={handleSave}
        onClose={() => setModalOpen(false)}
      />

      <ConfirmModal
        isOpen={confirmOpen}
        title="Delete Book"
        message={`Are you sure you want to delete book #${deleteTargetId}?`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setConfirmOpen(false)}
      />
    </section>
  );
}
