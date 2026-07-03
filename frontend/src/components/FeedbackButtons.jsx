import { ThumbsDown, ThumbsUp } from "lucide-react";

export default function FeedbackButtons({
  selectedFeedback,
  loading,
  onFeedback,
}) {
  return (
    <>
      <button
        type="button"
        className="msg-copy-btn"
        disabled={loading}
        aria-pressed={selectedFeedback === "HELPFUL"}
        onClick={() => onFeedback("HELPFUL")}
      >
        <span className="tooltip-text">Helpful</span>
        <ThumbsUp
          size={16}
          strokeWidth={selectedFeedback === "HELPFUL" ? 3 : 2}
        />
      </button>
      <button
        type="button"
        className="msg-copy-btn"
        disabled={loading}
        aria-pressed={selectedFeedback === "NOT_HELPFUL"}
        onClick={() => onFeedback("NOT_HELPFUL")}
      >
        <span className="tooltip-text">Not helpful</span>
        <ThumbsDown
          size={16}
          strokeWidth={selectedFeedback === "NOT_HELPFUL" ? 3 : 2}
        />
      </button>
    </>
  );
}
