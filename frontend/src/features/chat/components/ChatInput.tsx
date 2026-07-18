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
    <div className="border-t border-[var(--color-border)] p-3">
      <div className="flex items-end gap-2 rounded-xl border border-[var(--color-border)] bg-[var(--color-surface)] p-2 focus-within:border-[var(--color-primary)]/50 transition-colors">
        <textarea
          ref={inputRef}
          value={text}
          onChange={(e) => setText(e.target.value.slice(0, MAX_QUERY_LENGTH))}
          onKeyDown={handleKeyDown}
          placeholder="Ask about crimes, criminals, or investigations..."
          disabled={isQuerying}
          rows={1}
          className="flex-1 resize-none bg-transparent px-2 py-1.5 text-sm text-[var(--color-text)] placeholder:text-[var(--color-text-dim)] focus:outline-none disabled:opacity-50"
          style={{ maxHeight: "120px" }}
        />
        <button
          onClick={handleSubmit}
          disabled={!text.trim() || isQuerying}
          className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-[var(--color-primary)] text-white transition-all hover:bg-[var(--color-primary-hover)] disabled:opacity-30 disabled:cursor-not-allowed"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
            className="h-4 w-4"
          >
            <path d="M3.105 2.288a.75.75 0 0 0-.826.95l1.414 4.926A1.5 1.5 0 0 0 5.135 9.25h6.115a.75.75 0 0 1 0 1.5H5.135a1.5 1.5 0 0 0-1.442 1.086l-1.414 4.926a.75.75 0 0 0 .826.95 28.897 28.897 0 0 0 15.293-7.155.75.75 0 0 0 0-1.114A28.897 28.897 0 0 0 3.105 2.288Z" />
          </svg>
        </button>
      </div>
      <p className="mt-1.5 text-[10px] text-[var(--color-text-dim)] text-center">
        Press Enter to send · Shift+Enter for new line · {text.length}/{MAX_QUERY_LENGTH}
      </p>
    </div>
  );
}
