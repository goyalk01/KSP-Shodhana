"use client";

import type { ChatMessage as ChatMessageType } from "@/types/api";
import SpeakAloudButton from "@/components/common/SpeakAloudButton";

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
          className={`max-w-[80%] min-w-0 px-4.5 py-2.5 text-xs font-semibold leading-relaxed shadow-sm break-words ${
            isUser
              ? "bg-[var(--color-primary)] text-[var(--color-primary-foreground)] rounded-xl rounded-tr-xs"
              : isSystem
                ? "bg-[var(--color-danger)]/10 text-[var(--color-danger)] border border-[var(--color-danger)]/20 rounded-xl rounded-tl-xs"
                : "bg-white text-[var(--color-text)] border border-[var(--color-border)]/50 rounded-xl rounded-tl-xs"
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
            <p className="whitespace-pre-wrap break-words">{message.content}</p>
          )}
        </div>
      </div>

      {/* Timestamp & Speak Aloud Controls */}
      <div
        className={`mt-1.5 flex items-center gap-3 ${
          isUser ? "justify-end mr-10" : "ml-11"
        }`}
      >
        <span className="text-[10px] text-[var(--color-text-dim)] font-semibold">
          {new Date(message.timestamp).toLocaleTimeString("en-IN", {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </span>

        {!isUser && !isLoading && message.content && (
          <SpeakAloudButton text={message.content} />
        )}
      </div>
    </div>
  );
}
