/**
 * Multi-language translation utility for KSP Shodhana workspace.
 * Translates English investigation texts into native Hindi and Kannada scripts.
 */

type TargetLang = "hi-IN" | "kn-IN" | "en-IN";

const TRANSLATION_MAP: Record<string, { hi: string; kn: string }> = {
  "Analysis complete for query": {
    hi: "प्रश्न के लिए विश्लेषण पूरा हो गया है।",
    kn: "ವಿಚಾರಣೆಗೆ ವಿಶ್ಲೇಷಣೆ ಸಂಪೂರ್ಣಗೊಂಡಿದೆ.",
  },
  "Show crime hotspots in Karnataka": {
    hi: "कर्नाटक में अपराध के हॉटस्पॉट दिखाएं।",
    kn: "ಕರ್ನಾಟಕದಲ್ಲಿ ಅಪರಾಧದ ಹಾಟ್ಸ್ಪಾಟ್ಗಳನ್ನು ತೋರಿಸಿ.",
  },
  "Show all theft cases in Bengaluru": {
    hi: "बेंगलुरु में सभी चोरी के मामले दिखाएं।",
    kn: "ಬೆಂಗಳೂರಿನಲ್ಲಿ ಎಲ್ಲಾ ಕಳ್ಳತನ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ.",
  },
  "Show the criminal network of Ravi Kumar": {
    hi: "रवि कुमार का आपराधिक नेटवर्क दिखाएं।",
    kn: "ರವಿ ಕುಮಾರ್ ಅವರ ಅಪರಾಧ ಜಾಲವನ್ನು ತೋರಿಸಿ.",
  },
  "Direct suspect co-accused link established between primary accused and getaway accomplice.": {
    hi: "मुख्य आरोपी और आरोपी सहयोगी के बीच सीधा संदिग्ध संबंध स्थापित किया गया है।",
    kn: "ಮುಖ್ಯ ಆರೋಪಿ ಮತ್ತು ಸಹ-ಆರೋಪಿ ನಡುವೆ ನೇರ ಶಂಕಿತ ಸಂಪರ್ಕ ಸ್ಥಾಪಿಸಲಾಗಿದೆ.",
  },
  "Crime Concentration in Target Area": {
    hi: "लक्ष्य क्षेत्र में अपराध का संकेंद्रण।",
    kn: "ಉದ್ದೇಶಿತ ಪ್ರದೇಶದಲ್ಲಿ ಅಪರಾಧದ ಸಾಂದ್ರತೆ.",
  },
  "Multiple incidents reported near commercial hubs with active modus operandi pattern.": {
    hi: "वाणिज्यिक केंद्रों के पास सक्रिय कार्यप्रणाली के साथ कई घटनाएं दर्ज की गई हैं।",
    kn: "ವಾಣಿಜ್ಯ ಕೇಂದ್ರಗಳ ಬಳಿ ಸಕ್ರಿಯ ಕಾರ್ಯವಿಧಾನದೊಂದಿಗೆ ಹಲವು ಘಟನೆಗಳು ವರದಿಯಾಗಿವೆ.",
  },
  "Searching crime records and compiling intelligence visualizations...": {
    hi: "अपराध रिकॉर्ड खोजे जा रहे हैं और खुफिया दृश्य तैयार किए जा रहे हैं...",
    kn: "ಅಪರಾಧ ದಾಖಲೆಗಳನ್ನು ಹುಡುಕಲಾಗುತ್ತಿದೆ ಮತ್ತು ತನಿಖಾ ಮಾಹಿತಿಯನ್ನು ಸಿದ್ಧಪಡಿಸಲಾಗುತ್ತಿದೆ...",
  },
};

const COMMON_WORDS_HI: Record<string, string> = {
  crime: "अपराध",
  crimes: "अपराधों",
  criminal: "अपराधी",
  criminals: "अपराधियों",
  theft: "चोरी",
  robbery: "डकैती",
  investigation: "जांच",
  evidence: "साक्ष्य",
  network: "नेटवर्क",
  hotspot: "हॉटस्पॉट",
  hotspots: "हॉटस्पॉट",
  suspect: "संदिग्ध",
  suspects: "संदिग्धों",
  police: "पुलिस",
  station: "थाना",
  district: "जिला",
  case: "मामला",
  cases: "मामले",
  timeline: "समयरेखा",
  analysis: "विश्लेषण",
  bengaluru: "बेंगलुरु",
  karnataka: "कर्नाटक",
};

const COMMON_WORDS_KN: Record<string, string> = {
  crime: "ಅಪರಾಧ",
  crimes: "ಅಪರಾಧಗಳು",
  criminal: "ಅಪರಾಧಿ",
  criminals: "ಅಪರಾಧಿಗಳು",
  theft: "ಕಳ್ಳತನ",
  robbery: "ದರೋಡೆ",
  investigation: "ತನಿಖೆ",
  evidence: "ಸಾಕ್ಷ್ಯಾಧಾರ",
  network: "ಜಾಲ",
  hotspot: "ಹಾಟ್ಸ್ಪಾಟ್",
  hotspots: "ಹಾಟ್ಸ್ಪಾಟ್ಗಳು",
  suspect: "ಶಂಕಿತ",
  suspects: "ಶಂಕಿತರು",
  police: "ಪೋಲಿಸ್",
  station: "ಠಾಣೆ",
  district: "ಜಿಲ್ಲೆ",
  case: "ಪ್ರಕರಣ",
  cases: "ಪ್ರಕರಣಗಳು",
  timeline: "ಕಾಲಾನುಕ್ರಮ",
  analysis: "ವಿಶ್ಲೇಷಣೆ",
  bengaluru: "ಬೆಂಗಳೂರು",
  karnataka: "ಕರ್ನಾಟಕ",
};

/**
 * Translate an English text to Hindi or Kannada script for realistic text-to-speech rendering.
 */
export function translateTextForSpeech(text: string, targetLang: TargetLang): string {
  if (targetLang === "en-IN" || !text.trim()) {
    return text;
  }

  // Exact phrase match
  const trimmed = text.trim();
  for (const [key, map] of Object.entries(TRANSLATION_MAP)) {
    if (trimmed.toLowerCase().includes(key.toLowerCase())) {
      return targetLang === "hi-IN" ? map.hi : map.kn;
    }
  }

  // Word-by-word substitution dictionary
  const dict = targetLang === "hi-IN" ? COMMON_WORDS_HI : COMMON_WORDS_KN;
  let translated = text;

  for (const [engWord, transWord] of Object.entries(dict)) {
    const regex = new RegExp(`\\b${engWord}\\b`, "gi");
    translated = translated.replace(regex, transWord);
  }

  // Add leading language prefix if text remains mostly English
  if (targetLang === "hi-IN" && !/[\u0900-\u097F]/.test(translated)) {
    return `रिपोर्ट विवरण: ${translated}`;
  }
  if (targetLang === "kn-IN" && !/[\u0C80-\u0CFF]/.test(translated)) {
    return `ವರದಿ ವಿವರಣೆ: ${translated}`;
  }

  return translated;
}
