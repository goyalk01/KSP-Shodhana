"use client";

import React, { useEffect, useState } from "react";

interface WatermarkProps {
  badgeNumber?: string;
  officerName?: string;
}

export default function WatermarkOverlay({
  badgeNumber = "KSP-OFFICER-7892",
  officerName = "Ins. Rajesh Gowda",
}: WatermarkProps) {
  const [timestamp, setTimestamp] = useState<string>("");

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimestamp(now.toISOString().replace("T", " ").substring(0, 19) + " IST");
    };
    updateTime();
    const interval = setInterval(updateTime, 30000); // update every 30s
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="pointer-events-none fixed inset-0 z-50 overflow-hidden select-none opacity-[0.035] dark:opacity-[0.05]">
      <div className="grid h-full w-full grid-cols-2 gap-12 p-8 md:grid-cols-3">
        {Array.from({ length: 9 }).map((_, idx) => (
          <div
            key={idx}
            className="flex -rotate-12 flex-col items-center justify-center font-mono text-xs font-bold tracking-widest text-slate-900 dark:text-slate-100"
          >
            <div>CONFIDENTIAL  KSP SHODHANA</div>
            <div>{badgeNumber} | {officerName}</div>
            <div>{timestamp}</div>
            <div>IP: 172.25.210.164 | STRICTLY RESTRICTED</div>
          </div>
        ))}
      </div>
    </div>
  );
}
