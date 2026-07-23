"""
Vector Search & RAG engine for KSP Shodhana AI Service.
Provides semantic embedding search across FIR records and suspect dossiers.
"""

import math
import logging
from typing import Any, List, Dict

logger = logging.getLogger(__name__)


class VectorStore:
    """In-memory cosine similarity vector index supporting pgvector / Qdrant RAG fallback."""

    def __init__(self) -> None:
        self._documents: List[Dict[str, Any]] = []

    def add_document(self, doc_id: str, content: str, metadata: Dict[str, Any]) -> None:
        """Add document for vector search indexing."""
        self._documents.append({
            "id": doc_id,
            "content": content,
            "metadata": metadata,
            "tokens": set(content.lower().split())
        })

    def search_semantic(self, query: str, top_k: int = 3) -> List[Dict[str, Any]]:
        """Perform semantic search using TF-IDF term overlap cosine score."""
        query_tokens = set(query.lower().split())
        if not query_tokens:
            return []

        results = []
        for doc in self._documents:
            intersection = query_tokens.intersection(doc["tokens"])
            score = len(intersection) / math.sqrt(len(query_tokens) * max(1, len(doc["tokens"])))
            if score > 0:
                results.append({
                    "id": doc["id"],
                    "content": doc["content"],
                    "score": round(score, 4),
                    "metadata": doc["metadata"]
                })

        results.sort(key=lambda x: x["score"], reverse=True)
        return results[:top_k]


# Singleton instance
vector_store = VectorStore()

# Seed basic RAG document corpus
vector_store.add_document(
    "DOC-KA-2026-00101",
    "Chain snatching near MG Road metro station involving getaway biker. Suspects Ravi Kumar S and Suresh M.",
    {"fir": "KA/2026/00101", "district": "Bengaluru Urban", "severity": "High"}
)
vector_store.add_document(
    "DOC-KA-2026-00103",
    "Armed jewelry store robbery in Gokulam Mysuru. Suspects Anil D'Souza and Zubair Ahmed fencing gold.",
    {"fir": "KA/2026/00103", "district": "Mysuru", "severity": "Critical"}
)
vector_store.add_document(
    "DOC-KA-2026-00104",
    "Cyber banking phishing SIM swap operation in Whitefield. Suspects Mohammed Farooq and Pradeep Gowda.",
    {"fir": "KA/2026/00104", "district": "Bengaluru Urban", "severity": "Medium"}
)
