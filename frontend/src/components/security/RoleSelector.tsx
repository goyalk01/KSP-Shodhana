'use client';

import React from 'react';
import { useAuthStore, OfficerRole } from '@/features/auth/useAuthStore';

export const RoleSelector: React.FC = () => {
  const { currentRole, officerName, setRole } = useAuthStore();

  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setRole(e.target.value as OfficerRole);
  };

  return (
    <div className="flex items-center space-x-2 bg-slate-900/90 border border-amber-500/30 rounded-lg px-3 py-1.5 shadow-sm">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 text-amber-400 shrink-0">
        <path fillRule="evenodd" d="M10 1a4.5 4.5 0 0 0-4.5 4.5V9H5a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2h-.5V5.5A4.5 4.5 0 0 0 10 1Zm3 8V5.5a3 3 0 1 0-6 0V9h6Z" clipRule="evenodd" />
      </svg>
      <div className="flex flex-col">
        <span className="text-[10px] uppercase font-mono tracking-wider text-slate-400">
          RBAC Role: <span className="text-white font-semibold">{officerName}</span>
        </span>
        <select
          value={currentRole}
          onChange={handleRoleChange}
          className="bg-transparent text-xs font-mono font-medium text-amber-300 focus:outline-none cursor-pointer"
        >
          <option value="ROLE_OFFICER" className="bg-slate-900 text-white">ROLE_OFFICER (Standard)</option>
          <option value="ROLE_INSPECTOR" className="bg-slate-900 text-white">ROLE_INSPECTOR (Investigator)</option>
          <option value="ROLE_SUPERINTENDENT" className="bg-slate-900 text-white">ROLE_SUPERINTENDENT (Full Vault Access)</option>
        </select>
      </div>
    </div>
  );
};
