package com.example.emps.dto.report;

public class DocumentReportDto {

    private Long documentId;
    private String documentName;
    private String documentType;
    private String fileName;
    private String fileType;
    private String filePath;

    private Long employeeId;
    private String employeeCode;
    private String employeeName;

    public DocumentReportDto() {
    }

    public DocumentReportDto(Long documentId, String documentName,
                             String documentType, String fileName,
                             String fileType, String filePath,
                             Long employeeId, String employeeCode,
                             String employeeName) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}