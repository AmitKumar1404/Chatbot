import { useEffect, useRef, useState } from "react";

export default function InputBox({ onSend, onStop, isStreaming, disabled }) {
  const [text, setText] = useState("");
  const [isListening, setIsListening] = useState(false);
  const [voiceError, setVoiceError] = useState("");
  const recognitionRef = useRef(null);
  const textareaRef = useRef(null);
  const isRecognitionSupported =
    typeof window !== "undefined" &&
    !!(window.SpeechRecognition || window.webkitSpeechRecognition);

  function stopRecognition() {
    const recognition = recognitionRef.current;
    if (!recognition) return;

    try {
      recognition.stop();
    } catch (error) {
      console.error("Speech recognition stop failed", error);
    }

    setIsListening(false);
  }

  function getRecognition() {
    if (!isRecognitionSupported) return null;
    if (recognitionRef.current) return recognitionRef.current;

    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();

    recognition.lang = navigator.language || "en-US";
    recognition.continuous = true;
    recognition.interimResults = false;

    recognition.onstart = () => {
      setVoiceError("");
      setIsListening(true);
    };

    recognition.onresult = (event) => {
      let finalTranscript = "";

      for (let index = event.resultIndex; index < event.results.length; index += 1) {
        const result = event.results[index];
        if (!result?.isFinal) continue;
        finalTranscript += result[0]?.transcript ?? "";
      }

      const spokenText = finalTranscript.trim();
      if (!spokenText) return;

      setText((prev) => {
        const separator = prev && !/\s$/.test(prev) ? " " : "";
        return `${prev}${separator}${spokenText}`;
      });
      setVoiceError("");
      textareaRef.current?.focus();
    };

    recognition.onerror = (event) => {
      if (event.error === "aborted") return;

      let nextError = "Speech recognition failed. Please try again.";

      if (
        event.error === "not-allowed" ||
        event.error === "service-not-allowed"
      ) {
        nextError = "Microphone access was denied.";
      } else if (event.error === "audio-capture") {
        nextError = "No microphone was found.";
      } else if (event.error === "no-speech") {
        nextError = "No speech was detected.";
      } else if (event.error === "network") {
        nextError = "Speech recognition network error.";
      }

      setVoiceError(nextError);
      setIsListening(false);
    };

    recognition.onend = () => {
      setIsListening(false);
    };

    recognitionRef.current = recognition;
    return recognition;
  }

  function handleMicClick() {
    if (!isRecognitionSupported || disabled || isStreaming) {
      return;
    }

    if (isListening) {
      stopRecognition();
      return;
    }

    const recognition = getRecognition();
    if (!recognition) return;

    try {
      setVoiceError("");
      recognition.start();
    } catch (error) {
      setIsListening(false);
      setVoiceError("Speech recognition could not start.");
      console.error("Speech recognition start failed", error);
    }
  }

  useEffect(() => {
    if (disabled || isStreaming) {
      stopRecognition();
    }
  }, [disabled, isStreaming]);

  useEffect(() => {
    return () => {
      stopRecognition();
      recognitionRef.current = null;
    };
  }, []);

  function handleKeyDown(e) {
    if (e.key !== "Enter" || e.shiftKey) {
      return;
    }

    // ✅ STREAMING CHAL RAHI HAI
    // Enter completely ignore
    if (isStreaming) {
      return;
    }

    e.preventDefault();

    const trimmed = text.trim();

    if (!trimmed) return;

    stopRecognition();
    onSend(trimmed);

    setText("");
  }
  return (
    <div className="input-box">
      <div className="input-textarea-wrap">
        <textarea
          ref={textareaRef}
          className="input-textarea"
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Type a message…"
          rows={2}
          disabled={disabled}
          // readOnly={isStreaming}
        />
        {voiceError && (
          <p className="input-voice-status" role="status" aria-live="polite">
            {voiceError}
          </p>
        )}
      </div>

      <button
        className={`mic-btn ${isListening ? "mic-btn-listening" : ""}`}
        type="button"
        onClick={handleMicClick}
        disabled={disabled || isStreaming || !isRecognitionSupported}
        aria-label={isListening ? "Stop voice input" : "Start voice input"}
        aria-pressed={isListening}
        title={
          !isRecognitionSupported
            ? "Speech input is not supported in this browser"
            : isListening
            ? "Stop voice input"
            : "Start voice input"
        }
      >
        <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
          <path
            d="M12 15a3 3 0 0 0 3-3V7a3 3 0 1 0-6 0v5a3 3 0 0 0 3 3Zm5-3a1 1 0 1 1 2 0a7 7 0 0 1-6 6.92V21h2a1 1 0 1 1 0 2H9a1 1 0 1 1 0-2h2v-2.08A7 7 0 0 1 5 12a1 1 0 0 1 2 0a5 5 0 0 0 10 0Z"
            fill="currentColor"
          />
        </svg>
      </button>

      <button
        className="send-btn"
        type="button"
        disabled={disabled || (!isStreaming && !text.trim())}
        onClick={() => {
          // ✅ stop current response
          if (isStreaming) {
            stopRecognition();
            onStop();
            return;
          }

          const trimmed = text.trim();

          if (!trimmed) return;

          stopRecognition();
          onSend(trimmed);
          setText("");
        }}
      >
        {isStreaming ? "Stop" : "Send"}
      </button>
    </div>
  );
}
