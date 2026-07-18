/**
 * TypeScript types specific to the Chat feature.
 */

export interface SuggestionChip {
  text: string;
  icon?: string;
}

export interface VoiceInputState {
  isListening: boolean;
  transcript: string;
  error: string | null;
}
