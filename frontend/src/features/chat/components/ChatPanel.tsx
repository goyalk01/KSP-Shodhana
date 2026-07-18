"use client";

import { useRef, useEffect } from "react";
import { useWorkspaceStore } from "@/stores/workspaceStore";
import ChatMessage from "./ChatMessage";
import ChatInput from "./ChatInput";
import SuggestionChips from "./SuggestionChips";

export default function ChatPanel() {
  const { messages, isQuerying, suggestedFollowups } = useWorkspaceStore();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Auto-scroll to bottom on new messages
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className="flex flex-1 flex-col overflow-hidden bg-[var(--color-background)]">
      {/* Chat Header */}
      <div className="border-b border-[var(--color-border)] px-4 py-3">
        <h2 className="text-xs font-semibold uppercase tracking-wider text-[var(--color-text-muted)]">
          💬 Investigation Chat
        </h2>
      </div>

      {/* Messages List */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4">
        {messages.length === 0 && (
          <div className="flex flex-col items-center justify-center h-full gap-3 text-center">
            <p className="text-sm text-[var(--color-text-dim)]">
              Ask anything about crimes, criminals, or investigations.
            </p>
          </div>
        )}

        {messages.map((msg, idx) => (
          <ChatMessage key={msg.id} message={msg} index={idx} />
        ))}

        <div ref={messagesEndRef} />
      </div>

      {/* Suggestion Chips */}
      {suggestedFollowups.length > 0 && !isQuerying && (
        <SuggestionChips suggestions={suggestedFollowups} />
      )}

      {/* Input */}
      <ChatInput />
    </div>
  );
}
