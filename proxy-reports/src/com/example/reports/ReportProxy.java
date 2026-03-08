package com.example.reports;

/**
 * TODO (student):
 * Implement Proxy responsibilities here:
 * - access check
 * - lazy loading
 * - caching of RealReport within the same proxy
 */
public class ReportProxy implements Report {

    private final String reportId;
    private final String title;
    private final String classification;
    private final AccessControl accessControl = new AccessControl();
    private RealReport realReport;

    public ReportProxy(String reportId, String title, String classification) {
        this.reportId = reportId;
        this.title = title;
        this.classification = classification;
    }

    @Override
    public void display(User user) {
        if (!accessControl.canAccess(user, classification)) {
            System.out.println("[access-denied] user=" + user.getName()
                    + " role=" + user.getRole()
                    + " report=" + reportId
                    + " classification=" + classification);
            return;
        }

        if (realReport == null) {
            System.out.println("[proxy] first authorized access, creating RealReport for " + reportId);
            realReport = new RealReport(reportId, title, classification);
        } else {
            System.out.println("[proxy] using cached RealReport for " + reportId);
        }

        realReport.display(user);
    }
}
