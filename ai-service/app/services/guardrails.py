"""
Enterprise AI Guardrails & Prompt Injection Defense Engine.
Detects prompt hijacking, system instruction overrides, and jailbreak payloads.
"""

import re
import logging
from fastapi import HTTPException

logger = logging.getLogger(__name__)

# Regular expressions for common LLM prompt injection and jailbreak attacks
INJECTION_PATTERNS = [
    re.compile(r"ignore\s+(all\s+)?(previous|prior)\s+instructions", re.IGNORECASE),
    re.compile(r"override\s+(system|safety)\s+prompt", re.IGNORECASE),
    re.compile(r"you\s+are\s+now\s+in\s+DAN\s+mode", re.IGNORECASE),
    re.compile(r"bypass\s+(security|guardrails|filters)", re.IGNORECASE),
    re.compile(r"system:\s*you\s+are", re.IGNORECASE),
    re.compile(r"act\s+as\s+an\s+unrestricted\s+AI", re.IGNORECASE),
]


class GuardrailInspector:
    """Prompt injection defense and input sanitization inspector."""

    def sanitize_and_validate(self, text: str) -> str:
        if not text:
            return text

        for pattern in INJECTION_PATTERNS:
            if pattern.search(text):
                logger.warning("Prompt Injection Hijack Blocked: '%s'", text[:50])
                raise HTTPException(
                    status_code=400,
                    detail="SECURITY_VIOLATION: Prompt injection or jailbreak payload detected and blocked by KSP Guardrails."
                )

        return text.strip()


guardrail_inspector = GuardrailInspector()
