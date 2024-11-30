package sukriti.ngo.mis.ui.reports.PDF_ReportGeneration


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.*
import com.itextpdf.layout.layout.LayoutArea
import com.itextpdf.layout.layout.LayoutResult
import com.itextpdf.layout.layout.RootLayoutArea
import com.itextpdf.layout.property.*
import com.itextpdf.layout.renderer.DivRenderer
import com.itextpdf.layout.renderer.DocumentRenderer
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure.StateStructure
import sukriti.ngo.mis.ui.reports.data.UsageReportData
import sukriti.ngo.mis.ui.reports.data.UsageReportRawData
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class IText(val context: Context, fileName: String) {
    private val file = File(Environment.getExternalStorageDirectory(), "Download/$fileName.pdf")
    private val pdfDocument = PdfDocument(PdfWriter(file))
    private val width = 1150f //PageSize.Default.width+80
    private val height = 1700f //PageSize.Default.height
    private var document: Document? = null

    fun openPdf() {
        document = Document(pdfDocument, PageSize(width, height))
        document!!.setRenderer(CustomDocumentRenderer(document!!))
    }

    fun frontPage(report: String, states: ArrayList<StateStructure>, dur: String) {
        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.logo_white)
        val canvas = Canvas()
        canvas.drawBitmap(bmp, 0f, 0f, null)
        val icon = convertBmpToImg(bmp)
        icon.scaleToFit(width, 125f)


//        Sukriti with Logo
        val name = Paragraph()
        name.add(icon)
        name.setTextAlignment(TextAlignment.CENTER)
        document?.add(name)


//        Report of
        val drawReport = Paragraph()
        val clientName = SharedPrefsClient(context).getUserDetails().organisation.client
        drawReport.add("ECOMITRA\n$report")
        drawReport.setFontSize(50f)
        drawReport.setBold()
        drawReport.setMarginTop(450f)

        val drawClient = Paragraph()
        drawClient.add(drawReport)
        drawClient.add("\nfor $clientName")
        drawClient.setFontSize(40f)
        drawClient.setTextAlignment(TextAlignment.CENTER)

        document?.add(drawClient)

//        selected Report
        var districtCount: Int = 0
        var cityCount: Int = 0
        var complexCount: Int = 0
        val selectionCount = Paragraph()
        if (states.size > 0)
            for (state in states) {
                selectionCount.add("# " + state.name)
                if (state.districts.size > 0)
                    for (district in state.districts) {
                        districtCount += 1
                        if (district.cities.size > 0)
                            for (city in district.cities) {
                                cityCount += 1
                                if (city.complexes.size > 0)
                                    for (complex in city.complexes) {
                                        complexCount += 1
                                    }
                            }
                    }
                when {
                    districtCount == 0 -> selectionCount.add("( all )")
                    cityCount == 0 -> selectionCount.add("( district: $districtCount )")
                    complexCount == 0 -> selectionCount.add("( district: $districtCount, city: $cityCount )")
                    else -> selectionCount.add("( district: $districtCount, city: $cityCount, complex: $complexCount)")
                }
                districtCount = 0
                cityCount = 0
                complexCount = 0
                selectionCount.add("\n")
            }
        selectionCount.setFontSize(20f)
        selectionCount.setBold()

//        val cSize = width/2
//        table =Table(UnitValue.createPercentArray(floatArrayOf(cSize))).useAllAvailableWidth()
//        table?.addCell(Cell().add(selectionCount))
//        table?.setPadding(10f)
//        table?.setHeight(200f)

        val selectedTop = Paragraph()
        selectedTop.add("Selected Report: \n")
        selectedTop.setBold()
        selectedTop.setFontSize(30f)

        val drawSelection = Paragraph()
        drawSelection.add(selectedTop)
        drawSelection.add("\n")
        drawSelection.add(selectionCount)


//      duration
        val selectedIndex = when (dur) {
            "Today" -> 0
            "Yesterday" -> 1
            "Last 2 days" -> 2
            "Last 7 days" -> 3
            "Last 15 days" -> 4
            "Last 30 days" -> 5
            "Last 90 days" -> 6
            "Last 180 days" -> 7
            "Last 360 days" -> 8
            else -> -1
        }

        val durations = Nomenclature.getDuration(selectedIndex)
        val durationInDate = Nomenclature.getDataDuration(durations)


        val durationPeriod = Paragraph()
        durationPeriod.add(
            "" + convertToCustomFormat(durationInDate.from.toString()) + " - " + convertToCustomFormat(
                durationInDate.to.toString()
            )
        )
        durationPeriod.setFontSize(20f)

        val duration = Paragraph()
        duration.add("Report Duration : \n")
        duration.add(durationPeriod)
        duration.setTextAlignment(TextAlignment.RIGHT)
        duration.setBold()
        duration.setFontSize(30f)


        val detail = Paragraph()
        detail.add(drawSelection)
        detail.add(Tab())
        detail.addTabStops(TabStop(1000F, TabAlignment.RIGHT))
        detail.add(duration)
        detail.setMarginTop(450f)
        document?.add(detail)

        areaBreak()
    }

    fun addHeading(head: String, subHead: String, needMargin: Boolean, context: Context) {

        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.ic_toilet)
        val icon = convertBmpToImg(bmp)
        icon.scaleAbsolute(45f, 45f)
        icon.setMarginLeft(20f)

        val letter = Paragraph(subHead?:"")
        letter.setFontSize(14f)

        val text = Paragraph()
        text.add(head + "\n")
        text.add(letter)
        text.setBold()

        val comp = Paragraph()
        comp.add(icon)
        comp.add(Tab())
        comp.add(text)
        comp.setPadding(5f)
        comp.setFontSize(20f)
        if (needMargin) {
            comp.setMarginTop(20f)
        }
        comp.setBackgroundColor(DeviceRgb(0xA6, 0xCB, 0x0B))
