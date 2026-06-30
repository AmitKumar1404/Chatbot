import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import { Link, Navigate, useSearchParams } from "react-router-dom";
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

export default function ResetPasswordPage() {
  const { token, bootstrapping } = useAuth();
  const [searchParams] = useSearchParams();
  const tokenParam = searchParams.get("token") ?? "";

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
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

    if (newPassword !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (!tokenParam) {
      setError("Invalid or missing reset token");
      return;
    }

    setSubmitting(true);
    try {
      const res = await fetch(`${API_BASE_URL}/auth/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: tokenParam, newPassword }),
      });
      const body = await parseJsonSafe(res);
      if (!res.ok) {
        throw new Error(body?.message ?? "Reset failed");
      }
      setSuccess(body?.message ?? "Password reset successful. You can now sign in.");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setError(err?.message ?? "Reset failed");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1 className="auth-title">Reset password</h1>
        <p className="auth-sub">Choose a new password for your account.</p>
        {error ? <div className="auth-error">{error}</div> : null}
        {success ? <div className="auth-success">{success}</div> : null}
        <label className="auth-label">
          New password
          <div className="auth-password-wrap">
            <input
              className="auth-input auth-password-input"
              type={showPassword ? "text" : "password"}
              autoComplete="new-password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
              minLength={6}
            />
            <button
              type="button"
              className="auth-password-toggle"
              onClick={() => setShowPassword((prev) => !prev)}
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
        </label>
        <label className="auth-label">
          Confirm password
          <input
            className="auth-input"
            type={showPassword ? "text" : "password"}
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
            minLength={6}
          />
        </label>
        <button className="auth-submit" type="submit" disabled={submitting || !tokenParam}>
          {submitting ? "Resetting…" : "Reset password"}
        </button>
        <p className="auth-footer">
          {success ? (
            <Link to="/login">Go to sign in</Link>
          ) : (
            <Link to="/login">Back to sign in</Link>
          )}
        </p>
      </form>
    </div>
  );
}
