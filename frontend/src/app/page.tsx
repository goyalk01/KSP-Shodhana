"use client";

import { useState, useEffect } from "react";
import { useWorkspaceStore } from "@/stores/workspaceStore";
import ChatPanel from "@/features/chat/components/ChatPanel";
import VisualizationGrid from "@/features/workspace/components/VisualizationGrid";
import WorkspaceHeader from "@/features/workspace/components/WorkspaceHeader";
import WelcomeState from "@/features/workspace/components/WelcomeState";
import Sidebar from "@/features/workspace/components/Sidebar";
import SettingsPanel from "@/features/workspace/components/SettingsPanel";

export default function WorkspacePage() {
  const { messages, activeVisualizations, activeTab } = useWorkspaceStore();

  const hasMessages = messages.length > 0;
  const hasVisualizations = activeVisualizations.length > 0;

  // Interactive Resizable Slider for Chat vs Visualization Box Area
  const [chatWidth, setChatWidth] = useState(360);
  const [isDragging, setIsDragging] = useState(false);

  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isDragging) return;
      // 72px is left Sidebar width + 12px padding offset
      const newWidth = e.clientX - 84;
      if (newWidth >= 260 && newWidth <= 720) {
        setChatWidth(newWidth);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    if (isDragging) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
    }

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging]);

  return (
    <div className="flex h-screen overflow-hidden bg-[var(--color-background)]">
      {/* Left Sidebar Navigation */}
      <Sidebar />

      {/* Main Content Area */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header */}
        <WorkspaceHeader />

        {/* Body */}
        <div className="flex flex-1 overflow-hidden">
          {activeTab === "settings" ? (
            <SettingsPanel />
          ) : (
            <>
              {/* Resizable Chat Box Area */}
              <div
                className="flex flex-col p-3 pr-0 shrink-0 h-full overflow-hidden"
                style={{ width: `${chatWidth}px` }}
              >
                <ChatPanel />
              </div>

              {/* Resizable Slider Drag Handle */}
              <div
                onMouseDown={handleMouseDown}
                title="Click and drag to increase or decrease box area width"
                className={`group relative flex w-3 items-center justify-center cursor-col-resize select-none shrink-0 transition-colors ${
                  isDragging ? "bg-[var(--color-primary)]/20" : "hover:bg-[var(--color-primary)]/10"
                }`}
              >
                {/* Visual Drag Knob */}
                <div
                  className={`h-9 w-1.5 rounded-full transition-all duration-150 ${
                    isDragging
                      ? "bg-[var(--color-primary)] scale-y-125"
                      : "bg-[var(--color-border)] group-hover:bg-[var(--color-primary)]"
                  }`}
                />
              </div>

              {/* Dynamic Visualization Area */}
              <div className="flex flex-1 flex-col overflow-hidden h-full min-w-0">
                {hasVisualizations ? (
                  <VisualizationGrid />
                ) : (
                  <WelcomeState showMinimal={hasMessages} />
                )}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
