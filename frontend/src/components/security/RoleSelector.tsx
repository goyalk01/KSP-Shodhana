'use client';

import React from 'react';
import { useAuthStore, OfficerRole } from '@/features/auth/useAuthStore';

export const RoleSelector: React.FC = () => {
  const { currentRole, officerName, setRole } = useAuthStore();

  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setRole(e.target.value as OfficerRole);
  };

  return (
    <div className="flex items-center gap-2 rounded-lg border border-[var(--color-primary)]/30 bg-[var(--color-primary)]/10 px-3.5 py-1.5 transition-all duration-200 hover:bg-[var(--color-primary)]/20 shrink-0">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 text-[var(--color-primary)] shrink-0">
        <path fillRule="evenodd" d="M10 1a4.5 4.5 0 0 0-4.5 4.5V9H5a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2h-.5V5.5A4.5 4.5 0 0 0 10 1Zm3 8V5.5a3 3 0 1 0-6 0V9h6Z" clipRule="evenodd" />
      </svg>
      <div className="flex flex-col text-left">
        <span className="text-[9px] uppercase font-bold tracking-wider text-[var(--color-text-muted)] leading-none mb-0.5">
          RBAC: <span className="text-[var(--color-text)] font-extrabold">{officerName}</span>
        </span>
        <select
          value={currentRole}
          onChange={handleRoleChange}
          className="bg-transparent text-xs font-bold text-[var(--color-primary)] focus:outline-none cursor-pointer p-0 m-0 border-none leading-none"
        >
          <option value="ROLE_OFFICER" className="bg-white text-slate-800 font-medium">ROLE_OFFICER (Standard)</option>
          <option value="ROLE_INSPECTOR" className="bg-white text-slate-800 font-medium">ROLE_INSPECTOR (Investigator)</option>
          <option value="ROLE_SUPERINTENDENT" className="bg-white text-slate-800 font-medium">ROLE_SUPERINTENDENT (Vault Admin)</option>
        </select>
      </div>
    </div>
  );
};
