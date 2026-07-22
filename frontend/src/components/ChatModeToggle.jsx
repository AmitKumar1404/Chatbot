export default function ChatModeToggle({
  chatMode,
  onChange,
  hasDocument,
  selectedDocumentName,
  disabled,
}) {
  const askDisabled = disabled || !hasDocument;

  return (
    <div className="chat-mode-bar">
      <div className="chat-mode-toggle" role="group" aria-label="Chat mode">
        <button
          type="button"
          className={`chat-mode-btn ${chatMode === "NORMAL" ? "active" : ""}`}
          disabled={disabled}
          onClick={() => onChange("NORMAL")}
        >
          Normal Chat
        </button>
        <button
          type="button"
          className={`chat-mode-btn ${chatMode === "DOCUMENT" ? "active" : ""}`}
          disabled={askDisabled}
          title={hasDocument ? "Ask questions about the uploaded PDF" : "Upload a PDF first"}
          onClick={() => {
            if (!hasDocument) return;
            onChange("DOCUMENT");
          }}
        >
          Ask about PDF
        </button>
      </div>
      <div className="chat-mode-meta">
        {selectedDocumentName ? (
          <span className="chat-mode-file" title={selectedDocumentName}>
            PDF: {selectedDocumentName}
          </span>
        ) : (
          <span className="chat-mode-hint">Upload a PDF first</span>
        )}
        {chatMode === "DOCUMENT" && !hasDocument && (
          <span className="chat-mode-hint">Upload a PDF first</span>
        )}
      </div>
    </div>
  );
}
