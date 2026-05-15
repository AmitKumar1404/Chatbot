import { Navigate, Route, Routes } from 'react-router-dom';
import ChatApp from './ChatApp';
import { useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import './App.css';

function ProtectedChat() {
  const { token, bootstrapping } = useAuth();

  if (bootstrapping) {
    return (
      <div className="auth-page">
        <p className="auth-loading-msg">Loading session…</p>
      </div>
    );
  }

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <ChatApp />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/*" element={<ProtectedChat />} />
    </Routes>
  );
}
