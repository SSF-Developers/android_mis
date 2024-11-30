package sukriti.ngo.mis.ui.reports.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.dataModel.dynamo_db.Complex
import sukriti.ngo.mis.repository.data.*
import sukriti.ngo.mis.repository.entity.BwtProfile
import sukriti.ngo.mis.repository.entity.UsageProfile
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

data class UsageReportData(
    var complex: Complex,
    var usageData: ComplexWiseUsageData,
    var usageSummary: UsageSummaryStats,
    var usageComparison: UsageComparisonStats,
    var usageTimeline: UsageComparisonStats,
    var feedbackSummary: FeedbackStatsData,
    var feedbackComparison: FeedbackSummaryData,
    var feedbackDistribution: FeedbackComparisonData,
    var feedbackTimeline: FeedbackTimelineData,
    var collectionSummary: ChargeCollectionStats,
    var collectionComparison: DailyChargeCollectionData,
    var collectionTimeline: DailyChargeCollectionData,
    var upiCollectionSummary: ChargeCollectionStats,
    var upiCollectionComparison: DailyUpiCollectionData,
    var upiCollectionTimeline: DailyUpiCollectionData,
    var bwtSummary: ChargeCollectionStats,
    var bwtCollectionComparison: DailyBwtCollectionData,
    var bwtCollectionTimeline: DailyBwtCollectionData,
    var ticketResolutionTimeline: TicketResolutionTimelineData
    )
