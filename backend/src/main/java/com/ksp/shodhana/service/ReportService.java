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
 * Service layer for PDF report generation.
 * Generates structured, print-ready HTML which can be converted to PDF on the client or server.
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
     * Generates a print-ready HTML intelligence report for an investigation.
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
        sb.append("<style>");
        sb.append("body { font-family: 'Inter', system-ui, sans-serif; color: #1e293b; line-height: 1.5; padding: 40px; background: #fff; }");
        sb.append(".header { text-align: center; border-bottom: 2px solid #1e3a8a; padding-bottom: 20px; margin-bottom: 30px; }");
        sb.append(".header h1 { font-size: 24px; margin: 0; color: #1e3a8a; font-weight: 800; text-transform: uppercase; }");
        sb.append(".header p { margin: 5px 0 0 0; font-size: 12px; color: #64748b; tracking-wider; font-weight: 600; }");
        sb.append(".section { margin-bottom: 30px; }");
        sb.append(".section-title { font-size: 16px; font-weight: 700; color: #1e3a8a; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; margin-bottom: 15px; text-transform: uppercase; }");
        sb.append(".grid { display: grid; grid-template-cols: 1fr 1fr; gap: 15px; margin-bottom: 20px; }");
        sb.append(".field { font-size: 13px; }");
        sb.append(".label { font-weight: 600; color: #64748b; }");
        sb.append(".value { color: #0f172a; margin-top: 2px; }");
        sb.append(".full-width { grid-column: 1 / span 2; }");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 13px; }");
        sb.append("th, td { border: 1px solid #e2e8f0; padding: 10px; text-align: left; }");
        sb.append("th { background: #f8fafc; font-weight: 600; color: #475569; }");
        sb.append(".timeline-item { border-left: 2px solid #cbd5e1; padding-left: 20px; position: relative; margin-bottom: 20px; font-size: 13px; }");
        sb.append(".timeline-dot { position: absolute; left: -6px; top: 5px; width: 10px; height: 10px; border-radius: 50%; background: #1e3a8a; }");
        sb.append(".timeline-date { font-weight: 600; color: #64748b; font-size: 11px; }");
        sb.append(".timeline-title { font-weight: 700; color: #0f172a; margin: 2px 0; }");
        sb.append(".timeline-desc { color: #334155; }");
        sb.append(".footer { margin-top: 50px; border-top: 1px solid #e2e8f0; padding-top: 20px; text-align: center; font-size: 11px; color: #94a3b8; }");
        sb.append("</style></head><body>");

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
                    sb.append("<td>").append(link.getInvolvementDetail()).append("</td>");
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
                sb.append("<div class='timeline-date'>").append(event.getEventDate()).append(" · ").append(event.getCreatedBy()).append("</div>");
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

        sb.append("</body></html>");

        return sb.toString();
    }

    public String getDownloadUrl(String reportId) {
        log.info("Getting download URL for report {}", reportId);
        // MVP: return localized preview endpoint
        return "/api/v1/reports/" + reportId + "/preview";
    }
}
