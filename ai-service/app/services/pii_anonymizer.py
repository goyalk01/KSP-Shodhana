"""
PII Anonymization & Detokenization Engine for KSP Shodhana.
Masks sensitive identities, Aadhaar numbers, phone numbers, and license plates
before prompts leave service boundaries, then restores them on-premise.
"""

import re
import logging
from typing import Tuple, Dict

logger = logging.getLogger(__name__)


class PIIAnonymizer:
    """Enterprise PII Masking and Detokenization engine."""

    def __init__(self) -> None:
        # Regex patterns for Indian PII identifiers
        self.phone_pattern = re.compile(r'\b[6-9]\d{9}\b')
        self.aadhaar_pattern = re.compile(r'\b\d{4}[-\s]?\d{4}[-\s]?\d{4}\b')
        self.vehicle_pattern = re.compile(r'\bKA[-\s]?\d{2}[-\s]?[A-Z]{1,2}[-\s]?\d{4}\b', re.IGNORECASE)

    def anonymize(self, text: str) -> Tuple[str, Dict[str, str]]:
        """
        Redact PII from input text and return (anonymized_text, token_map).
        """
        if not text:
            return text, {}

        token_map: Dict[str, str] = {}
        anonymized = text

        # 1. Mask Aadhaar Numbers
        aadhaar_matches = list(self.aadhaar_pattern.finditer(anonymized))
        for idx, match in enumerate(aadhaar_matches):
            token = f"<AADHAAR_{idx+1}>"
            val = match.group(0)
            token_map[token] = val
            anonymized = anonymized.replace(val, token)

        # 2. Mask Phone Numbers
        phone_matches = list(self.phone_pattern.finditer(anonymized))
        for idx, match in enumerate(phone_matches):
            token = f"<PHONE_{idx+1}>"
            val = match.group(0)
            token_map[token] = val
            anonymized = anonymized.replace(val, token)

        # 3. Mask Vehicle Numbers
        vehicle_matches = list(self.vehicle_pattern.finditer(anonymized))
        for idx, match in enumerate(vehicle_matches):
            token = f"<VEHICLE_{idx+1}>"
            val = match.group(0)
            token_map[token] = val
            anonymized = anonymized.replace(val, token)

        logger.info("PII Anonymization completed: %d tokens masked", len(token_map))
        return anonymized, token_map

    def detokenize(self, text: str, token_map: Dict[str, str]) -> str:
        """
        Restore anonymized tokens back to real values on-premise.
        """
        if not text or not token_map:
            return text

        restored = text
        for token, original in token_map.items():
            restored = restored.replace(token, original)

        return restored


# Singleton instance
pii_anonymizer = PIIAnonymizer()
