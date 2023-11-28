package pl.jutupe.cartogobackend.order.infrastructure

import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import pl.jutupe.cartogobackend.order.domain.model.Order
import pl.jutupe.cartogobackend.order.domain.service.OrderService
import pl.jutupe.cartogobackend.storage.domain.StorageService
import pl.jutupe.cartogobackend.storage.domain.model.DeliveryCustomerSignatureFileResource
import pl.jutupe.cartogobackend.user.domain.UserService
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class FormGenerator(
    private val storageService: StorageService,
    private val userService: UserService,
    private val orderService: OrderService,
) {

    var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    fun createDeliveryForm(order: Order) : ByteArrayResource {
        val out = ByteArrayOutputStream()
        val template = ClassPathResource("deliveryForm.pdf").inputStream
        val document = Document(PdfDocument(PdfReader(template), PdfWriter(out)))
        val pdfForm = PdfAcroForm.getAcroForm(document.pdfDocument, false)
        val font = PdfFontFactory.createFont("ArialCE.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)

        val fields = pdfForm.formFields
        fields["Text1"]?.setValue("Zlecenie ${order.number} - ${order.rental.name}", font, 10f)
        fields["Text2"]?.setValue(order.rental.address.let { "${it.postalCode} ${it.city}, ${it.street}" }, font, 10f)
        fields["Text3"]?.setValue(formatter.format(LocalDateTime.now()), font, 10f)
        fields["Text4"]?.setValue(order.vehicle.let { "${it.name} - ${it.registrationNumber}" }, font, 10f)
        fields["Text5"]?.setValue(order.customer.let { "${it.firstName} ${it.lastName}" }, font, 10f)
        fields["Text6"]?.setValue(order.customer.phoneNumber, font, 10f)
        fields["Text7"]?.setValue(order.delivery?.address?.let {  "${it.postalCode} ${it.city}, ${it.street}" } ?: "-", font, 10f)
        fields["Text8"]?.setValue(order.delivery?.operator?.name ?: "", font, 10f)

        runCatching {
            val path = userService.getSignatureFileResource(order.delivery!!.operator)!!.pathWithName
            val signature = storageService.getFile(path)!!.readBytes()

            document.add(
                Image(ImageDataFactory.create(signature)).apply {
                    setFixedPosition(200f, 100f)
                    scaleToFit(200f, 60f)
                }
            )
        }.onFailure {
            it.printStackTrace()
        }

        runCatching {
            val path = orderService.getDeliveryCustomerSignatureFileResource(order)!!.pathWithName
            val signature = storageService.getFile(path)!!.readBytes()

            document.add(
                Image(ImageDataFactory.create(signature)).apply {
                    setFixedPosition(170f, 190f)
                    scaleToFit(200f, 60f)
                }
            )
        }.onFailure {
            it.printStackTrace()
        }

        template.close()
        document.close()

        val bytes = out.toByteArray()
        out.close()
        return ByteArrayResource(bytes)
    }

    fun createReceptionForm(order: Order) : ByteArrayResource {
        val out = ByteArrayOutputStream()
        val template = ClassPathResource("receptionForm.pdf").inputStream
        val document = Document(PdfDocument(PdfReader(template), PdfWriter(out)))
        val pdfForm = PdfAcroForm.getAcroForm(document.pdfDocument, false)
        val font = PdfFontFactory.createFont("ArialCE.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)

        val fields = pdfForm.formFields
        fields["Text1"]?.setValue("Zlecenie ${order.number} - ${order.rental.name}", font, 10f)
        fields["Text2"]?.setValue(order.rental.address.let { "${it.postalCode} ${it.city}, ${it.street}" }, font, 10f)
        fields["Text3"]?.setValue(formatter.format(LocalDateTime.now()), font, 10f)
        fields["Text4"]?.setValue(order.vehicle.let { "${it.name} - ${it.registrationNumber}" }, font, 10f)
        fields["Text5"]?.setValue(order.customer.let { "${it.firstName} ${it.lastName}" }, font, 10f)
        fields["Text6"]?.setValue(order.customer.phoneNumber, font, 10f)
        fields["Text7"]?.setValue(order.reception?.address?.let {  "${it.postalCode} ${it.city}, ${it.street}" } ?: "-", font, 10f)
        fields["Text8"]?.setValue(order.reception?.operator?.name ?: "", font, 10f)

        runCatching {
            val path = userService.getSignatureFileResource(order.reception!!.operator)!!.pathWithName
            val signature = storageService.getFile(path)!!.readBytes()

            document.add(
                Image(ImageDataFactory.create(signature)).apply {
                    setFixedPosition(200f, 100f)
                    scaleToFit(200f, 60f)
                }
            )
        }.onFailure {
            it.printStackTrace()
        }

        runCatching {
            val path = orderService.getReceptionCustomerSignatureFileResource(order)!!.pathWithName
            val signature = storageService.getFile(path)!!.readBytes()

            document.add(
                Image(ImageDataFactory.create(signature)).apply {
                    setFixedPosition(170f, 190f)
                    scaleToFit(200f, 60f)
                }
            )
        }.onFailure {
            it.printStackTrace()
        }

        template.close()
        document.close()

        val bytes = out.toByteArray()
        out.close()
        return ByteArrayResource(bytes)
    }
}