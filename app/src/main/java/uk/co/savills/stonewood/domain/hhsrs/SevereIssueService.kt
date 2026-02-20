package uk.co.savills.stonewood.domain.hhsrs

import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel

interface SevereIssueService {
    fun getIssue(projectId: String, propertyId: Int, elementId: String): HHSRSSevereIssueModel?

    fun saveIssue(issue: HHSRSSevereIssueModel)

    fun reportIssues()
}
