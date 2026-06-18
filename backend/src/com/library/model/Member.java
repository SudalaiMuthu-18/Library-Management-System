package com.library.model;

/**
 * Represents a library member.
 */
public class Member {

    private int    memberId;
    private String name;
    private String email;
    private String phone;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Member() {}

    public Member(int memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public int    getMemberId() { return memberId; }
    public String getName()     { return name;     }
    public String getEmail()    { return email;    }
    public String getPhone()    { return phone;    }

    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setName(String name)      { this.name     = name;     }
    public void setEmail(String email)    { this.email    = email;    }
    public void setPhone(String phone)    { this.phone    = phone;    }

    // ─── JSON Serialization ───────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"memberId\":%d,\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"}",
            memberId,
            esc(name),
            esc(email),
            esc(phone)
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
