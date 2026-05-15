import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function RegisterPage() {
  const { register, token, bootstrapping } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
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
    setSubmitting(true);
    try {
      await register(username.trim(), password);

      // success message
      setSuccess("Registration successful! Please login to continue.");

      // form clear
      setUsername("");
      setPassword("");

      // hide message after 2 seconds
      setTimeout(() => {
        setSuccess("");
      }, 2000);
    } catch (err) {
      setError(err?.message ?? "Registration failed");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1 className="auth-title">Create account</h1>
        <p className="auth-sub">Register to use the chatbot.</p>
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
        <label className="auth-label">
          Password
          <div className="auth-password-wrap">
            <input
              className="auth-input auth-password-input"
              type={showPassword ? "text" : "password"}
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
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
        <button className="auth-submit" type="submit" disabled={submitting}>
          {submitting ? "Creating…" : "Register"}
        </button>
        <p className="auth-footer">
          {success ? (
            <>
              Go to <Link to="/login">Login Page</Link>
            </>
          ) : (
            <>
              Already have an account? <Link to="/login">Sign in</Link>
            </>
          )}
        </p>
      </form>
    </div>
  );
}
