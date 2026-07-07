import { useEffect, useState } from "react";

const REASONS = [
  { value: "INCORRECT", label: "Incorrect" },
  { value: "INCOMPLETE", label: "Incomplete" },
  { value: "HALLUCINATION", label: "Hallucination" },
  { value: "OFFENSIVE", label: "Offensive" },
  { value: "OTHER", label: "Other" },
];

export default function FeedbackReasonModal({ isOpen, onClose, onSubmit }) {
  const [selectedReason, setSelectedReason] = useState("");

  useEffect(() => {
    if (!isOpen) return undefined;

    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="logout-modal-overlay" onClick={onClose}>
      <div
        className="logout-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="feedback-reason-title"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="feedback-reason-title" className="logout-modal-title">
          Why wasn't this response helpful?
        </h2>

        <div style={{ display: "grid", gap: "10px", marginTop: "16px" }}>
          {REASONS.map((reason) => (
            <label
              key={reason.value}
              style={{
                alignItems: "center",
                color: "#f9fafb",
                cursor: "pointer",
                display: "flex",
                gap: "10px",
                lineHeight: 1.4,
              }}
            >
              <input
                type="radio"
                name="feedbackReason"
                value={reason.value}
                checked={selectedReason === reason.value}
                onChange={() => setSelectedReason(reason.value)}
              />
              {reason.label}
            </label>
          ))}
        </div>

        <div className="logout-modal-actions">
          <button type="button" className="logout-cancel-btn" onClick={onClose}>
            Cancel
          </button>
          <button
            type="button"
            className="logout-confirm-btn"
            disabled={!selectedReason}
            onClick={() => onSubmit(selectedReason)}
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );
}
