/**
 * General utility functions.
 */

import { type ClassValue, clsx } from "clsx";

/**
 * Merge Tailwind CSS classes with conflict resolution.
 * Uses clsx for conditional classes.
 */
export function cn(...inputs: ClassValue[]) {
  return clsx(inputs);
}

/** Generate a unique ID for messages, etc. */
export function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
}

/** Format a date string for display */
export function formatDate(dateStr: string): string {
  if (!dateStr) return "";
  try {
    const formattedStr = dateStr.includes(" ") ? dateStr.replace(" ", "T") : dateStr;
    const d = new Date(formattedStr);
    if (isNaN(d.getTime())) return dateStr;
    return d.toLocaleDateString("en-IN", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return dateStr;
  }
}

/** Format a confidence score as a percentage */
export function formatConfidence(confidence: number): string {
  return `${Math.round(confidence * 100)}%`;
}

/** Truncate text with ellipsis */
export function truncate(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength - 3) + "...";
}
