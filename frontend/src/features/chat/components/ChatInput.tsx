"use client";

import { useState, useRef, useCallback, useEffect } from "react";
import { useWorkspaceStore } from "@/stores/workspaceStore";
import { MAX_QUERY_LENGTH } from "@/lib/constants";

type LanguageCode = "en-IN" | "hi-IN" | "kn-IN";

const LANGUAGES: { code: LanguageCode; label: string; flag: string }[] = [
  { code: "en-IN", label: "English", flag: "EN" },
  { code: "hi-IN", label: "हिंदी", flag: "HI" },
  { code: "kn-IN", label: "ಕನ್ನಡ", flag: "KN" },
];

export default function ChatInput() {
  const [text, setText] = useState("");
  const [isListening, setIsListening] = useState(false);
  const [selectedLang, setSelectedLang] = useState<LanguageCode>("en-IN");
  const [showLangMenu, setShowLangMenu] = useState(false);
  const [speechSupported, setSpeechSupported] = useState(true);

  const { sendQuery, isQuerying } = useWorkspaceStore();
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const recognitionRef = useRef<any>(null);

  // Initialize SpeechRecognition instance
  useEffect(() => {
    if (typeof window === "undefined") return;

    const SpeechRecognition =
      (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      setSpeechSupported(false);
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = selectedLang;

    recognition.onresult = (event: any) => {
      let currentTranscript = "";
      for (let i = event.resultIndex; i < event.results.length; i++) {
        currentTranscript += event.results[i][0].transcript;
      }
      if (currentTranscript) {
        setText((prev) => {
          const combined = (prev ? prev + " " : "") + currentTranscript.trim();
          return combined.slice(0, MAX_QUERY_LENGTH);
        });
      }
    };

    recognition.onerror = (event: any) => {
      console.warn("Speech recognition error:", event.error);
      setIsListening(false);
    };

    recognition.onend = () => {
      setIsListening(false);
    };

    recognitionRef.current = recognition;

    return () => {
      if (recognitionRef.current) {
        try {
          recognitionRef.current.stop();
        } catch (e) {
          // Ignore stop errors on unmount
        }
      }
    };
  }, [selectedLang]);

  const toggleListening = () => {
    if (!speechSupported || !recognitionRef.current) {
      alert("Voice speech recognition is not supported in this browser. Please use Chrome or Edge.");
      return;
    }

    if (isListening) {
      try {
        recognitionRef.current.stop();
      } catch (e) {
        // ignore
      }
      setIsListening(false);
    } else {
      try {
        recognitionRef.current.lang = selectedLang;
        recognitionRef.current.start();
        setIsListening(true);
      } catch (e) {
        console.error("Failed to start speech recognition:", e);
        setIsListening(false);
      }
    }
  };

  const handleSubmit = useCallback(() => {
    const trimmed = text.trim();
    if (!trimmed || isQuerying) return;
    if (isListening && recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch (e) {
        // ignore
      }
      setIsListening(false);
    }
    sendQuery(trimmed);
    setText("");
    inputRef.current?.focus();
  }, [text, isQuerying, isListening, sendQuery]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div className="border-t border-[var(--color-border)]/50 p-4 bg-white/10 relative">
      {/* Listening Status Bar */}
      {isListening && (
        <div className="mb-2 flex items-center justify-between rounded-xl bg-[var(--color-primary)]/10 px-3.5 py-1.5 border border-[var(--color-primary)]/20 animate-pulse">
          <div className="flex items-center gap-2">
            <span className="flex h-2 w-2 rounded-full bg-[var(--color-danger)] animate-ping" />
            <span className="text-[11px] font-extrabold text-[var(--color-primary)]">
              Listening in {LANGUAGES.find((l) => l.code === selectedLang)?.label}... Speak into microphone
            </span>
          </div>
          <button
            onClick={toggleListening}
            className="text-[10px] font-bold text-[var(--color-danger)] uppercase hover:underline cursor-pointer"
          >
            Stop
          </button>
        </div>
      )}

      <div className="flex items-center gap-2 rounded-2xl border border-[var(--color-border)] bg-white/90 p-2 pl-3 pr-2 focus-within:border-[var(--color-primary)]/50 focus-within:shadow-[0_4px_16px_rgba(93,112,82,0.1)] transition-all">
        {/* Language Selector Dropdown */}
        <div className="relative shrink-0">
          <button
            type="button"
            onClick={() => setShowLangMenu((prev) => !prev)}
            aria-label="Select Voice Language"
            title="Select Voice Language"
            className="flex items-center gap-1 rounded-lg border border-[var(--color-border)]/60 bg-[var(--color-surface)] px-2 py-1 text-[11px] font-extrabold text-[var(--color-text)] hover:bg-[var(--color-surface-hover)] transition-all cursor-pointer"
          >
            <span>{LANGUAGES.find((l) => l.code === selectedLang)?.flag}</span>
            <span className="text-[9px] text-[var(--color-text-muted)]">▼</span>
          </button>

          {showLangMenu && (
            <div className="absolute bottom-full left-0 mb-2 w-32 rounded-xl border border-[var(--color-border)] bg-white p-1 shadow-xl z-50 animate-fade-in">
              <div className="px-2 py-1 text-[9px] font-extrabold text-[var(--color-text-dim)] uppercase tracking-wider border-b border-[var(--color-border)]/40 mb-1">
                Speech Language
              </div>
              {LANGUAGES.map((lang) => (
                <button
                  key={lang.code}
                  type="button"
                  onClick={() => {
                    setSelectedLang(lang.code);
                    setShowLangMenu(false);
                  }}
                  className={`flex w-full items-center justify-between px-2.5 py-1.5 text-xs font-bold rounded-lg transition-colors cursor-pointer ${
                    selectedLang === lang.code
                      ? "bg-[var(--color-primary)] text-white"
                      : "text-[var(--color-text)] hover:bg-[var(--color-surface-hover)]"
                  }`}
                >
                  <span>{lang.label}</span>
                  <span className="text-[9px] opacity-75">{lang.flag}</span>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Text Input */}
        <textarea
          ref={inputRef}
          value={text}
          onChange={(e) => setText(e.target.value.slice(0, MAX_QUERY_LENGTH))}
          onKeyDown={handleKeyDown}
          placeholder={`Ask about crimes or criminals (${LANGUAGES.find((l) => l.code === selectedLang)?.label} Voice active)...`}
          disabled={isQuerying}
          aria-label="Natural language investigation query input"
          rows={1}
          className="flex-1 resize-none bg-transparent py-1.5 text-xs font-semibold text-[var(--color-text)] placeholder:text-[var(--color-text-dim)] focus:outline-none disabled:opacity-50 min-w-0"
          style={{ maxHeight: "120px" }}
        />

        {/* Microphone Button */}
        <button
          type="button"
          onClick={toggleListening}
          disabled={isQuerying}
          aria-label={isListening ? "Stop Listening" : "Start Voice Input"}
          title={isListening ? "Stop Listening" : `Voice Input (${LANGUAGES.find((l) => l.code === selectedLang)?.label})`}
          className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full transition-all duration-200 cursor-pointer ${
            isListening
              ? "bg-[var(--color-danger)] text-white shadow-lg scale-110 animate-bounce"
              : "bg-[var(--color-surface-hover)] text-[var(--color-text-muted)] hover:text-[var(--color-primary)] hover:bg-[var(--color-primary)]/10"
          }`}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4.5 h-4.5">
            <path d="M7 4a3 3 0 0 1 6 0v6a3 3 0 1 1-6 0V4Z" />
            <path d="M5.5 9.643a.75.75 0 0 0-1.5 0V10c0 3.06 2.29 5.58 5.25 5.954V17.5h-1.5a.75.75 0 0 0 0 1.5h4.5a.75.75 0 0 0 0-1.5h-1.5v-1.546A6.001 6.001 0 0 0 16 10v-.357a.75.75 0 0 0-1.5 0V10a4.5 4.5 0 0 1-9 0v-.357Z" />
          </svg>
        </button>

        {/* Submit Button */}
        <button
          type="button"
          onClick={handleSubmit}
          disabled={!text.trim() || isQuerying}
          aria-label="Send investigation query"
          title="Send investigation query"
          className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-[var(--color-primary)] text-white shadow-soft transition-all duration-300 hover:scale-105 hover:bg-[var(--color-primary-hover)] active:scale-90 disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer focus-visible:ring-2 focus-visible:ring-[var(--color-primary)]"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            className="h-4.5 w-4.5"
          >
            <path d="M3.105 2.288a.75.75 0 0 0-.826.95l1.414 4.926A1.5 1.5 0 0 0 5.135 9.25h6.115a.75.75 0 0 1 0 1.5H5.135a1.5 1.5 0 0 0-1.442 1.086l-1.414 4.926a.75.75 0 0 0 .826.95 28.897 28.897 0 0 0 15.293-7.155.75.75 0 0 0 0-1.114A28.897 28.897 0 0 0 3.105 2.288Z" />
          </svg>
        </button>
      </div>

      <p className="mt-2 text-[9px] font-bold text-[var(--color-text-dim)] text-center tracking-wide">
        Voice Input ({LANGUAGES.find((l) => l.code === selectedLang)?.label}) · Press Enter to send · {text.length}/{MAX_QUERY_LENGTH}
      </p>
    </div>
  );
}
