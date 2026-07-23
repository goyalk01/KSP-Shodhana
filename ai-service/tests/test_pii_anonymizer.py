"""
Unit test suite for FastAPI PII Anonymizer Engine.
"""

from app.services.pii_anonymizer import PIIAnonymizer


def test_pii_anonymization_and_detokenization():
    anonymizer = PIIAnonymizer()
    sample_prompt = "Officer Rajesh Gowda (Aadhaar: 8812-4491-0023, Phone: 9845012345, Vehicle: KA-01-E-7788)"

    masked, map_dict = anonymizer.anonymize(sample_prompt)
    restored = anonymizer.detokenize(masked, map_dict)

    # 1. Verify no sensitive PII leaked in masked cloud prompt
    assert "8812-4491-0023" not in masked
    assert "9845012345" not in masked
    assert "KA-01-E-7788" not in masked

    # 2. Verify exact match on local restoration
    assert restored == sample_prompt
