import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { API_BASE_URL } from "../apiConfig";

async function parseJsonSafe(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

export default function ForgotPasswordPage() {
  const { token, bootstrapping } = useAuth();
  const [username, setUsername] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [submitting, setSubmitting] = useState(false);

  if (bootstrapping) {
    return (
      <div className="auth-page">
        <p className="auth-loading-msg">Loading…</p>
      </div>
    );
  }

  if (token) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);
    try {
      const res = await fetch(`${API_BASE_URL}/auth/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username.trim() }),
      });
      const body = await parseJsonSafe(res);
      if (!res.ok) {
        throw new Error(body?.message ?? "Request failed");
      }
      setSuccess(body?.message ?? "If an account with that username exists, a password reset link has been sent.");
      setUsername("");
    } catch (err) {
      setError(err?.message ?? "Request failed");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1 className="auth-title">Forgot password</h1>
        <p className="auth-sub">Enter your username and we&apos;ll email you a reset link.</p>
        {error ? <div className="auth-error">{error}</div> : null}
        {success ? <div className="auth-success">{success}</div> : null}
        <label className="auth-label">
          Username
          <input
            className="auth-input"
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <button className="auth-submit" type="submit" disabled={submitting}>
          {submitting ? "Sending…" : "Send reset link"}
        </button>
        <p className="auth-footer">
          <Link to="/login">Back to sign in</Link>
        </p>
      </form>
    </div>
  );
}
