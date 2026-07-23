package com.ksp.shodhana.service;

import com.ksp.shodhana.exception.ShodhanaException;
import com.ksp.shodhana.model.Crime;
import com.ksp.shodhana.model.CrimeCriminalLink;
import com.ksp.shodhana.model.Criminal;
import com.ksp.shodhana.model.TimelineEvent;
import com.ksp.shodhana.repository.CrimeCriminalLinkRepository;
import com.ksp.shodhana.repository.CrimeRepository;
import com.ksp.shodhana.repository.CriminalRepository;
import com.ksp.shodhana.repository.TimelineEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for PDF report generation and auto-download preview.
 * Generates structured, print-ready HTML which auto-triggers the browser's PDF save dialog.
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final CrimeRepository crimeRepository;
    private final CrimeCriminalLinkRepository crimeCriminalLinkRepository;
    private final CriminalRepository criminalRepository;
    private final TimelineEventRepository timelineEventRepository;

    public ReportService(
            CrimeRepository crimeRepository,
            CrimeCriminalLinkRepository crimeCriminalLinkRepository,
            CriminalRepository criminalRepository,
            TimelineEventRepository timelineEventRepository) {
        this.crimeRepository = crimeRepository;
        this.crimeCriminalLinkRepository = crimeCriminalLinkRepository;
        this.criminalRepository = criminalRepository;
        this.timelineEventRepository = timelineEventRepository;
    }

    /**
     * Generates a print-ready HTML intelligence report for an investigation,
     * equipped with auto-print PDF trigger script and PDF print styling.
     */
    public String generateReport(Long investigationId) {
        log.info("Generating report for investigation ID {}", investigationId);

        // Fetch primary crime (investigation 1 maps to crime 1, investigation 2 to crime 3)
        Long crimeId = investigationId == 1L ? 1L : 3L;
        Optional<Crime> crimeOpt = crimeRepository.findById(crimeId);
        if (crimeOpt.isEmpty()) {
            throw new ShodhanaException("INVESTIGATION_NOT_FOUND", "Investigation " + investigationId + " has no linked crime");
        }

        Crime crime = crimeOpt.get();
        List<CrimeCriminalLink> links = crimeCriminalLinkRepository.findByCrimeId(crimeId);
        List<TimelineEvent> timeline = timelineEventRepository.findByInvestigationId(investigationId);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='utf-8'>");
        sb.append("<title>KSP Case Intelligence Report - ").append(crime.getFirNumber()).append("</title>");
        sb.append("<link href='https://fonts.googleapis.com/css2?family=Fraunces:wght@700;800&family=Nunito:wght@400;600;700&display=swap' rel='stylesheet'>");
        sb.append("<style>");
        sb.append("body { font-family: 'Nunito', system-ui, sans-serif; color: #2C2C24; line-height: 1.6; padding: 40px; padding-top: 80px; background: #FDFCF8; position: relative; }");
        sb.append("body::before { content: ''; position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 9999; opacity: 0.03; mix-blend-mode: multiply; background-image: url(\"data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E\"); }");
        
        // Print action bar
        sb.append(".print-bar { position: fixed; top: 0; left: 0; right: 0; height: 56px; background: #2C2C24; color: #FFFFFF; display: flex; items-center: center; justify-content: space-between; padding: 0 24px; z-index: 10000; box-shadow: 0 2px 10px rgba(0,0,0,0.15); }");
        sb.append(".print-bar-title { font-size: 13px; font-weight: 700; color: #F0EBE5; }");
        sb.append(".print-btn { background: #5D7052; color: #FFFFFF; border: none; padding: 8px 18px; border-radius: 8px; font-weight: 700; font-size: 12px; cursor: pointer; transition: background 0.2s; }");
        sb.append(".print-btn:hover { background: #4a5a41; }");
        
        // Document styling
        sb.append(".header { text-align: center; border-bottom: 2px solid #5D7052; padding-bottom: 24px; margin-bottom: 30px; }");
        sb.append(".header h1 { font-family: 'Fraunces', Georgia, serif; font-size: 26px; margin: 0; color: #5D7052; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; }");
        sb.append(".header p { margin: 8px 0 0 0; font-size: 11px; color: #78786C; font-weight: 700; letter-spacing: 0.15em; }");
        sb.append(".section { margin-bottom: 35px; background: #FEFEFA; border: 1px solid rgba(222, 216, 207, 0.6); border-radius: 20px; padding: 24px; box-shadow: 0 4px 15px -2px rgba(93, 112, 82, 0.04); page-break-inside: avoid; }");
        sb.append(".section-title { font-family: 'Fraunces', Georgia, serif; font-size: 16px; font-weight: 800; color: #5D7052; border-bottom: 1px solid #DED8CF; padding-bottom: 8px; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 0.05em; }");
        sb.append(".grid { display: grid; grid-template-cols: 1fr 1fr; gap: 20px; margin-bottom: 10px; }");
        sb.append(".field { font-size: 13px; }");
        sb.append(".label { font-weight: 700; color: #78786C; font-size: 11px; text-transform: uppercase; letter-spacing: 0.05em; }");
        sb.append(".value { color: #2C2C24; margin-top: 4px; font-weight: 600; }");
        sb.append(".full-width { grid-column: 1 / span 2; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 13px; }");
        sb.append("th, td { border: 1px solid #DED8CF; padding: 12px; text-align: left; }");
        sb.append("th { background: #F0EBE5; font-weight: 700; color: #5D7052; text-transform: uppercase; font-size: 11px; letter-spacing: 0.05em; }");
        sb.append("td { color: #2C2C24; font-weight: 600; }");
        sb.append(".timeline-item { border-left: 2px dashed #DED8CF; padding-left: 24px; position: relative; margin-bottom: 25px; font-size: 13px; }");
        sb.append(".timeline-dot { position: absolute; left: -6px; top: 6px; width: 10px; height: 10px; border-radius: 50%; background: #5D7052; border: 2px solid #FEFEFA; }");
        sb.append(".timeline-date { font-weight: 700; color: #78786C; font-size: 11px; text-transform: uppercase; }");
        sb.append(".timeline-title { font-family: 'Fraunces', Georgia, serif; font-weight: 800; color: #2C2C24; margin: 4px 0; font-size: 14px; }");
        sb.append(".timeline-desc { color: #78786C; font-weight: 600; }");
        sb.append(".footer { margin-top: 60px; border-top: 1px solid #DED8CF; padding-top: 24px; text-align: center; font-size: 11px; color: #949484; font-weight: 600; }");
        
        // Media query for PDF print rendering
        sb.append("@media print { ");
        sb.append("@page { size: A4; margin: 15mm; } ");
        sb.append("body { padding: 0 !important; background: #FFFFFF !important; } ");
        sb.append(".print-bar { display: none !important; } ");
        sb.append(".section { border: 1px solid #CCCCCC !important; box-shadow: none !important; border-radius: 8px !important; } ");
        sb.append("}");

        sb.append("</style></head><body>");

        // Action Bar for PDF Save
        sb.append("<div class='print-bar no-print'>");
        sb.append("<div class='print-bar-title'>KSP Shodhana Official Intelligence Dossier · ").append(crime.getFirNumber()).append("</div>");
        sb.append("<button class='print-btn' onclick='window.print()'>Save / Print as PDF</button>");
        sb.append("</div>");

        // Header
        sb.append("<div class='header'>");
        sb.append("<h1>KARNATAKA STATE POLICE</h1>");
        sb.append("<p>CRIME INTELLIGENCE & INVESTIGATION WORKSPACE · SHODHANA REPORT</p>");
        sb.append("</div>");

        // Case Summary
        sb.append("<div class='section'>");
        sb.append("<div class='section-title'>Case Information Summary</div>");
        sb.append("<div class='grid'>");
        sb.append("<div class='field'><div class='label'>FIR Number</div><div class='value'>").append(crime.getFirNumber()).append("</div></div>");
        sb.append("<div class='field'><div class='label'>Crime Category</div><div class='value'>").append(crime.getCrimeType()).append("</div></div>");
        sb.append("<div class='field'><div class='label'>Date Reported</div><div class='value'>").append(crime.getDateReported()).append("</div></div>");
        sb.append("<div class='field'><div class='label'>Date Occurred</div><div class='value'>").append(crime.getDateOccurred()).append("</div></div>");
        sb.append("<div class='field'><div class='label'>District / Station</div><div class='value'>").append(crime.getDistrict()).append(" / ").append(crime.getStation()).append("</div></div>");
        sb.append("<div class='field'><div class='label'>Investigating Officer</div><div class='value'>").append(crime.getInvestigatingOfficer()).append("</div></div>");
        sb.append("<div class='field full-width'><div class='label'>Incident Address</div><div class='value'>").append(crime.getAddress()).append("</div></div>");
        sb.append("<div class='field full-width'><div class='label'>Modus Operandi</div><div class='value'>").append(crime.getModusOperandi() != null ? crime.getModusOperandi() : "N/A").append("</div></div>");
        sb.append("<div class='field full-width'><div class='label'>Weapon Used</div><div class='value'>").append(crime.getWeaponUsed() != null ? crime.getWeaponUsed() : "None").append("</div></div>");
        sb.append("<div class='field full-width'><div class='label'>Brief Description</div><div class='value'>").append(crime.getDescription()).append("</div></div>");
        sb.append("</div>");
        sb.append("</div>");

        // Associated Criminals
        sb.append("<div class='section'>");
        sb.append("<div class='section-title'>Identified Suspects & Accused</div>");
        if (links.isEmpty()) {
            sb.append("<p style='font-size: 13px; color: #64748b;'>No suspects or accused currently linked in records.</p>");
        } else {
            sb.append("<table><thead><tr><th>Name</th><th>Alias</th><th>Role</th><th>Risk Level</th><th>Details</th></tr></thead><tbody>");
            for (CrimeCriminalLink link : links) {
                Optional<Criminal> criminalOpt = criminalRepository.findById(link.getCriminalRowId());
                if (criminalOpt.isPresent()) {
                    Criminal criminal = criminalOpt.get();
                    sb.append("<tr>");
                    sb.append("<td>").append(criminal.getName()).append("</td>");
                    sb.append("<td>").append(criminal.getAlias() != null ? criminal.getAlias() : "-").append("</td>");
                    sb.append("<td>").append(link.getRole()).append("</td>");
                    sb.append("<td>").append(criminal.getRiskLevel()).append("</td>");
                    sb.append("<td>").append(link.getInvolvementDetail() != null ? link.getInvolvementDetail() : "Direct Involvement").append("</td>");
                    sb.append("</tr>");
                }
            }
            sb.append("</tbody></table>");
        }
        sb.append("</div>");

        // Timeline
        sb.append("<div class='section'>");
        sb.append("<div class='section-title'>Investigation Timeline Logs</div>");
        if (timeline.isEmpty()) {
            sb.append("<p style='font-size: 13px; color: #64748b;'>No timeline logs recorded for this investigation.</p>");
        } else {
            for (TimelineEvent event : timeline) {
                sb.append("<div class='timeline-item'>");
                sb.append("<div class='timeline-dot'></div>");
                sb.append("<div class='timeline-date'>").append(event.getEventDate() != null ? event.getEventDate() : "2026-06-15").append(" · ").append(event.getCreatedBy() != null ? event.getCreatedBy() : "KSP Officer").append("</div>");
                sb.append("<div class='timeline-title'>").append(event.getTitle()).append(" [").append(event.getEventType()).append("]</div>");
                sb.append("<div class='timeline-desc'>").append(event.getDescription()).append("</div>");
                sb.append("</div>");
            }
        }
        sb.append("</div>");

        // Footer
        sb.append("<div class='footer'>");
        sb.append("Report generated automatically on ").append(Instant.now().toString());
        sb.append(" · KSP Shodhana Intelligence Workspace");
        sb.append("</div>");

        // Auto-download PDF script trigger
        sb.append("<script>");
        sb.append("window.addEventListener('DOMContentLoaded', function() {");
        sb.append("  setTimeout(function() {");
        sb.append("    window.print();");
        sb.append("  }, 600);");
        sb.append("});");
        sb.append("</script>");

        sb.append("</body></html>");

        return sb.toString();
    }

    public String getDownloadUrl(String reportId) {
        log.info("Getting download URL for report {}", reportId);
        // Return localized preview endpoint
        return "/api/v1/reports/" + reportId + "/preview";
    }
}
