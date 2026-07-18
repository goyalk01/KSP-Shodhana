"use client";

import type { ChatMessage as ChatMessageType } from "@/types/api";

interface ChatMessageProps {
  message: ChatMessageType;
  index: number;
}

export default function ChatMessage({ message, index }: ChatMessageProps) {
  const isUser = message.role === "user";
  const isSystem = message.role === "system";
  const isLoading = message.isLoading;

  return (
    <div
      className="animate-fade-in"
      style={{ animationDelay: `${index * 0.05}s` }}
    >
      <div
        className={`flex gap-3 ${isUser ? "flex-row-reverse" : "flex-row"}`}
      >
        {/* Avatar */}
        <div
          className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs font-bold ${
            isUser
              ? "bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-primary-hover)] text-white"
              : isSystem
                ? "bg-[var(--color-danger)]/20 text-[var(--color-danger)]"
                : "bg-gradient-to-br from-[var(--color-accent)] to-emerald-500 text-white"
          }`}
        >
          {isUser ? "You" : isSystem ? "!" : "AI"}
        </div>

        {/* Message Bubble */}
        <div
          className={`max-w-[85%] rounded-xl px-3.5 py-2.5 text-sm leading-relaxed ${
            isUser
              ? "bg-[var(--color-primary)] text-white rounded-br-sm"
              : isSystem
                ? "bg-[var(--color-danger)]/10 text-[var(--color-danger)] border border-[var(--color-danger)]/20 rounded-bl-sm"
                : "bg-[var(--color-surface)] text-[var(--color-text)] border border-[var(--color-border)] rounded-bl-sm"
          }`}
        >
          {isLoading ? (
            <div className="flex items-center gap-1.5 py-1">
              <span className="typing-dot" />
              <span className="typing-dot" />
              <span className="typing-dot" />
              <span className="ml-2 text-xs text-[var(--color-text-muted)]">
                Analyzing your query...
              </span>
            </div>
          ) : (
            <p className="whitespace-pre-wrap">{message.content}</p>
          )}
        </div>
      </div>

      {/* Timestamp */}
      <p
        className={`mt-1 text-[10px] text-[var(--color-text-dim)] ${
          isUser ? "text-right mr-10" : "ml-10"
        }`}
      >
        {new Date(message.timestamp).toLocaleTimeString("en-IN", {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </p>
    </div>
  );
}
