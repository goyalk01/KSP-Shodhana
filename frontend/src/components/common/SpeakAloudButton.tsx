"use client";

import { useState, useEffect, useRef } from "react";

type LanguageCode = "en-IN" | "hi-IN" | "kn-IN";

const LANGUAGES: { code: LanguageCode; label: string; flag: string }[] = [
  { code: "en-IN", label: "English", flag: "EN" },
  { code: "hi-IN", label: "हिंदी", flag: "HI" },
  { code: "kn-IN", label: "ಕನ್ನಡ", flag: "KN" },
];

interface SpeakAloudButtonProps {
  text: string;
  size?: "sm" | "md";
}

export default function SpeakAloudButton({ text, size = "md" }: SpeakAloudButtonProps) {
  const [isSpeaking, setIsSpeaking] = useState(false);
  const [selectedLang, setSelectedLang] = useState<LanguageCode>("en-IN");
  const [showMenu, setShowMenu] = useState(false);
  const [supported, setSupported] = useState(true);

  const utteranceRef = useRef<SpeechSynthesisUtterance | null>(null);

  // Auto-detect language from text content on mount/change
  useEffect(() => {
    if (typeof window === "undefined" || !("speechSynthesis" in window)) {
      setSupported(false);
      return;
    }

    if (/[\u0C80-\u0CFF]/.test(text)) {
      setSelectedLang("kn-IN");
    } else if (/[\u0900-\u097F]/.test(text)) {
      setSelectedLang("hi-IN");
    } else {
      setSelectedLang("en-IN");
    }
  }, [text]);

  // Clean up synthesis on unmount
  useEffect(() => {
    return () => {
      if (typeof window !== "undefined" && "speechSynthesis" in window) {
        window.speechSynthesis.cancel();
      }
    };
  }, []);

  const handleToggleSpeak = () => {
    if (!supported || typeof window === "undefined") {
      alert("Text-to-Speech is not supported in this browser.");
      return;
    }

    const synth = window.speechSynthesis;

    if (isSpeaking) {
      synth.cancel();
      setIsSpeaking(false);
      return;
    }

    // Cancel any active speech
    synth.cancel();

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = selectedLang;
    utterance.rate = 0.95; // Slightly slower for crisp clear officer audio
    utterance.pitch = 1.0;

    // Pick best available voice matching requested language
    const voices = synth.getVoices();
    const matchingVoice = voices.find(
      (v) => v.lang === selectedLang || v.lang.startsWith(selectedLang.slice(0, 2))
    );
    if (matchingVoice) {
      utterance.voice = matchingVoice;
    }

    utterance.onend = () => {
      setIsSpeaking(false);
    };

    utterance.onerror = (e) => {
      console.warn("Speech synthesis error:", e);
      setIsSpeaking(false);
    };

    utteranceRef.current = utterance;
    setIsSpeaking(true);
    synth.speak(utterance);
  };

  if (!supported) return null;

  return (
    <div className="relative inline-flex items-center gap-1">
      <button
        type="button"
        onClick={handleToggleSpeak}
        aria-label={isSpeaking ? "Stop Speaking Aloud" : `Read Aloud in ${LANGUAGES.find((l) => l.code === selectedLang)?.label}`}
        title={isSpeaking ? "Stop Speaking Aloud" : `Read Aloud in ${LANGUAGES.find((l) => l.code === selectedLang)?.label}`}
        className={`flex items-center gap-1.5 rounded-lg px-2 py-1 text-[10px] font-extrabold transition-all duration-200 cursor-pointer ${
          isSpeaking
            ? "bg-[var(--color-primary)] text-white shadow-md animate-pulse"
            : "bg-[var(--color-surface-hover)] text-[var(--color-text-muted)] hover:text-[var(--color-primary)] hover:bg-[var(--color-primary)]/10"
        }`}
      >
        {isSpeaking ? (
          <>
            <span className="flex h-2 w-2 rounded-full bg-white animate-ping" />
            <span>Speaking ({LANGUAGES.find((l) => l.code === selectedLang)?.flag})...</span>
          </>
        ) : (
          <>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-3.5 h-3.5">
              <path d="M10 3.75a.75.75 0 0 0-1.264-.546L4.703 7H3.167A2.167 2.167 0 0 0 1 9.167v1.666C1 12.03 2.03 13 3.167 13h1.536l4.033 3.796A.75.75 0 0 0 10 16.25V3.75ZM15.854 6.146a.75.75 0 0 1 1.06 1.06 5.5 5.5 0 0 1 0 7.778.75.75 0 1 1-1.06-1.06 4 4 0 0 0 0-5.658.75.75 0 0 1 0-1.06Z" />
              <path d="M13.207 8.793a.75.75 0 0 1 1.06 0 2 2 0 0 1 0 2.828.75.75 0 1 1-1.06-1.06.5.5 0 0 0 0-.708.75.75 0 0 1 0-1.06Z" />
            </svg>
            <span>Speak</span>
          </>
        )}
      </button>

      {/* Language Selector Dropdown Pill */}
      <div className="relative">
        <button
          type="button"
          onClick={() => setShowMenu((prev) => !prev)}
          aria-label="Select Read Aloud Voice Language"
          title="Select Read Aloud Voice Language"
          className="rounded-md border border-[var(--color-border)]/60 bg-white px-1.5 py-0.5 text-[9px] font-black text-[var(--color-text-muted)] hover:text-[var(--color-primary)] hover:border-[var(--color-primary)]/40 transition-colors cursor-pointer"
        >
          {LANGUAGES.find((l) => l.code === selectedLang)?.flag} ▾
        </button>

        {showMenu && (
          <div className="absolute bottom-full right-0 mb-1 w-28 rounded-xl border border-[var(--color-border)] bg-white p-1 shadow-xl z-50 animate-fade-in">
            <div className="px-2 py-0.5 text-[8px] font-extrabold text-[var(--color-text-dim)] uppercase tracking-wider border-b border-[var(--color-border)]/40 mb-1">
              Voice Language
            </div>
            {LANGUAGES.map((lang) => (
              <button
                key={lang.code}
                type="button"
                onClick={() => {
                  setSelectedLang(lang.code);
                  setShowMenu(false);
                  if (isSpeaking && typeof window !== "undefined") {
                    window.speechSynthesis.cancel();
                    setIsSpeaking(false);
                  }
                }}
                className={`flex w-full items-center justify-between px-2 py-1 text-[11px] font-bold rounded-md transition-colors cursor-pointer ${
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
    </div>
  );
}
