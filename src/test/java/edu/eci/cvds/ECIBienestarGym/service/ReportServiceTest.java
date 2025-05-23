package edu.eci.cvds.ECIBienestarGym.service;

import edu.eci.cvds.ECIBienestarGym.dto.ReportDTO;
import edu.eci.cvds.ECIBienestarGym.dto.ReportEntryDTO;
import edu.eci.cvds.ECIBienestarGym.dto.UserDTO;
import edu.eci.cvds.ECIBienestarGym.embeddables.ReportEntry;
import edu.eci.cvds.ECIBienestarGym.enums.Role;
import edu.eci.cvds.ECIBienestarGym.model.Report;
import edu.eci.cvds.ECIBienestarGym.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllReports() {
        List<Report> mockReports = Arrays.asList(new Report(), new Report());
        when(reportRepository.findAll()).thenReturn(mockReports);

        List<Report> reports = reportService.getAllReports();

        assertEquals(2, reports.size());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void getReportById() {
        String id = "report123";
        Report mockReport = new Report();
        when(reportRepository.findById(id)).thenReturn(Optional.of(mockReport));

        Report report = reportService.getReportById(id);

        assertEquals(mockReport, report);
        verify(reportRepository, times(1)).findById(id);
    }

    @Test
    void getReportsByDate() {
        LocalDate date = LocalDate.now();
        List<Report> mockReports = Arrays.asList(new Report(), new Report());
        when(reportRepository.findByGeneratedAt(date)).thenReturn(mockReports);

        List<Report> reports = reportService.getReportsByGeneratedAt(date);

        assertEquals(2, reports.size());
        verify(reportRepository, times(1)).findByGeneratedAt(date);
    }

    @Test
    void createReport() {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId("report123");
        reportDTO.setDescription("This is a test report");
        reportDTO.setGeneratedAt(LocalDate.now());
        reportDTO.setCoachId(new UserDTO("user123", "John Doe", "johndoe@example.com", Role.STUDENT));
        reportDTO.setEntries(Arrays.asList(
                new ReportEntryDTO("Session 1", Map.of("asistencias", 5, "objetivosCumplidos", true)),
                new ReportEntryDTO("Session 2", Map.of("asistencias", 3, "objetivosCumplidos", false))
            ));

        Report mockReport = new Report();
        mockReport.setId("report123");
        mockReport.setDescription("This is a test report");
        mockReport.setGeneratedAt(LocalDate.now());

        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report createdReport = reportService.createReport(reportDTO);

        assertEquals(mockReport, createdReport);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void updateReport() {
        String id = "report123";

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId("report123");
        reportDTO.setDescription("This is an updated report");
        reportDTO.setGeneratedAt(LocalDate.now());
        reportDTO.setCoachId(new UserDTO("user123", "John Doe", "johndoe@example.com", Role.STUDENT));
        reportDTO.setEntries(Arrays.asList(
            new ReportEntryDTO("Session 1", Map.of("asistencias", 5, "objetivosCumplidos", true)),
            new ReportEntryDTO("Session 2", Map.of("asistencias", 3, "objetivosCumplidos", false))
        ));

        Report mockReport = new Report();
        mockReport.setId("report123");
        mockReport.setDescription("This is the original report");
        mockReport.setGeneratedAt(LocalDate.now());

        when(reportRepository.findById(id)).thenReturn(Optional.of(mockReport));
        when(reportRepository.save(any(Report.class))).thenReturn(mockReport);

        Report updatedReport = reportService.updateReport(id, reportDTO);

        assertEquals(mockReport, updatedReport);
        verify(reportRepository, times(1)).findById(id);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void deleteReport() {
        String id = "report123";

        Report mockReport = new Report();
        mockReport.setId(id);

        when(reportRepository.findById(id)).thenReturn(Optional.of(mockReport));
        doNothing().when(reportRepository).deleteById(id);

        reportService.deleteReport(id);

        verify(reportRepository, times(1)).findById(id);
        verify(reportRepository, times(1)).deleteById(id);
    }
}