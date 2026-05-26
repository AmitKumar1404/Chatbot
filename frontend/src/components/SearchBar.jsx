import { Search } from "lucide-react";

export default function SearchBar({
  value,
  onChange,
  onSearch,
  isLoading = false,
  disabled = false,
}) {
  const canSearch = !isLoading && !disabled && value.trim().length > 0;

  function handleSubmit(event) {
    event.preventDefault();
    if (!canSearch) return;
    onSearch();
  }

  return (
    <form className="sidebar-search" onSubmit={handleSubmit}>
      <input
        type="text"
        className="sidebar-search-input"
        placeholder="Search chats or messages..."
        value={value}
        onChange={(event) => onChange(event.target.value)}
        aria-label="Search chats or messages"
      />
      <button
        type="submit"
        className="sidebar-search-btn"
        aria-label="Search"
        disabled={!canSearch}
      >
        <Search size={16} />
      </button>
    </form>
  );
}
