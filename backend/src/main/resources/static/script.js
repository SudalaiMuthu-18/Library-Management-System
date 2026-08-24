// ═══════════════════════════════════════════════════════
//   LIBRARY MANAGEMENT SYSTEM — Frontend Controller (API Integration)
// ═══════════════════════════════════════════════════════

const API_BASE = window.location.protocol.startsWith("file") ? "http://localhost:8080/api" : "/api";
let currentDeleteCallback = null;
let searchBookTimeout = null;
let searchMemberTimeout = null;

// ─── INIT & NAVIGATION ─────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
    // Show Time in header
    updateTime();
    setInterval(updateTime, 1000);

    // Initial section & data fetch
    showSection("dashboard");
    checkServerStatus();
    setInterval(checkServerStatus, 5000); // Check server every 5 seconds
});

function updateTime() {
    const now = new Date();
    document.getElementById("topbarTime").textContent = now.toLocaleTimeString
    ([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
} 

function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll(".section").forEach(sec => sec.classList.add("hidden"));
    // Show target section
    document.getElementById(`section-${sectionId}`).classList.remove("hidden");

    // Update active nav link
    document.querySelectorAll(".nav-item").forEach(item => item.classList.remove("active"));
    const activeNav = document.getElementById(`nav-${sectionId}`);
    if (activeNav) activeNav.classList.add("active");

    // Update topbar title
    const titles = {
        dashboard: "Dashboard Overview",
        books: "Books Catalog",
        members: "Library Members",
        issue: "Book Transactions"
    };
    document.getElementById("topbarTitle").textContent = titles[sectionId] || "Library System";

    // Close mobile sidebar if open
    document.getElementById("sidebar").classList.remove("active");

    // Load appropriate data
    if (sectionId === "dashboard") {
        loadDashboardStats(); 
        loadDashboardIssued();
    } else if (sectionId === "books") {
        loadBooks();
    } else if (sectionId === "members") {
        loadMembers();
    } else if (sectionId === "issue") {
        loadIssued();
    }
}

function toggleSidebar() {
    document.getElementById("sidebar").classList.toggle("active");
}

// ─── SERVER STATUS ──────────────────────────────────────────────────────

async function checkServerStatus() {
    const dot = document.getElementById("statusDot");
    const text = document.getElementById("statusText");
    try {
        const res = await fetch(`${API_BASE}/stats`); 
        if (res.ok) {
            dot.className = "status-dot online";
            text.textContent = "Server: Connected"; 
        } else {
            throw new Error();
        }
    } catch (e) {
        dot.className = "status-dot offline";
        text.textContent = "Server: Disconnected";
    }
}

// ─── TOAST NOTIFICATIONS ─────────────────────────────────────────────────

function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");
    const toast = document.createElement("div");
    toast.className = `toast ${type}-toast`;

    let icon = "💡";
    if (type === "success") icon = "✅";
    if (type === "error") icon = "❌";
    if (type === "info") icon = "ℹ️";

    toast.innerHTML = `
        <span class="toast-icon">${icon}</span>
        <span class="toast-message">${message}</span>
        <button class="toast-close" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);

    // Auto-remove after 4 seconds
    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(50px)";
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// ─── CONFIRMATION DIALOG ────────────────────────────────────────────────

function showConfirm(message, onConfirm) {
    document.getElementById("confirmMessage").textContent = message;
    const modal = document.getElementById("confirmModal");
    modal.classList.remove("hidden");
    
    currentDeleteCallback = onConfirm;
    
    // Wire button
    document.getElementById("confirmOkBtn").onclick = () => {
        if (currentDeleteCallback) currentDeleteCallback();
        cancelConfirm();
    };
}

function cancelConfirm() {
    document.getElementById("confirmModal").classList.add("hidden");
    currentDeleteCallback = null;
}

// ═══════════════════════════════════════════════════════
//   DASHBOARD FUNCTIONS
// ═══════════════════════════════════════════════════════

async function loadDashboardStats() {
    try {
        const res = await fetch(`${API_BASE}/stats`);
        if (!res.ok) throw new Error("Could not load statistics.");
        const data = await res.json();
        
        document.getElementById("statTotalBooksVal").textContent = data.totalBooks;
        document.getElementById("statAvailableVal").textContent = data.availableBooks;
        document.getElementById("statMembersVal").textContent = data.totalMembers;
        document.getElementById("statIssuedVal").textContent = data.issuedBooks;
    } catch (e) {
        console.error(e);
    }
}

async function loadDashboardIssued() {
    const wrap = document.getElementById("dashIssuedTable");
    try {
        const res = await fetch(`${API_BASE}/issued`);
        if (!res.ok) throw new Error();
        const data = await res.json();
        
        if (data.length === 0) {
            wrap.innerHTML = `<div class="loading-row">No books currently issued. Great job!</div>`;
            return;
        }

        let html = `
            <table class="data-table">
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
        `;
        
        data.slice(0, 5).forEach(row => {
            html += `
                <tr>
                    <td><b>#${row.issueId}</b></td>
                    <td>${row.bookId}</td>
                    <td>${row.bookTitle}</td>
                    <td>${row.memberName}</td>
                    <td><span class="badge badge-danger">${row.dueDate}</span></td>
                </tr>
            `;
        });
        
        html += `</tbody></table>`;
        wrap.innerHTML = html;
    } catch (e) {
        wrap.innerHTML = `<div class="loading-row">Could not load issued book list.</div>`;
    }
}

// ═══════════════════════════════════════════════════════
//   BOOKS MODULE
// ═══════════════════════════════════════════════════════

async function loadBooks(query = "") {
    const body = document.getElementById("booksBody");
    body.innerHTML = `<tr><td colspan="8" class="loading-row">Loading books…</td></tr>`;
                                                   
  try {           
        const url = query ? `${API_BASE}/books?search=${encodeURIComponent(query)}` : `${API_BASE}/books`;
        const res = await fetch(url);
        if (!res.ok) throw new Error("Failed to fetch books.");
        const data = await res.json();

        if (data.length === 0) {
            body.innerHTML = `<tr><td colspan="8" class="loading-row">No books found. Add some!</td></tr>`;
            return;
        }

        let html = "";
        data.forEach(book => {
            const statusBadge = book.available > 0 
                ? `<span class="badge badge-success">Available</span>`
                : `<span class="badge badge-danger">Out of Stock</span>`;
            
            html += `
                <tr>
                    <td><b>#${book.bookId}</b></td>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.genre}</td>
                    <td>${book.quantity}</td>
                    <td>${book.available}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn-action edit-action" onclick="openBookModal(${JSON.stringify(book).replace(/"/g, '&quot;')})">✏️</button>
                        <button class="btn-action delete-action" onclick="deleteBook(${book.bookId})">🗑️</button>
                    </td>
                </tr>
            `;
        });
        body.innerHTML = html;
    } catch (e) {
        body.innerHTML = `<tr><td colspan="8" class="loading-row">Error fetching books. Make sure server is running.</td></tr>`;
        showToast("Error loading books.", "error");
    }
}

function debounceBookSearch() {
    clearTimeout(searchBookTimeout);
    searchBookTimeout = setTimeout(() => {
        const query = document.getElementById("bookSearch").value;
        loadBooks(query); 
    }, 300);                      
}     

// ─── BOOK MODAL & CRUD ──────────────────────────────────────────────────

function openBookModal(book = null) {
    const overlay = document.getElementById("bookModal");
    const title = document.getElementById("bookModalTitle");
    
    // Clear inputs
    document.getElementById("editBookId").value = "";
    document.getElementById("bookTitle").value = "";
    document.getElementById("bookAuthor").value = "";
    document.getElementById("bookGenre").value = "General";
    document.getElementById("bookQty").value = "1"; 

    if (book) {
        title.textContent = "Edit Book Details";
        document.getElementById("editBookId").value = book.bookId;
        document.getElementById("bookTitle").value = book.title;
        document.getElementById("bookAuthor").value = book.author;
        document.getElementById("bookGenre").value = book.genre;
        document.getElementById("bookQty").value = book.quantity;
    } else {
        title.textContent = "Add New Book";
    }

    overlay.classList.remove("hidden");
    document.getElementById("bookTitle").focus();
}

function closeBookModal() {
    document.getElementById("bookModal").classList.add("hidden");
}

async function saveBook() {
    const id = document.getElementById("editBookId").value;
    const title = document.getElementById("bookTitle").value.trim();
    const author = document.getElementById("bookAuthor").value.trim();
    const genre = document.getElementById("bookGenre").value;
    const quantity = parseInt(document.getElementById("bookQty").value) || 1;

    if (!title || !author) {
        showToast("Title and Author are required fields.", "error");
        return; 
    }

    const payload = { title, author, genre, quantity };

    try {
        let res;
        if (id) {
            // Edit
            payload.available = quantity; // Basic reset / scale logic
            res = await fetch(`${API_BASE}/books/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        } else {
            // New
            res = await fetch(`${API_BASE}/books`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        }

        const data = await res.json();
        if (res.ok) {
            showToast(data.message || "Book saved successfully!");
            closeBookModal();
            loadBooks();
        } else {
            showToast(data.error || "Failed to save book.", "error");
        }
    } catch (e) {
        showToast("Network error. Could not connect to API server.", "error");
    }
}

function deleteBook(id) {
    showConfirm("Are you sure you want to delete book #" + id + "?", async () => {
        try {
            const res = await fetch(`${API_BASE}/books/${id}`, { method: "DELETE" });
            const data = await res.json();
            if (res.ok) {
                showToast("Book deleted successfully.");
                loadBooks();
            } else {
                showToast(data.error || "Could not delete book.", "error");
            }
        } catch (e) {
            showToast("Server error deleting book.", "error");
        }
    });
}

// ═══════════════════════════════════════════════════════
//   MEMBERS MODULE
// ═══════════════════════════════════════════════════════

async function loadMembers(query = "") {
    const body = document.getElementById("membersBody");
    body.innerHTML = `<tr><td colspan="5" class="loading-row">Loading members…</td></tr>`;

    try {
        const url = query ? `${API_BASE}/members?search=${encodeURIComponent(query)}` : `${API_BASE}/members`;
        const res = await fetch(url);
        if (!res.ok) throw new Error();
        const data = await res.json();

        if (data.length === 0) {
            body.innerHTML = `<tr><td colspan="5" class="loading-row">No members found. Add some!</td></tr>`;
            return;
        }

        let html = "";
        data.forEach(mem => {
            html += `
                <tr>
                    <td><b>#${mem.memberId}</b></td>
                    <td>${mem.name}</td>
                    <td>${mem.email || "—"}</td>
                    <td>${mem.phone || "—"}</td>
                    <td>
                        <button class="btn-action edit-action" onclick="openMemberModal(${JSON.stringify(mem).replace(/"/g, '&quot;')})">✏️</button>
                        <button class="btn-action delete-action" onclick="deleteMember(${mem.memberId})">🗑️</button>
                    </td>
                </tr>
            `;
        });
        body.innerHTML = html;
    } catch (e) {
        body.innerHTML = `<tr><td colspan="5" class="loading-row">Error fetching members database.</td></tr>`;
    }
}

function debounceMemberSearch() {
    clearTimeout(searchMemberTimeout);
    searchMemberTimeout = setTimeout(() => {
        const query = document.getElementById("memberSearch").value;
        loadMembers(query);
    }, 300);
}

// ─── MEMBER MODAL & CRUD ────────────────────────────────────────────────

function openMemberModal(mem = null) {
    const overlay = document.getElementById("memberModal");
    const title = document.getElementById("memberModalTitle");

    // Clear inputs
    document.getElementById("editMemberId").value = "";
    document.getElementById("memberName").value = "";
    document.getElementById("memberEmail").value = "";
    document.getElementById("memberPhone").value = "";

    if (mem) {
        title.textContent = "Edit Member Information";
        document.getElementById("editMemberId").value = mem.memberId;
        document.getElementById("memberName").value = mem.name;
        document.getElementById("memberEmail").value = mem.email || "";
        document.getElementById("memberPhone").value = mem.phone || "";
    } else {
        title.textContent = "Register New Member";
    }

    overlay.classList.remove("hidden");
    document.getElementById("memberName").focus();
}

function closeMemberModal() {
    document.getElementById("memberModal").classList.add("hidden");
}

async function saveMember() {   
    const id = document.getElementById("editMemberId").value;
    const name = document.getElementById("memberName").value.trim();
    const email = document.getElementById("memberEmail").value.trim();
    const phone = document.getElementById("memberPhone").value.trim();

    if (!name) {
        showToast("Member Name is required.", "error");
        return;
    }

    const payload = { name, email, phone };

    try {
        let res;
        if (id) {
            res = await fetch(`${API_BASE}/members/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        } else {
            res = await fetch(`${API_BASE}/members`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        }

        const data = await res.json();
        if (res.ok) {
            showToast(data.message || "Member details saved successfully!");
            closeMemberModal();
            loadMembers();
        } else {
            showToast(data.error || "Could not complete operation.", "error");
        }
    } catch (e) {
        showToast("Error updating database.", "error");
    }
}

function deleteMember(id) {
    showConfirm("Are you sure you want to delete member #" + id + "?", async () => {
        try {
            const res = await fetch(`${API_BASE}/members/${id}`, { method: "DELETE" });
            const data = await res.json();
            if (res.ok) {
                showToast("Member deleted successfully.");
                loadMembers();
            } else {
                showToast(data.error || "Could not delete member.", "error");
            }
        } catch (e) {
            showToast("Server error deleting member.", "error");
        }
    });
}

// ═══════════════════════════════════════════════════════
//   ISSUE / RETURN MODULE
// ═══════════════════════════════════════════════════════

async function loadIssued() {
    const body = document.getElementById("issuedBody");
    body.innerHTML = `<tr><td colspan="5" class="loading-row">Loading transactions…</td></tr>`;

    try {
        const res = await fetch(`${API_BASE}/issued`);
        if (!res.ok) throw new Error();
        const data = await res.json();

        if (data.length === 0) {
            body.innerHTML = `<tr><td colspan="5" class="loading-row">No books are currently issued. All caught up!</td></tr>`;
            return;
        }

        let html = "";
        data.forEach(row => {
            html += `
                <tr>
                    <td><b>#${row.issueId}</b></td>
                    <td>
                        <span class="badge badge-success" style="font-size: 0.7rem; margin-right: 0.5rem;">ID: ${row.bookId}</span>
                        ${row.bookTitle}
                    </td>
                    <td>
                        <span class="badge badge-success" style="font-size: 0.7rem; margin-right: 0.5rem;">ID: ${row.memberId}</span>
                        ${row.memberName}
                    </td>
                    <td>${row.issueDate}</td>
                    <td><span class="badge badge-danger">${row.dueDate}</span></td>
                </tr>
            `;
        });
        body.innerHTML = html;
    } catch (e) {
        body.innerHTML = `<tr><td colspan="5" class="loading-row">Could not load active borrow transaction records.</td></tr>`;
    }
}

async function issueBook() {
    const bookId = parseInt(document.getElementById("issueBookId").value);
    const memberId = parseInt(document.getElementById("issueMemberId").value);

    if (!bookId || !memberId) {
        showToast("Please enter valid Book ID and Member ID.", "error");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/issue`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ bookId, memberId })
        });
        const data = await res.json();

        if (res.ok) {
            showToast("Book issued successfully!");
            document.getElementById("issueBookId").value = "";
            document.getElementById("issueMemberId").value = "";
            loadIssued();
        } else {
            showToast(data.error || "Failed to issue book.", "error");
        }
    } catch (e) {
        showToast("Network error. Check your server.", "error");
    }
}

async function returnBook() {
    const issueId = parseInt(document.getElementById("returnIssueId").value);

    if (!issueId) {
        showToast("Please enter a valid Issue ID.", "error");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/return`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ issueId })
        });
        const data = await res.json();

        if (res.ok) {
            showToast("Book returned successfully!");
            document.getElementById("returnIssueId").value = "";
            loadIssued();
        } else {
            showToast(data.error || "Failed to return book.", "error");
        }
    } catch (e) {
        showToast("Network error. Check your server.", "error");
    }
}