//           comp.setBackgroundColor(ColorConstants.GREEN)
//        comp.setBorderRadius(BorderRadius(20f))
        comp.setBorderTopLeftRadius(BorderRadius(20f))
        comp.setBorderTopRightRadius(BorderRadius(20f))
        document!!.add(comp)
    }

    fun addSubHeading(txt: String) {
        val boldText = Paragraph(txt)
        boldText.setBold()
        boldText.setFontSize(16f)
        boldText.setPadding(5f)
        boldText.setMarginTop(25f)
        document!!.add(boldText)
    }


    private var table: Table? = null


    fun drawGraphicalReportTable(): Table? {
        val cSize = width / 6
        table = Table(
            UnitValue.createPercentArray(
                floatArrayOf(
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize
                )
            )
        ).useAllAvailableWidth()
        return table
    }


    fun drawGraphicalReportTableCell(mData: List<UsageReportRawData>) {

        table?.addCell(
            Cell().add(
                Paragraph("Cabin Type").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
//        table?.addCell(Cell().add(Paragraph("Cabin Count").setItalic().setTextAlignment(TextAlignment.CENTER)))
        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Feedback").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Collection").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("New Ticket").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        for (i in mData.indices) {
            if (mData[i].cabinType == "Total") {
                table?.addCell(
                    Cell().add(
                        Paragraph(mData[i].cabinType).setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
//                table?.addCell(Cell().add(Paragraph((mData[i].cabinCount).toString()).setBold().setTextAlignment(TextAlignment.CENTER)))
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].usage).toString()).setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].feedback).toString()).setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].collection).toString()).setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].raisedTicketCount).toString()).setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                    )
                )
            } else {
                table?.addCell(
                    Cell().add(
                        Paragraph(mData[i].cabinType).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
//                table?.addCell(Cell().add(Paragraph((mData[i].cabinCount).toString()).setTextAlignment(TextAlignment.CENTER)))
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].usage).toString()).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].feedback).toString()).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].collection).toString()).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
                table?.addCell(
                    Cell().add(
                        Paragraph((mData[i].raisedTicketCount).toString()).setTextAlignment(
                            TextAlignment.CENTER
                        )
                    )
                )
            }
        }

        document!!.add(table)
    }

    fun convertBmpToImg(bitmap: Bitmap): Image {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val data: ImageData = ImageDataFactory.create(byteArray)
        return Image(data)
    }

    fun drawImage(bitmap: Bitmap) {
        val img = convertBmpToImg(bitmap)
        img.setHorizontalAlignment(HorizontalAlignment.LEFT)
        img.setMarginLeft(100f)
        img.scaleAbsolute((width / 1.5).toFloat(), ((height / (7)).toFloat()))
        document!!.add(img)
    }


    fun areaBreak() {
        val aB = AreaBreak()
        document?.add(aB)
    }
    fun nextPage(){
        document?.add(AreaBreak(AreaBreakType.NEXT_PAGE))
    }

    fun drawDataReportRow(mData: UsageReportData) {

        //top row
        var cSize = width / 7
        table = Table(
            UnitValue.createPercentArray(
                floatArrayOf(
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize
                )
            )
        ).useAllAvailableWidth()

        table?.addCell(
            Cell().add(
                Paragraph("Date").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Total").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Male WC").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Female WC").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("PD WC").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Male Urinals").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Tickets").setBold().setTextAlignment(TextAlignment.CENTER)
            )
        )
        document?.add(table)

        //second row
        cSize = width / 2
        table = Table(
            UnitValue.createPercentArray(
                floatArrayOf(
                    cSize * 2,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize,
                    cSize
                )
            )
        ).useAllAvailableWidth()
        val _rs = context.getString(R.string.Rs)

        table?.addCell(Cell().add(Paragraph("").setItalic().setTextAlignment(TextAlignment.CENTER)))

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

        table?.addCell(
            Cell().add(
                Paragraph("Usage").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )
        table?.addCell(
            Cell().add(
                Paragraph("Comparison").setItalic().setTextAlignment(TextAlignment.CENTER)
            )
        )

//        rest list
        for (i in mData.usageComparison.female.indices) {
            table?.addCell(
                Cell().add(
                    Paragraph(mData.usageComparison.male[i].date).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + getTotalCount(mData, i)).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph(_rs + getTotalAmount(mData, i)).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.usageComparison.male[i].count).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph(_rs + mData.collectionComparison.male[i].amount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.usageComparison.female[i].count).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph(_rs + mData.collectionComparison.female[i].amount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.usageComparison.pd[i].count).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph(_rs + mData.collectionComparison.pd[i].amount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.usageComparison.mur[i].count).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph(_rs + mData.collectionComparison.mur[i].amount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.ticketResolutionTimeline.total[i].raiseCount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table?.addCell(
                Cell().add(
                    Paragraph("" + mData.ticketResolutionTimeline.total[i].resolveCount).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

        }
        document?.add(table)
    }


    private fun getTotalCount(mData: UsageReportData, position: Int): Int {
        var total = mData.usageComparison.male[position].count
        +mData.usageComparison.female[position].count
        +mData.usageComparison.pd[position].count
        +mData.usageComparison.mur[position].count

        return total
    }

    private fun getTotalAmount(mData: UsageReportData, position: Int): Float {
        var total = mData.collectionComparison.male[position].amount+mData.collectionComparison.female[position].amount+mData.collectionComparison.pd[position].amount+mData.collectionComparison.mur[position].amount

        return total
    }

    fun closePdf() {
        document!!.close()
    }

    fun convertToCustomFormat(dateStr: String?): String {
        val utc = TimeZone.getTimeZone("UTC")
        val sourceFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        val destFormat = SimpleDateFormat("EEE dd-MMM-YYYY")
        sourceFormat.timeZone = utc
        val convertedDate = sourceFormat.parse(dateStr)
        return destFormat.format(convertedDate)
    }

}

internal class CustomDocumentRenderer(document: Document?) :
    DocumentRenderer(document) {
    override fun updateCurrentArea(overflowResult: LayoutResult?): LayoutArea {
        val area = super.updateCurrentArea(overflowResult) // margins are applied on this level
        val newBBox = area.bBox.clone()

        // apply border
        val borderWidths = floatArrayOf(10f, 10f, 10f, 10f)
        newBBox.applyMargins(
            borderWidths[0],
            borderWidths[1], borderWidths[2], borderWidths[3], false
        )

        // this div will be added as a background
        val div = Div()
            .setWidth(newBBox.width)
            .setHeight(newBBox.height)
            .setBorder(SolidBorder(10F))
            .setBorderRadius(BorderRadius(50f))
//            .setBackgroundColor(ColorConstants.BLUE)
        addChild(DivRenderer(div))

        // apply padding
        val paddingWidths = floatArrayOf(20f, 20f, 20f, 20f)
        newBBox.applyMargins(
            paddingWidths[0],
            paddingWidths[1], paddingWidths[2], paddingWidths[3], false
        )

        return RootLayoutArea(area.pageNumber, newBBox).also { currentArea = it }
    }


}