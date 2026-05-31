import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { API_BASE_URL } from "../apiConfig";

const STORAGE_KEY = "chatbot_access_token";

const AuthContext = createContext(null);

async function parseJsonSafe(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null);
  const [username, setUsername] = useState(null);
  const [bootstrapping, setBootstrapping] = useState(true);

  useEffect(() => {
    const stored = sessionStorage.getItem(STORAGE_KEY);
    if (!stored) {
      setBootstrapping(false);
      return;
    }

    let cancelled = false;

    (async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/auth/me`, {
          headers: { Authorization: `Bearer ${stored}` },
        });
        if (!res.ok) {
          sessionStorage.removeItem(STORAGE_KEY);
          if (!cancelled) {
            setToken(null);
            setUsername(null);
          }
          return;
        }
        const body = await parseJsonSafe(res);
        if (!cancelled && body?.username) {
          setToken(stored);
          setUsername(body.username);
        }
      } catch {
        sessionStorage.removeItem(STORAGE_KEY);
        if (!cancelled) {
          setToken(null);
          setUsername(null);
        }
      } finally {
        if (!cancelled) setBootstrapping(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (uname, password) => {
    const res = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: uname, password }),
    });
    const body = await parseJsonSafe(res);
    if (!res.ok) {
      const msg = body?.message ?? "Login failed";
      throw new Error(msg);
    }
    if (!body?.token) {
      throw new Error("Invalid response from server");
    }
    sessionStorage.setItem(STORAGE_KEY, body.token);
    setToken(body.token);
    setUsername(body.username ?? uname);
  }, []);

  const register = useCallback(async (uname, password) => {
    const res = await fetch(`${API_BASE_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: uname, password }),
    });
    const body = await parseJsonSafe(res);
    if (!res.ok) {
      const msg = body?.message ?? "Registration failed";
      throw new Error(msg);
    }
  }, []);

  const logout = useCallback(() => {
    sessionStorage.removeItem(STORAGE_KEY);
    setToken(null);
    setUsername(null);
  }, []);

  const value = useMemo(
    () => ({
      token,
      username,
      bootstrapping,
      login,
      register,
      logout,
    }),
    [token, username, bootstrapping, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}
