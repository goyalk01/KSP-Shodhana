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
          className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-[10px] font-bold shadow-soft ${
            isUser
              ? "bg-gradient-to-br from-[var(--color-secondary)] to-[var(--color-warning)] text-white"
              : isSystem
                ? "bg-[var(--color-danger)]/15 text-[var(--color-danger)]"
                : "bg-gradient-to-br from-[var(--color-primary)] to-[#7e9672] text-white"
          }`}
        >
          {isUser ? "You" : isSystem ? "!" : "AI"}
        </div>

         {/* Message Bubble */}
        <div
          className={`max-w-[85%] px-4 py-2.5 text-xs font-semibold leading-relaxed shadow-soft ${
            isUser
              ? "bg-[var(--color-primary)] text-[var(--color-primary-foreground)] rounded-[20px] rounded-tr-[4px]"
              : isSystem
                ? "bg-[var(--color-danger)]/10 text-[var(--color-danger)] border border-[var(--color-danger)]/20 rounded-[20px] rounded-tl-[4px]"
                : "bg-white text-[var(--color-text)] border border-[var(--color-border)]/50 rounded-[20px] rounded-tl-[4px]"
          }`}
        >
          {isLoading ? (
            <div className="flex items-center gap-1.5 py-1">
              <span className="typing-dot" />
              <span className="typing-dot" />
              <span className="typing-dot" />
              <span className="ml-2 text-[10px] text-[var(--color-text-dim)]">
                Analyzing...
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
