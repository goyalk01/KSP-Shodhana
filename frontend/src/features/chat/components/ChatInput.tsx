"use client";

import { useState, useRef, useCallback } from "react";
import { useWorkspaceStore } from "@/stores/workspaceStore";
import { MAX_QUERY_LENGTH } from "@/lib/constants";

export default function ChatInput() {
  const [text, setText] = useState("");
  const { sendQuery, isQuerying } = useWorkspaceStore();
  const inputRef = useRef<HTMLTextAreaElement>(null);

  const handleSubmit = useCallback(() => {
    const trimmed = text.trim();
    if (!trimmed || isQuerying) return;
    sendQuery(trimmed);
    setText("");
    inputRef.current?.focus();
  }, [text, isQuerying, sendQuery]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div className="border-t border-[var(--color-border)]/50 p-4 bg-white/10">
      <div className="flex items-center gap-2 rounded-2xl border border-[var(--color-border)] bg-white/90 p-2 pl-4 pr-2 focus-within:border-[var(--color-primary)]/50 focus-within:shadow-[0_4px_16px_rgba(93,112,82,0.1)] transition-all">
        <textarea
          ref={inputRef}
          value={text}
          onChange={(e) => setText(e.target.value.slice(0, MAX_QUERY_LENGTH))}
          onKeyDown={handleKeyDown}
          placeholder="Ask about crimes or criminals..."
          disabled={isQuerying}
          aria-label="Natural language investigation query input"
          rows={1}
          className="flex-1 resize-none bg-transparent py-1.5 text-xs font-semibold text-[var(--color-text)] placeholder:text-[var(--color-text-dim)] focus:outline-none disabled:opacity-50 min-w-0"
          style={{ maxHeight: "120px" }}
        />
        <button
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
        Press Enter to send · Shift+Enter for new line · {text.length}/{MAX_QUERY_LENGTH}
      </p>
    </div>
  );
}
